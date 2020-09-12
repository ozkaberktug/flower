package flower.model.elements;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static flower.view.ViewConstants.CODE_FONT;
import static flower.view.ViewConstants.TILESIZE;

public class CommandBlock extends AbstractBlock {


    public CommandBlock(Point offset) {
        super(COMMAND_BLOCK);
        area = new Rectangle(offset.x, offset.y, 9, 5);
        code = "";
    }

    public CommandBlock(CommandBlock block) {
        super(COMMAND_BLOCK);
        this.code = block.code;
        this.area = block.area;
        this.breakpoint = block.breakpoint;
        this.hovered = block.hovered;
        this.selected = block.selected;
        this.processing = block.processing;
    }

    public CommandBlock() {
        super(COMMAND_BLOCK);
        area = new Rectangle();
        code = "";
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        super.draw(graphics2D);
        int index = 1;
        for (String line : code.split("\\n")) {
            graphics2D.drawString(line, (area.x + 1) * TILESIZE, (area.y + 1 + index) * TILESIZE);
            index++;
        }
    }

    @Override
    public void showDialog() {
        String title = "Block #" + getId();

        JTextArea codeArea = new JTextArea(code, 5, 40);
        codeArea.setFont(CODE_FONT);
        JScrollPane codeScrollPane = new JScrollPane(codeArea);

        JCheckBox bpCheckBox = new JCheckBox("Set breakpoint");
        bpCheckBox.setSelected(breakpoint);

        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(area.width, 1, 100, 1));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(area.height, 1, 100, 1));
        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
        spinnerPanel.add(new JLabel("Width:"));
        spinnerPanel.add(widthSpinner);
        spinnerPanel.add(Box.createGlue());
        spinnerPanel.add(new JLabel("Height:"));
        spinnerPanel.add(heightSpinner);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter statements:"), codeScrollPane, spinnerPanel, bpCheckBox};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            saveChanges(codeArea.getText(), (int) widthSpinner.getValue(), (int) heightSpinner.getValue());
            breakpoint = bpCheckBox.isSelected();
        }
    }

    @Override
    public Shape getShape() {
        return new Rectangle(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);
    }

    @Override
    public Point[] getInputPins() {
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + area.width / 2, area.y - 1);
        return ret;
    }

    @Override
    public Point[] getOutputPins() {
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + area.width / 2, area.y + area.height);
        return ret;
    }

}
