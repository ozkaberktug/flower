package flower.view;

import flower.controller.ToolbarPanelController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;

public class ToolbarPanel extends JPanel {

    public final ToolbarPanelController controller = new ToolbarPanelController();

    private JButton runBtn;
    private JButton stopBtn;

    public JButton getRunBtn() {return runBtn;}
    public JButton getStopBtn() {return stopBtn;}

    public ToolbarPanel() {
        super(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createEtchedBorder());
        initComponent();
    }

    private void initComponent() {

        runBtn = new JButton("Run");
        runBtn.addActionListener(controller);

        stopBtn = new JButton("Stop");
        stopBtn.addActionListener(controller);
        stopBtn.setEnabled(false);

        add(runBtn);
        add(stopBtn);
    }

}
