package flower.model.elements;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import static flower.view.ViewConstants.CODE_FONT;

abstract public class ExpressionBlock extends AbstractBlock {

    ExpressionBlock(int type) {
        super(type);
    }

    @Override
    public void showDialog() {
        String title = "Block #" + getId();

        JTextField codeField = new JTextField(code, 40);
        codeField.setFont(CODE_FONT);

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

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter expression:"), codeField, spinnerPanel, bpCheckBox};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            saveChanges(codeField.getText(), (int) widthSpinner.getValue(), (int) heightSpinner.getValue());
            breakpoint = bpCheckBox.isSelected();
        }
    }

//    @Override
//    public void normalizeSize() {
//        area.width = Math.max((int) (code.length() * 0.8f), 9);
//        if (area.width % 2 == 0) area.width++;
//    }

}
