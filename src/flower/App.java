package flower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App extends JFrame implements WindowListener, ActionListener {

    public static final String version_string = "ALPHA_2";

    public Project project = new Project();
    public DrawPanel drawPanel = null;
    public SelectPanel selectPanel = null;
    public StatusPanel statusPanel = null;

    public App() {
        super("Flower - The Flowchart Designer");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        initUI();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // create and add components to UI
    private void initUI() {
        JMenuBar menuBar = new JMenuBar();
        //*********//
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        //**//
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.addActionListener(this);
        newMenuItem.setToolTipText("Create a blank project.");
        fileMenu.add(newMenuItem);
        //**//
        fileMenu.addSeparator();
        JMenuItem exportMenuItem = new JMenuItem("Export");
        exportMenuItem.setMnemonic(KeyEvent.VK_E);
        exportMenuItem.addActionListener(this);
        exportMenuItem.setToolTipText("Export as PNG file.");
        fileMenu.add(exportMenuItem);
        fileMenu.addSeparator();
        //**//
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(this);
        exitMenuItem.setToolTipText("Exit the application.");
        fileMenu.add(exitMenuItem);
        //**********//
        menuBar.add(Box.createHorizontalGlue());
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(this);
        aboutMenuItem.setToolTipText("About Flowchart Designer");
        helpMenu.add(aboutMenuItem);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcEditorPanel = new GridBagConstraints(0, 0, 1, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 8, 0, 8), 0, 0);
        drawPanel = new DrawPanel(this);
        selectPanel = new SelectPanel(this);
        JScrollPane selectPanelScrollPane = new JScrollPane(selectPanel);
        selectPanelScrollPane.setBorder(null);
        selectPanelScrollPane.setMinimumSize(selectPanel.getMinimumSize());
        selectPanelScrollPane.setPreferredSize(selectPanel.getPreferredSize());
        JSplitPane editorPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectPanelScrollPane, drawPanel);
        editorPanel.setOneTouchExpandable(true);
        contentPanel.add(editorPanel, gbcEditorPanel);
        /**/
        GridBagConstraints gbcStatusPanel = new GridBagConstraints(0, 1, 1, 1, 1.f, 0f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
        statusPanel = new StatusPanel(this);
        contentPanel.add(statusPanel, gbcStatusPanel);

        setContentPane(contentPanel);
        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New":
                if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    return;
                project = new Project();
                selectPanel.clearSelection();
                drawPanel.clear();
                break;
            case "Export":
                project.exportDialog(this);
                break;
            case "Exit":
                windowClosing(null);
                break;
            case "About":
                JOptionPane.showMessageDialog(this, "<html>Version: " + App.version_string + "<br/>This program written by Berktug Kaan Ozkan<br/>github.com/ozkaberktug</html>", "About", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
        if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;
        setVisible(false);
        dispose();
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) { }

    @Override
    public void windowIconified(WindowEvent e) { }

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }

    @Override
    public void windowDeactivated(WindowEvent e) { }


}
