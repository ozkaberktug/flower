package flower.view;

import flower.controller.ToolbarPanelController;
import flower.resources.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ToolbarPanel extends JPanel {

    public final ToolbarPanelController controller = new ToolbarPanelController();

    private JLabel runBtn;
    private JLabel stopBtn;
    private JLabel relocateBtn;
    private JLabel gridBtn;
    private JLabel qualityBtn;

    public JLabel getRunBtn() {
        return runBtn;
    }

    public JLabel getStopBtn() {
        return stopBtn;
    }

    public JLabel getRelocateBtn() {
        return relocateBtn;
    }

    public JLabel getGridBtn() {
        return gridBtn;
    }

    public JLabel getQualityBtn() {
        return qualityBtn;
    }

    public ToolbarPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        initComponent();

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "runBtn");
        getActionMap().put("runBtn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runBtn.isEnabled())
                    controller.runSimulation();
            }
        });
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F6"), "stopBtn");
        getActionMap().put("stopBtn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stopBtn.isEnabled())
                    controller.stopSimulation();
            }
        });
    }

    private void initComponent() {
        runBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.RUN));
        runBtn.setToolTipText("Run (F5)");
        runBtn.addMouseListener(controller);

        stopBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.STOP));
        stopBtn.setToolTipText("Stop (F6)");
        stopBtn.setEnabled(false);
        stopBtn.addMouseListener(controller);

        add(Box.createRigidArea(new Dimension(5, 0)));
        add(runBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(stopBtn);

        add(Box.createHorizontalGlue());

        relocateBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.RELOCATE));
        relocateBtn.setToolTipText("Relocate");
        relocateBtn.addMouseListener(controller);

        gridBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.GRIDS_ON));
        gridBtn.setToolTipText("Toggle grids");
        gridBtn.addMouseListener(controller);

        qualityBtn = new JLabel(ResourceManager.getImageIcon(ResourceManager.QUALITY_OFF));
        qualityBtn.setToolTipText("Toggle quality");
        qualityBtn.setEnabled(false);
        qualityBtn.addMouseListener(controller);

        add(qualityBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(relocateBtn);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(gridBtn);
        add(Box.createRigidArea(new Dimension(5, 0)));
    }

}
