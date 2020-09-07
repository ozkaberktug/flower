package flower.controller;

import flower.App;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectPanelController extends MouseAdapter implements TreeSelectionListener {

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!App.getInstance().isInputProcessing()) return;
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) App.getInstance().selectPanel.getTree().getLastSelectedPathComponent();
        if (selected != null && selected.isLeaf()) {
            String obj = (String) selected.getUserObject();
            App.getInstance().drawPanel.controller.setBlockToAdd(obj);
            App.getInstance().statusPanel.controller.setStatus(obj + " selected", StatusPanelController.INFO);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // for deselection
        if (!App.getInstance().isInputProcessing()) return;
        int row = App.getInstance().selectPanel.getTree().getRowForLocation(e.getX(), e.getY());
        if (row == -1) { // click on the "empty surface"
            clear();
            App.getInstance().statusPanel.controller.setStatus("Ready", StatusPanelController.INFO);
        }
    }

    public void clear() {
        App.getInstance().selectPanel.getTree().clearSelection();
        App.getInstance().drawPanel.controller.setBlockToAdd(null);
    }

}
