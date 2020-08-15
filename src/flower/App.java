package flower;

import flower.controller.StatusPanelController;
import flower.model.Project;
import flower.util.ExceptionHandler;
import flower.util.FileHandler;
import flower.util.Interpreter;
import flower.view.DrawPanel;
import flower.view.SelectPanel;
import flower.view.StatusPanel;
import flower.view.ToolbarPanel;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App extends JFrame implements WindowListener, ActionListener {

    public static final String version_string = "0.2.0";

    public static final Project project = new Project();
    public static final DrawPanel drawPanel = new DrawPanel();
    public static final SelectPanel selectPanel = new SelectPanel();
    public static final StatusPanel statusPanel = new StatusPanel();
    public static final ToolbarPanel toolbarPanel = new ToolbarPanel();
    public static Interpreter interpreter = new Interpreter();
    private static final ExceptionHandler exceptionHandler = new ExceptionHandler();

    private static boolean inputProcessing = true;
    public static void blockInputProcessing() { inputProcessing = false; }
    public static void enableInputProcessing() {inputProcessing = true;}
    public static boolean isInputProcessing() {return inputProcessing;}

    public App() {
        super("flower - Untitled");
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            exceptionHandler.handle(e, ExceptionHandler.NORMAL);
        }
        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        initUI();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        statusPanel.controller.setStatus("Ready", StatusPanelController.INFO);
        statusPanel.controller.pushLog("flower v" + version_string, StatusPanelController.INFO);
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
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.addActionListener(this);
        openMenuItem.setToolTipText("Open a project.");
        fileMenu.add(openMenuItem);
        //**//
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.addActionListener(this);
        saveMenuItem.setToolTipText("Save a project.");
        fileMenu.add(saveMenuItem);
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
        /**/
        GridBagConstraints gbcToolbarPanel = new GridBagConstraints(0, 0, 1, 1, 1.f, 0.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        contentPanel.add(toolbarPanel, gbcToolbarPanel);
        /**/
        GridBagConstraints gbcEditorPanel = new GridBagConstraints(0, 1, 1, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 8, 0, 8), 0, 0);
        JScrollPane selectPanelScrollPane = new JScrollPane(selectPanel);
        selectPanelScrollPane.setBorder(null);
        selectPanelScrollPane.setMinimumSize(selectPanel.getMinimumSize());
        selectPanelScrollPane.setPreferredSize(selectPanel.getPreferredSize());
        JSplitPane editorPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectPanelScrollPane, drawPanel);
        editorPanel.setOneTouchExpandable(true);
        contentPanel.add(editorPanel, gbcEditorPanel);
        /**/
        GridBagConstraints gbcStatusPanel = new GridBagConstraints(0, 2, 1, 1, 1.f, 0f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
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
                project.clear();
                selectPanel.controller.clear();
                drawPanel.controller.clear();
                setTitle("flower - Untitled");
                statusPanel.controller.pushLog("Created new project.", StatusPanelController.INFO);
                statusPanel.controller.setStatus("Ready", StatusPanelController.INFO);
                break;
            case "Open":
                FileHandler.showOpenDialog(this);
                break;
            case "Save":
                FileHandler.showSaveDialog(this);
                break;
            case "Export":
                FileHandler.showExportDialog();
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
        statusPanel.controller.pushLog("Exiting application...", StatusPanelController.INFO);
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
