package flower.view;

import flower.controller.ToolbarPanelController;
import flower.resources.ResourceManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;

public class ToolbarPanel extends JPanel {

    public final ToolbarPanelController controller = new ToolbarPanelController();

    private JLabel runBtn;
    private JLabel stopBtn;

    public JLabel getRunBtn() {return runBtn;}
    public JLabel getStopBtn() {return stopBtn;}

    public ToolbarPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        initComponent();
    }

    private void initComponent() {

        runBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.RUN_BUTTON));
        runBtn.addMouseListener(controller);

        stopBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.STOP_BUTTON_DISABLED));
        stopBtn.setEnabled(false);
        stopBtn.addMouseListener(controller);

        add(Box.createRigidArea(new Dimension(5, 0)));
        add(runBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(stopBtn);
    }

}
