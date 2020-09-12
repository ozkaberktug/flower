package flower.model.elements;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

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

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter expression:"), codeField, bpCheckBox};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!codeField.getText().isEmpty() && !codeField.getText().matches("\\s+"))
                saveChanges(codeField.getText());
            breakpoint = bpCheckBox.isSelected();
        }
    }

    @Override
    public void normalizeSize() {
        area.width = Math.max((int) (code.length() * 0.8f), 9);
        if (area.width % 2 == 0) area.width++;
    }

}
