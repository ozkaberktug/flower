package flower.view;

import flower.controller.ToolbarPanelController;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;

public class ToolbarPanel extends JPanel {

    public final ToolbarPanelController controller = new ToolbarPanelController();

    private final JButton runBtn = new JButton("Run");;
    private final JButton stopBtn = new JButton("Stop");;

    public ToolbarPanel() {
        super(new FlowLayout(FlowLayout.LEFT));
        runBtn.addActionListener(controller);
        stopBtn.addActionListener(controller);
        stopBtn.setEnabled(false);
        add(runBtn);
        add(stopBtn);
    }

    public JButton getRunBtn() {return runBtn;}
    public JButton getStopBtn() {return stopBtn;}

}
