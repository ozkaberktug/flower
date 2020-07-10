package flower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolbarPanel extends JPanel implements ActionListener {

    private final App app;

    public JButton runBtn;
    public JButton stopBtn;

    public ToolbarPanel(App app) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.app = app;

        runBtn = new JButton("Run");
        runBtn.addActionListener(this);
        stopBtn = new JButton("Stop");
        stopBtn.addActionListener(this);
        add(runBtn);
        add(stopBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Run")) {
            // todo
        }
        if(e.getActionCommand().equals("Stop")) {
            // todo
        }
    }

}
