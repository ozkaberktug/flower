package flower.controller;

import flower.App;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectPanelController extends MouseAdapter implements ActionListener, TreeSelectionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("G")) {
            App.drawPanel.controller.setToggleGrids(!App.drawPanel.controller.isToggleGrids());
        }
        if (e.getActionCommand().equals("Q")) {
            App.drawPanel.controller.setToggleQuality(!App.drawPanel.controller.isToggleQuality());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!App.isInputProcessing()) return;
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) App.selectPanel.getTree().getLastSelectedPathComponent();
        if (selected != null && selected.isLeaf()) {
            String obj = (String) selected.getUserObject();
            App.drawPanel.controller.setBlockToAdd(obj);
            App.statusPanel.appendLog(obj + " selected.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // for deselection
        if (!App.isInputProcessing()) return;
        int row = App.selectPanel.getTree().getRowForLocation(e.getX(), e.getY());
        if (row == -1) { // click on the "empty surface"
            clear();
            App.statusPanel.appendLog("Ready.");
        }
    }

    public void clear() {
        App.selectPanel.getTree().clearSelection();
        App.drawPanel.controller.setBlockToAdd(null);
    }

}
