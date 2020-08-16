package flower.view;

import flower.controller.ToolbarPanelController;
import flower.resources.ResourceManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.Dimension;

public class ToolbarPanel extends JPanel {

    public final ToolbarPanelController controller = new ToolbarPanelController();

    private JLabel runBtn;
    private JLabel stopBtn;
    private JLabel relocateBtn;
    private JLabel gridBtn;
    private JLabel qualityBtn;

    public JLabel getRunBtn() {return runBtn;}
    public JLabel getStopBtn() {return stopBtn;}
    public JLabel getRelocateBtn() { return relocateBtn; }
    public JLabel getGridBtn() { return gridBtn; }
    public JLabel getQualityBtn() { return qualityBtn; }

    public ToolbarPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        initComponent();
    }

    private void initComponent() {
        runBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.RUN));
        runBtn.addMouseListener(controller);

        stopBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.STOP));
        stopBtn.setEnabled(false);
        stopBtn.addMouseListener(controller);

        add(Box.createRigidArea(new Dimension(5, 0)));
        add(runBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(stopBtn);

        add(Box.createHorizontalGlue());

        relocateBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.RELOCATE));
        relocateBtn.addMouseListener(controller);

        gridBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.GRIDS_ON));
        gridBtn.addMouseListener(controller);

        qualityBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.QUALITY_OFF));
        qualityBtn.setEnabled(false);
        qualityBtn.addMouseListener(controller);

        add(gridBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(qualityBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(relocateBtn);
        add(Box.createRigidArea(new Dimension(5, 0)));
    }

}
