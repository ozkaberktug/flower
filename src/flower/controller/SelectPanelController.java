package flower.controller;

import flower.view.DrawPanel;
import flower.view.StatusPanel;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectPanelController implements ActionListener, TreeSelectionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("G")) {
            DrawPanel.controller.setToggleGrids(!DrawPanel.controller.isToggleGrids());
        }
        if (e.getActionCommand().equals("Q")) {
            DrawPanel.controller.setToggleQuality(!DrawPanel.controller.isToggleQuality());
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!toggleInputProcessing) return;
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selected != null && selected.isLeaf()) {
            String obj = (String) selected.getUserObject();
            DrawPanel.blockToAdd = obj;
            StatusPanel.appendLog(obj + " selected.");
        }
    }

}
