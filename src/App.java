import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App extends JFrame implements WindowListener {

    public static final String version_string = "ALPHA_1";
    public static final Dimension minSizeDim = new Dimension(400, 300);

    public DrawPanel drawPanel = null;

    public App() {
        super();
        addUI();
        setUI();
    }

    // create and add components to UI
    private void addUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbcDrawPanel = new GridBagConstraints(0, 0, 1, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        drawPanel = new DrawPanel(this);
        add(drawPanel, gbcDrawPanel);
        pack();
    }

    // setup configurations
    private void setUI() {
        setTitle("Flower - The Flowchart Designer");
        setMinimumSize(minSizeDim);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(this);
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
        // TODO handle closing operations
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
