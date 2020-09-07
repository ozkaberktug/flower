package flower.util;

import flower.App;
import flower.controller.StatusPanelController;
import flower.model.elements.AbstractBlock;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import static flower.view.ViewConstants.CODE_FONT;

public class Dialogs {

    public static void showExportDialog() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
        if (chooser.showDialog(null, "Export") == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(null, "Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    FileOperations.export(chooser.getSelectedFile());
                }
            } else {
                FileOperations.export(chooser.getSelectedFile());
            }
        }
    }

    public static void showOpenDialog(App app) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Flower Projects (*.fp)", "fp"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                app.resetApp();
                FileOperations.open(chooser.getSelectedFile());
                app.setTitle("flower - " + App.getInstance().project.name);
            } else {
                JOptionPane.showMessageDialog(app, "No such file!");
            }
        }
    }

    public static void showSaveDialog(App app) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Flower Projects (*.fp)", "fp"));
        if (chooser.showSaveDialog(app) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(app, "Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    FileOperations.save(chooser.getSelectedFile());
                    app.setTitle("flower - " + App.getInstance().project.name);
                }
            } else {
                FileOperations.save(chooser.getSelectedFile());
                app.setTitle("flower - " + App.getInstance().project.name);
            }
        }
    }

    public static void showStatisticsDialog() {
        int numStartBlock = 0;
        int numStopBlock = 0;
        int numIfBlock = 0;
        int numCommandBlock = 0;
        int numInputBlock = 0;
        int numOutputBlock = 0;
        int numLabelBlock = 0;
        for (AbstractBlock ab : App.getInstance().project.blocks) {
            switch (ab.getType()) {
                case AbstractBlock.COMMAND_BLOCK:
                    numCommandBlock++;
                    break;
                case AbstractBlock.IF_BLOCK:
                    numIfBlock++;
                    break;
                case AbstractBlock.START_BLOCK:
                    numStartBlock++;
                    break;
                case AbstractBlock.STOP_BLOCK:
                    numStopBlock++;
                    break;
                case AbstractBlock.INPUT_BLOCK:
                    numInputBlock++;
                    break;
                case AbstractBlock.OUTPUT_BLOCK:
                    numOutputBlock++;
                    break;
                case AbstractBlock.LABEL_BLOCK:
                    numLabelBlock++;
                    break;
            }
        }

        String title = "Project Statistics";
        String[] columnNames = {"Element Type", "Number"};
        Object[][] data = {
                {"START", numStartBlock},
                {"STOP", numStopBlock},
                {"COMMAND", numCommandBlock},
                {"IF", numIfBlock},
                {"INPUT", numInputBlock},
                {"OUTPUT", numOutputBlock},
                {"LABEL", numLabelBlock},
                {"Lines", App.getInstance().project.lines.size()},
        };
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(table);
        final JComponent[] inputs = new JComponent[]{scrollPane};
        JOptionPane.showMessageDialog(null, inputs, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void showInputParamsDialog() {
        String title = "Input Parameters";
        JTextArea codeArea = new JTextArea(App.getInstance().project.inputParams, 5, 40);
        codeArea.setFont(CODE_FONT);
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        final JComponent[] inputs = new JComponent[]{new JLabel("Enter input parameters:"), codeScrollPane};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) App.getInstance().project.inputParams = codeArea.getText();
    }

    public static void showGotoDialog() {
        String title = "Go to";

        JTextField field = new JTextField(10);
        field.setFont(CODE_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter block id:"), field};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!field.getText().isEmpty() && field.getText().matches("\\d+")) {
                App.getInstance().drawPanel.getController().locate(Integer.parseInt(field.getText()));
            } else {
                App.getInstance().statusPanel.getController().setStatus("Enter a valid id!", StatusPanelController.ERROR);
            }
        }
    }

    public static void showFindDialog() {
        // todo: find next button may be added
        String title = "Find";

        JTextField field = new JTextField(30);
        field.setFont(CODE_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter the text:"), field};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!field.getText().isEmpty() && !field.getText().matches("\\s+")) {
                for (AbstractBlock block : App.getInstance().project.blocks)
                    if (block.getCode().contains(field.getText()))
                        App.getInstance().drawPanel.getController().locate(block.getId());
                    else
                        App.getInstance().statusPanel.getController().setStatus("There is no match for " + field.getText(), StatusPanelController.INFO);
            } else {
                App.getInstance().statusPanel.getController().setStatus("Enter a valid text!", StatusPanelController.ERROR);
            }
        }
    }

    public static void showReplaceDialog() {
        // todo: replace next button may be added
        String title = "Replace";

        JTextField findField = new JTextField(30);
        findField.setFont(CODE_FONT);
        JTextField replaceField = new JTextField(30);
        replaceField.setFont(CODE_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Find:"), findField, new JLabel("Replace:"), replaceField};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!findField.getText().isEmpty() && !findField.getText().matches("\\s+")) {
                for (AbstractBlock block : App.getInstance().project.blocks)
                    if (block.getCode().contains(findField.getText()))
                        block.setCode(block.getCode().replace(findField.getText(), replaceField.getText()));
                    else
                        App.getInstance().statusPanel.getController().setStatus("There is no match for " + findField.getText(), StatusPanelController.INFO);
            } else {
                App.getInstance().statusPanel.getController().setStatus("Enter a valid text!", StatusPanelController.ERROR);
            }
        }
    }
}
