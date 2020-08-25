package flower;

import flower.controller.StatusPanelController;
import flower.model.Project;
import flower.util.Dialogs;
import flower.util.ExceptionHandler;
import flower.util.Interpreter;
import flower.view.DrawPanel;
import flower.view.SelectPanel;
import flower.view.StatusPanel;
import flower.view.ToolbarPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App extends JFrame implements WindowListener, ActionListener {

    public static final String version_string = "0.3.0";
    public static final String about_string = "<html>Version: " + App.version_string + "<br/>This program written by Berktuğ Kaan Özkan<br/>github.com/ozkaberktug</html>";

    public static final Project project = new Project();
    public static final DrawPanel drawPanel = new DrawPanel();
    public static final SelectPanel selectPanel = new SelectPanel();
    public static final StatusPanel statusPanel = new StatusPanel();
    public static final ToolbarPanel toolbarPanel = new ToolbarPanel();
    public static Interpreter interpreter = new Interpreter();
    public static final ExceptionHandler exceptionHandler = new ExceptionHandler();

    private static boolean inputProcessing = true;

    public static void blockInputProcessing() {
        inputProcessing = false;
    }

    public static void enableInputProcessing() {
        inputProcessing = true;
    }

    public static boolean isInputProcessing() {
        return inputProcessing;
    }

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
        initComponent();
        initMenuBar();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        statusPanel.controller.setStatus("Ready", StatusPanelController.INFO);
        statusPanel.controller.pushLog("flower v" + version_string, StatusPanelController.INFO);
    }

    // create and add components to UI
    private void initComponent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        /**/
        GridBagConstraints gbcToolbarPanel = new GridBagConstraints(0, 0, 1, 1, 1.f, 0.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 5, 1, 5), 5, 5);
        contentPanel.add(toolbarPanel, gbcToolbarPanel);
        /**/
        GridBagConstraints gbcEditorPanel = new GridBagConstraints(0, 1, 1, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 1, 5), 0, 0);
        JScrollPane selectPanelScrollPane = new JScrollPane(selectPanel);
        selectPanelScrollPane.setBorder(null);
        selectPanelScrollPane.setMinimumSize(selectPanel.getMinimumSize());
        selectPanelScrollPane.setPreferredSize(selectPanel.getPreferredSize());
        JSplitPane editorPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectPanelScrollPane, drawPanel);
        editorPanel.setOneTouchExpandable(true);
        contentPanel.add(editorPanel, gbcEditorPanel);
        /**/
        GridBagConstraints gbcStatusPanel = new GridBagConstraints(0, 2, 1, 1, 1.f, 0f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 1, 5), 0, 0);
        contentPanel.add(statusPanel, gbcStatusPanel);

        setContentPane(contentPanel);
    }

    private void initMenuBar() {
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
        exportMenuItem.setAccelerator(KeyStroke.getKeyStroke("control E"));
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
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);
        //**//
        JMenuItem gotoMenuItem = new JMenuItem("Go to block");
        gotoMenuItem.setActionCommand("Goto");
        gotoMenuItem.setMnemonic(KeyEvent.VK_G);
        gotoMenuItem.addActionListener(this);
        gotoMenuItem.setToolTipText("Find block by given id.");
        editMenu.add(gotoMenuItem);
        //**//
        JMenuItem findMenuItem = new JMenuItem("Find");
        findMenuItem.setMnemonic(KeyEvent.VK_F);
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke("control F"));
        findMenuItem.addActionListener(this);
        findMenuItem.setToolTipText("Find text occurrences.");
        editMenu.add(findMenuItem);
        //**//
        JMenuItem replaceMenuItem = new JMenuItem("Replace");
        replaceMenuItem.setMnemonic(KeyEvent.VK_R);
        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke("control R"));
        replaceMenuItem.addActionListener(this);
        replaceMenuItem.setToolTipText("Replace texts.");
        editMenu.add(replaceMenuItem);
        //**********//
        JMenu projectMenu = new JMenu("Project");
        projectMenu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(projectMenu);
        //**//
        JMenuItem inputMenuItem = new JMenuItem("Input parameters");
        inputMenuItem.setMnemonic(KeyEvent.VK_I);
        inputMenuItem.addActionListener(this);
        inputMenuItem.setActionCommand("Input");
        inputMenuItem.setToolTipText("Set or view input parameters.");
        projectMenu.add(inputMenuItem);
        //**//
        projectMenu.addSeparator();
        JMenuItem statisticsMenuItem = new JMenuItem("Statistics");
        statisticsMenuItem.setMnemonic(KeyEvent.VK_S);
        statisticsMenuItem.addActionListener(this);
        statisticsMenuItem.setToolTipText("View project statistics.");
        projectMenu.add(statisticsMenuItem);
        //**********//
        menuBar.add(Box.createHorizontalGlue());
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        //**//
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(this);
        aboutMenuItem.setToolTipText("About Flowchart Designer");
        helpMenu.add(aboutMenuItem);
        setJMenuBar(menuBar);
    }

    public void resetApp() {
        project.clear();
        selectPanel.controller.clear();
        drawPanel.controller.clear();
        toolbarPanel.controller.clear();
        setTitle("flower - Untitled");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New":
                if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                    return;
                resetApp();
                statusPanel.controller.pushLog("Created new project.", StatusPanelController.INFO);
                statusPanel.controller.setStatus("Ready", StatusPanelController.INFO);
                break;
            case "Open":
                Dialogs.showOpenDialog(this);
                break;
            case "Save":
                Dialogs.showSaveDialog(this);
                break;
            case "Export":
                Dialogs.showExportDialog();
                break;
            case "Exit":
                windowClosing(null);
                break;
            case "About":
                JOptionPane.showMessageDialog(this, about_string, "About", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Input":
                Dialogs.showInputParamsDialog();
                break;
            case "Statistics":
                Dialogs.showStatisticsDialog();
                break;
            case "Goto":
                Dialogs.showGotoDialog();
                break;
            case "Find":
                Dialogs.showFindDialog();
                break;
            case "Replace":
                Dialogs.showReplaceDialog();
                break;
        }
        System.out.println(e.getActionCommand());
    }

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
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
