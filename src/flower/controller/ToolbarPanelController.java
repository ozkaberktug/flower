package flower.controller;

import flower.App;
import flower.model.elements.AbstractBlock;
import flower.util.Interpreter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolbarPanelController implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Run")) {
            App.toolbarPanel.getRunBtn().setEnabled(false);
            App.toolbarPanel.getStopBtn().setEnabled(true);
            App.blockInputProcessing();
            App.interpreter.start();
        }
        if (e.getActionCommand().equals("Stop")) {
            App.interpreter.isRunning = false;
            App.interpreter = new Interpreter();
            App.enableInputProcessing();
            App.toolbarPanel.getRunBtn().setEnabled(true);
            App.toolbarPanel.getStopBtn().setEnabled(false);
            for(AbstractBlock block : App.project.blocks) block.setProcessing(false);
        }
    }

}
