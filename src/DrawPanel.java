import javax.swing.*;

public class DrawPanel extends JPanel implements Runnable {

    @Override
    public void addNotify() {
        super.addNotify();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        // TODO: cycle
    }

    public App app = null;

    public DrawPanel(App app) {
        this.app = app;
        setPreferredSize(App.minSizeDim);
    }
}
