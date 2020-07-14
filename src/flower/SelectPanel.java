package flower;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectPanel extends JPanel {

    public final App app;
    public boolean toggleInputProcessing = true;
    private JTree tree;

    public SelectPanel(App app) {
        super(new BorderLayout(10, 10));
        this.app = app;
        setMinimumSize(new Dimension(150, 100));
        setPreferredSize(new Dimension(150, 100));
        setBorder(BorderFactory.createTitledBorder("Add to Flow"));
        constructTree();
        add(tree, BorderLayout.CENTER);
        JPanel optPanel = new JPanel();
        JToggleButton gridToggleBtn = new JToggleButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.drawPanel.toggleGrids = !app.drawPanel.toggleGrids;
            }
        });
        gridToggleBtn.setSelected(true);
        gridToggleBtn.setText("G");
        optPanel.add(gridToggleBtn);
        JToggleButton qualityToggleBtn = new JToggleButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.drawPanel.toggleQuality = !app.drawPanel.toggleQuality;
            }
        });
        qualityToggleBtn.setText("Q");
        optPanel.add(qualityToggleBtn);
        add(optPanel, BorderLayout.PAGE_END);

    }

    private void constructTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode elementaryBlocks = new DefaultMutableTreeNode("Elementary Blocks");
        DefaultMutableTreeNode misc = new DefaultMutableTreeNode("Misc");
        DefaultMutableTreeNode nStart = new DefaultMutableTreeNode("START");
        DefaultMutableTreeNode nStop = new DefaultMutableTreeNode("STOP");
        DefaultMutableTreeNode nInput = new DefaultMutableTreeNode("INPUT");
        DefaultMutableTreeNode nOutput = new DefaultMutableTreeNode("OUTPUT");
        DefaultMutableTreeNode nIf = new DefaultMutableTreeNode("IF");
        DefaultMutableTreeNode nCommand = new DefaultMutableTreeNode("COMMAND");
        DefaultMutableTreeNode nLabel = new DefaultMutableTreeNode("LABEL");
        elementaryBlocks.add(nStart);
        elementaryBlocks.add(nStop);
        elementaryBlocks.add(nInput);
        elementaryBlocks.add(nOutput);
        elementaryBlocks.add(nIf);
        elementaryBlocks.add(nCommand);
        misc.add(nLabel);
        root.add(elementaryBlocks);
        root.add(misc);
        tree = new JTree(root);
        tree.setRootVisible(false);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(e -> {
            if (!toggleInputProcessing) return;
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selected != null && selected.isLeaf()) {
                String obj = (String) selected.getUserObject();
                app.drawPanel.blockToAdd = obj;
                app.statusPanel.appendLog(obj + " selected.");
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // for deselection
                if (!toggleInputProcessing) return;
                int row = tree.getRowForLocation(e.getX(), e.getY());
                if (row == -1) { // click on the "empty surface"
                    clearSelection();
                    app.statusPanel.appendLog("Ready.");
                }
            }
        });
    }

    public void clearSelection() {
        tree.clearSelection();
        app.drawPanel.blockToAdd = null;
    }

}