package flower.controller;

import flower.App;
import flower.model.elements.AbstractBlock;
import flower.resources.ResourceManager;
import flower.util.Interpreter;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolbarPanelController extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent() == App.toolbarPanel.getRunBtn() && App.toolbarPanel.getRunBtn().isEnabled())
            runSimulation();
        if (e.getComponent() == App.toolbarPanel.getStopBtn() && App.toolbarPanel.getStopBtn().isEnabled())
            stopSimulation();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        App.toolbarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        App.toolbarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void runSimulation() {
        App.toolbarPanel.getRunBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.RUN_BUTTON_DISABLED));
        App.toolbarPanel.getStopBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.STOP_BUTTON));
        App.toolbarPanel.getRunBtn().setEnabled(false);
        App.toolbarPanel.getStopBtn().setEnabled(true);
        App.blockInputProcessing();
        App.interpreter.start();
    }

    public void stopSimulation() {
        App.toolbarPanel.getRunBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.RUN_BUTTON));
        App.toolbarPanel.getStopBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.STOP_BUTTON_DISABLED));
        App.interpreter.isRunning = false;
        App.interpreter = new Interpreter();
        App.enableInputProcessing();
        App.toolbarPanel.getRunBtn().setEnabled(true);
        App.toolbarPanel.getStopBtn().setEnabled(false);
        for (AbstractBlock block : App.project.blocks) block.setProcessing(false);
    }

}
