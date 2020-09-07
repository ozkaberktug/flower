package flower.util;

import flower.App;
import flower.controller.StatusPanelController;
import flower.model.elements.AbstractBlock;
import flower.model.elements.ChartElement;
import flower.resources.ResourceManager;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import static flower.view.ViewConstants.CODE_FONT;

public class Dialogs {

    public static void showExportDialog() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
        if (chooser.showDialog(App.getInstance(), "Export") == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(App.getInstance(), "Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    FileOperations.export(chooser.getSelectedFile());
                }
            } else {
                FileOperations.export(chooser.getSelectedFile());
            }
        }
    }

    public static void showOpenDialog() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Flower Projects (*.fp)", "fp"));
        if (chooser.showOpenDialog(App.getInstance()) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                App.getInstance().resetApp();
                FileOperations.open(chooser.getSelectedFile());
                App.getInstance().setTitle("flower - " + App.getInstance().project.name);
            } else {
                JOptionPane.showMessageDialog(App.getInstance(), "No such file!");
            }
        }
    }

    public static void showSaveDialog() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Flower Projects (*.fp)", "fp"));
        if (chooser.showSaveDialog(App.getInstance()) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(App.getInstance(), "Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    FileOperations.save(chooser.getSelectedFile());
                    App.getInstance().setTitle("flower - " + App.getInstance().project.name);
                }
            } else {
                FileOperations.save(chooser.getSelectedFile());
                App.getInstance().setTitle("flower - " + App.getInstance().project.name);
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
                case ChartElement.COMMAND_BLOCK:
                    numCommandBlock++;
                    break;
                case ChartElement.IF_BLOCK:
                    numIfBlock++;
                    break;
                case ChartElement.START_BLOCK:
                    numStartBlock++;
                    break;
                case ChartElement.STOP_BLOCK:
                    numStopBlock++;
                    break;
                case ChartElement.INPUT_BLOCK:
                    numInputBlock++;
                    break;
                case ChartElement.OUTPUT_BLOCK:
                    numOutputBlock++;
                    break;
                case ChartElement.LABEL_BLOCK:
                    numLabelBlock++;
                    break;
            }
        }

        String title = "Project Statistics";
        String[] columnNames = {"Element Type", "Number"};
        Object[][] data = {{"START", numStartBlock}, {"STOP", numStopBlock}, {"COMMAND", numCommandBlock}, {"IF", numIfBlock}, {"INPUT", numInputBlock}, {"OUTPUT", numOutputBlock}, {"LABEL", numLabelBlock}, {"Lines", App.getInstance().project.lines.size()},};
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(table);
        final JComponent[] inputs = new JComponent[]{scrollPane};
        JOptionPane.showMessageDialog(App.getInstance(), inputs, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void showInputParamsDialog() {
        String title = "Input Parameters";
        JTextArea codeArea = new JTextArea(App.getInstance().project.inputParams, 5, 40);
        codeArea.setFont(CODE_FONT);
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        final JComponent[] inputs = new JComponent[]{new JLabel("Enter input parameters:"), codeScrollPane};
        int result = JOptionPane.showConfirmDialog(App.getInstance(), inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) App.getInstance().project.inputParams = codeArea.getText();
    }

    public static void showGotoDialog() {
        String title = "Go to";

        JTextField field = new JTextField(10);
        field.setFont(CODE_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter block id:"), field};
        int result = JOptionPane.showConfirmDialog(App.getInstance(), inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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
        int result = JOptionPane.showConfirmDialog(App.getInstance(), inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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
        int result = JOptionPane.showConfirmDialog(App.getInstance(), inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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

    public static void showAboutDialog() {
        try {
            JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setPage(ResourceManager.ABOUT_PAGE);
            JScrollPane editorScrollPane = new JScrollPane(editorPane);
            editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            editorScrollPane.setPreferredSize(new Dimension(400, 500));
            JOptionPane.showMessageDialog(App.getInstance(), editorScrollPane, "About", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            App.getInstance().exceptionHandler.handle(e, ExceptionHandler.NORMAL);
        }
    }

}
