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
        stopBtn.setEnabled(false);
        add(runBtn);
        add(stopBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Run")) {
            if (!app.interpreter.isRunning) runBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            app.drawPanel.toggleInputProcessing = false;
            app.selectPanel.toggleInputProcessing = false;
            app.interpreter.start();
        }
        if (e.getActionCommand().equals("Stop")) {
            app.interpreter.interrupt();
            try {
                app.interpreter.join();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            app.interpreter = new Interpreter(app);
            app.drawPanel.toggleInputProcessing = true;
            app.selectPanel.toggleInputProcessing = true;
            runBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }
    }

}
