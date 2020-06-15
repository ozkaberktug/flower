import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class App extends JFrame implements WindowListener {

    public static final String version_string = "ALPHA_1";
    public static final Dimension minSizeDim = new Dimension(400, 300);

    public static DrawPanel drawPanel = null;

    public App() {
        super();
        configureUI();
        initUI();
    }

    private void initUI() {
        drawPanel = new DrawPanel(this);
        pack();
    }

    private void configureUI() {
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
    public void windowActivated(WindowEvent e) {    }

    @Override
    public void windowDeactivated(WindowEvent e) { }
}
