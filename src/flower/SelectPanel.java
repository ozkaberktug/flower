package flower;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectPanel extends JPanel {

    public App app;
    private JTree tree;

    public SelectPanel(App app) {
        super(new GridLayout(0, 1));
        this.app = app;
        setMinimumSize(new Dimension(100, 100));
        setPreferredSize(new Dimension(150, 100));
        setBorder(BorderFactory.createTitledBorder("Add to Flow"));
        constructTree();
        add(tree);
    }

    private void constructTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode block = new DefaultMutableTreeNode("Elementary Blocks");
        DefaultMutableTreeNode nStart = new DefaultMutableTreeNode("START");
        DefaultMutableTreeNode nStop = new DefaultMutableTreeNode("STOP");
        DefaultMutableTreeNode nInput = new DefaultMutableTreeNode("INPUT");
        DefaultMutableTreeNode nOutput = new DefaultMutableTreeNode("OUTPUT");
        DefaultMutableTreeNode nIf = new DefaultMutableTreeNode("IF");
        DefaultMutableTreeNode nCommand = new DefaultMutableTreeNode("COMMAND");
        block.add(nStart);
        block.add(nStop);
        block.add(nInput);
        block.add(nOutput);
        block.add(nIf);
        block.add(nCommand);
        root.add(block);
        tree = new JTree(root);
        tree.setRootVisible(false);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selected != null && selected.isLeaf()) {
                app.drawPanel.blockToAdd = (String) selected.getUserObject();
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // for deselection
                int row = tree.getRowForLocation(e.getX(), e.getY());
                if (row == -1) { // click on the "empty surface"
                    clearSelection();
                }
            }
        });
    }

    public void clearSelection() {
        tree.clearSelection();
        app.drawPanel.blockToAdd = null;
    }

}
