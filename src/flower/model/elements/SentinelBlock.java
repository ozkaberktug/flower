package flower.model.elements;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

abstract public class SentinelBlock extends AbstractBlock {

    SentinelBlock(int type) {
        super(type);
    }

    @Override
    public void showDialog() {
        String title = "Block #" + getId();

        JCheckBox bpCheckBox = new JCheckBox("Set breakpoint");
        bpCheckBox.setSelected(breakpoint);

        int result = JOptionPane.showConfirmDialog(null, bpCheckBox, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) breakpoint = bpCheckBox.isSelected();
    }

    @Override
    public void normalizeSize() { }

}
