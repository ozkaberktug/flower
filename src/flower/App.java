package flower;

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
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public class App extends JFrame implements WindowListener, ActionListener {

    public static final String version_string = "0.1.0";

    public Project project = null;
    public DrawPanel drawPanel = null;
    public SelectPanel selectPanel = null;
    public StatusPanel statusPanel = null;
    public ToolbarPanel toolbarPanel = null;
    public Interpreter interpreter = null;

    public App() {
        super("Flower - The Flowchart Designer");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler((th, ex) -> {
            Date date = new Date();
            String home_dir = System.getProperty("user.home");
            String timestamp = String.valueOf(date.getTime());
            String logFilePath = home_dir + File.separator + "err_log-" + timestamp + ".txt";
            File logFile = new File(logFilePath);

            try {
                PrintStream ps = new PrintStream(logFile);
                ps.println(statusPanel.getLog());
                ex.printStackTrace(ps);
                ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message = "<html>" + ex.toString() + " occurred! <br/><br/> This should not be happened. An error log has been saved to <br/> <u>" + logFilePath + "</u><br/><br/>If you send me an email (bkozkan@outlook.com), please attach this file also!";

            JOptionPane.showMessageDialog(null, message, "Exception occurred on " + th.getName(), JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        });
        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        initUI();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        project = new Project(this);
        interpreter = new Interpreter(this);
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
        toolbarPanel = new ToolbarPanel(this);
        contentPanel.add(toolbarPanel, gbcToolbarPanel);
        /**/
        GridBagConstraints gbcEditorPanel = new GridBagConstraints(0, 1, 1, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 8, 0, 8), 0, 0);
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
        GridBagConstraints gbcStatusPanel = new GridBagConstraints(0, 2, 1, 1, 1.f, 0f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
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
                project = new Project(this);
                selectPanel.clearSelection();
                drawPanel.clear();
                statusPanel.clear();
                break;
            case "Open":
                project.showOpenDialog(this);
                break;
            case "Save":
                project.showSaveDialog(this);
                break;
            case "Export":
                project.showExportDialog(this);
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
