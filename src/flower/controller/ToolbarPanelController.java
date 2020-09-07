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
        if (e.getComponent() == App.getInstance().toolbarPanel.getRunBtn() && App.getInstance().toolbarPanel.getRunBtn().isEnabled())
            runSimulation();

            // STOP BUTTON
        else if (e.getComponent() == App.getInstance().toolbarPanel.getStopBtn() && App.getInstance().toolbarPanel.getStopBtn().isEnabled())
            stopSimulation();

            // GRID BUTTON - ON -> OFF
        else if (e.getComponent() == App.getInstance().toolbarPanel.getGridBtn() && App.getInstance().toolbarPanel.getGridBtn().isEnabled()) {
            App.getInstance().drawPanel.getController().setToggleGrids(false);
            App.getInstance().toolbarPanel.getGridBtn().setEnabled(false);
            App.getInstance().toolbarPanel.getGridBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.GRIDS_OFF));

            // GRID BUTTON - OFF -> ON
        } else if (e.getComponent() == App.getInstance().toolbarPanel.getGridBtn() && !App.getInstance().toolbarPanel.getGridBtn().isEnabled()) {
            App.getInstance().drawPanel.getController().setToggleGrids(true);
            App.getInstance().toolbarPanel.getGridBtn().setEnabled(true);
            App.getInstance().toolbarPanel.getGridBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.GRIDS_ON));

            // QUALITY BUTTON - ON -> OFF
        } else if (e.getComponent() == App.getInstance().toolbarPanel.getQualityBtn() && App.getInstance().toolbarPanel.getQualityBtn().isEnabled()) {
            App.getInstance().drawPanel.getController().setToggleQuality(false);
            App.getInstance().toolbarPanel.getQualityBtn().setEnabled(false);
            App.getInstance().toolbarPanel.getQualityBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.QUALITY_OFF));

            // QUALITY BUTTON - OFF -> ON
        } else if (e.getComponent() == App.getInstance().toolbarPanel.getQualityBtn() && !App.getInstance().toolbarPanel.getQualityBtn().isEnabled()) {
            App.getInstance().drawPanel.getController().setToggleQuality(true);
            App.getInstance().toolbarPanel.getQualityBtn().setEnabled(true);
            App.getInstance().toolbarPanel.getQualityBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.QUALITY_ON));

            // RELOCATE BUTTON
        } else if (e.getComponent() == App.getInstance().toolbarPanel.getRelocateBtn()) {
            App.getInstance().drawPanel.getController().relocate();
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        App.getInstance().toolbarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        App.getInstance().toolbarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void clear() {
        App.getInstance().drawPanel.getController().setToggleQuality(false);
        App.getInstance().toolbarPanel.getQualityBtn().setEnabled(false);
        App.getInstance().toolbarPanel.getQualityBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.QUALITY_OFF));
        App.getInstance().drawPanel.getController().setToggleGrids(true);
        App.getInstance().toolbarPanel.getGridBtn().setEnabled(true);
        App.getInstance().toolbarPanel.getGridBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.GRIDS_ON));
    }

    public void runSimulation() {
        App.getInstance().toolbarPanel.getRunBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.RUN));
        App.getInstance().toolbarPanel.getStopBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.STOP));
        App.getInstance().toolbarPanel.getRunBtn().setEnabled(false);
        App.getInstance().toolbarPanel.getStopBtn().setEnabled(true);
        App.getInstance().blockInputProcessing();
        App.getInstance().interpreter.start();
    }

    public void stopSimulation() {
        App.getInstance().toolbarPanel.getRunBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.RUN));
        App.getInstance().toolbarPanel.getStopBtn().setIcon(ResourceManager.getImageIcon(ResourceManager.STOP));
        App.getInstance().interpreter.isRunning = false;
        App.getInstance().interpreter = new Interpreter();
        App.getInstance().enableInputProcessing();
        App.getInstance().toolbarPanel.getRunBtn().setEnabled(true);
        App.getInstance().toolbarPanel.getStopBtn().setEnabled(false);
        for (AbstractBlock block : App.getInstance().project.blocks) block.setProcessing(false);
    }

}
