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

        // RUN BUTTON
        if (e.getComponent() == App.toolbarPanel.getRunBtn() && App.toolbarPanel.getRunBtn().isEnabled())
            runSimulation();

            // STOP BUTTON
        else if (e.getComponent() == App.toolbarPanel.getStopBtn() && App.toolbarPanel.getStopBtn().isEnabled())
            stopSimulation();

            // GRID BUTTON - ON -> OFF
        else if (e.getComponent() == App.toolbarPanel.getGridBtn() && App.toolbarPanel.getGridBtn().isEnabled()) {
            App.drawPanel.controller.setToggleGrids(false);
            App.toolbarPanel.getGridBtn().setEnabled(false);
            App.toolbarPanel.getGridBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.GRIDS_OFF));

            // GRID BUTTON - OFF -> ON
        } else if (e.getComponent() == App.toolbarPanel.getGridBtn() && !App.toolbarPanel.getGridBtn().isEnabled()) {
            App.drawPanel.controller.setToggleGrids(true);
            App.toolbarPanel.getGridBtn().setEnabled(true);
            App.toolbarPanel.getGridBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.GRIDS_ON));

            // QUALITY BUTTON - ON -> OFF
        } else if (e.getComponent() == App.toolbarPanel.getQualityBtn() && App.toolbarPanel.getQualityBtn().isEnabled()) {
            App.drawPanel.controller.setToggleQuality(false);
            App.toolbarPanel.getQualityBtn().setEnabled(false);
            App.toolbarPanel.getQualityBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.QUALITY_OFF));

            // QUALITY BUTTON - OFF -> ON
        } else if (e.getComponent() == App.toolbarPanel.getQualityBtn() && !App.toolbarPanel.getQualityBtn().isEnabled()) {
            App.drawPanel.controller.setToggleQuality(true);
            App.toolbarPanel.getQualityBtn().setEnabled(true);
            App.toolbarPanel.getQualityBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.QUALITY_ON));

            // RELOCATE BUTTON
        } else if (e.getComponent() == App.toolbarPanel.getRelocateBtn()) {
            App.drawPanel.controller.relocate();
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        App.toolbarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        App.toolbarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void clear() {
        App.drawPanel.controller.setToggleQuality(false);
        App.toolbarPanel.getQualityBtn().setEnabled(false);
        App.toolbarPanel.getQualityBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.QUALITY_OFF));
        App.drawPanel.controller.setToggleGrids(true);
        App.toolbarPanel.getGridBtn().setEnabled(true);
        App.toolbarPanel.getGridBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.GRIDS_ON));
    }

    public void runSimulation() {
        App.toolbarPanel.getRunBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.RUN));
        App.toolbarPanel.getStopBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.STOP));
        App.toolbarPanel.getRunBtn().setEnabled(false);
        App.toolbarPanel.getStopBtn().setEnabled(true);
        App.blockInputProcessing();
        App.interpreter.start();
    }

    public void stopSimulation() {
        App.toolbarPanel.getRunBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.RUN));
        App.toolbarPanel.getStopBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.STOP));
        App.interpreter.isRunning = false;
        App.interpreter = new Interpreter();
        App.enableInputProcessing();
        App.toolbarPanel.getRunBtn().setEnabled(true);
        App.toolbarPanel.getStopBtn().setEnabled(false);
        for (AbstractBlock block : App.project.blocks) block.setProcessing(false);
    }

}
