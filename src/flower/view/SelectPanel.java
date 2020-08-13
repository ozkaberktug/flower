package flower.view;

import flower.App;
import flower.controller.SelectPanelController;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectPanel extends JPanel {

    public static final SelectPanelController controller = new SelectPanelController();
    private JTree tree;

    public SelectPanel() {
        super(new BorderLayout(10, 10));
        setMinimumSize(new Dimension(150, 100));
        setPreferredSize(new Dimension(150, 100));
        setBorder(BorderFactory.createTitledBorder("Add to Flow"));
        constructTree();
        add(tree, BorderLayout.CENTER);
        JPanel optPanel = new JPanel();
        JToggleButton gridToggleBtn = new JToggleButton();
        gridToggleBtn.addActionListener(controller);
        gridToggleBtn.setSelected(true);
        gridToggleBtn.setText("G");
        optPanel.add(gridToggleBtn);
        JToggleButton qualityToggleBtn = new JToggleButton();
        qualityToggleBtn.setText("Q");
        qualityToggleBtn.addActionListener(controller);
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
        DrawPanel.controller.blockToAdd = null;
    }

}
