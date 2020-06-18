import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class DrawPanel extends JPanel implements Runnable, MouseMotionListener, MouseListener, MouseWheelListener {

    @Override
    public void addNotify() {
        super.addNotify();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        do {
            final long minDelay = 20; // ms
            long st = System.currentTimeMillis();
            repaint();
            long diff = System.currentTimeMillis() - st;
            long delay = minDelay - diff;
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (true);
    }

    private Point getCellCoords() {
        int hoverY = (int) mouse.getY();
        if (hoverY < 0) hoverY -= TILESIZE;
        int hoverX = (int) mouse.getX();
        if (hoverX < 0) hoverX -= TILESIZE;
        hoverX /= TILESIZE;
        hoverY /= TILESIZE;
        return new Point(hoverX, hoverY);
    }

    public static final int TILESIZE = 16;
    public static final Dimension PREFERRED_SIZE = new Dimension(TILESIZE * 40, TILESIZE * 30);

    public App app = null;
    private final AffineTransform toScreen = new AffineTransform(1, 0, 0, 1, 0, 0);
    private Point2D mouse = null;
    private int click = MouseEvent.NOBUTTON;

    public DrawPanel(App app) {
        super();
        this.app = app;
        setPreferredSize(PREFERRED_SIZE);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
//        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        graphics2D.setColor(Color.GRAY);
        FontMetrics fm = graphics2D.getFontMetrics();
        // print zoom percent
        String zoomTxt = String.format(" %.0f%% ", toScreen.getScaleX() * 100.f);
        graphics2D.drawString(zoomTxt, 0, getHeight() - fm.getDescent());
        // print mouse coords
        if (mouse != null) {
            String coordsTxt = String.format(" (%.1f, %.1f) ", mouse.getX(), mouse.getY());
            graphics2D.drawString(coordsTxt, getWidth() - fm.stringWidth(coordsTxt), getHeight() - fm.getDescent());
        }
        // print cell coords
        if (mouse != null) {
            String cellTxt = String.format(" (%d, %d) ", getCellCoords().x, getCellCoords().y);
            graphics2D.drawString(cellTxt, getWidth() - fm.stringWidth(cellTxt), getHeight() - fm.getHeight());
        }
        graphics2D.setColor(Color.BLACK);
        graphics2D.setTransform(toScreen);

        // draw points
        try {
            Point2D ptULC = toScreen.inverseTransform(new Point2D.Double(0.f, 0.f), null);
            Point2D ptLRC = toScreen.inverseTransform(new Point2D.Double(getWidth(), getHeight()), null);
            final int padding = 7;
            for (int i = (int) (ptULC.getX() / TILESIZE) - 1; i < ptLRC.getX() / TILESIZE; i++) {
                for (int j = (int) (ptULC.getY() / TILESIZE) - 1; j < ptLRC.getY() / TILESIZE; j++) {
                    Rectangle dot = new Rectangle(i * TILESIZE + padding, j * TILESIZE + padding, TILESIZE - padding * 2, TILESIZE - padding * 2);
                    graphics2D.fillOval(dot.x, dot.y, dot.width, dot.height);
                    if (mouse != null) {
                        Rectangle cell = new Rectangle(i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE);
                        if (cell.contains(mouse)) {
                            graphics2D.setColor(Color.GREEN);
                            graphics2D.setStroke(new BasicStroke(3.f));
                            graphics2D.drawOval(cell.x + padding / 2, cell.y + padding / 2, cell.width - padding, cell.height - padding);
                            graphics2D.setColor(Color.BLACK);
                        }
                    }
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

        graphics2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        if (click != MouseEvent.NOBUTTON) return;
        click = e.getButton();
        if (click == MouseEvent.BUTTON3) {
            // todo popup menu
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (click == e.getButton()) click = MouseEvent.NOBUTTON;
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) {
        mouse = null;
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        try {
            Point2D target = toScreen.inverseTransform(mouseEvent.getPoint(), null);
            Point2D dist = new Point2D.Double(target.getX() - mouse.getX(), target.getY() - mouse.getY());
            if (click == MouseEvent.BUTTON1) toScreen.translate(dist.getX(), dist.getY());
            mouse = toScreen.inverseTransform(mouseEvent.getPoint(), null);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        try {
            mouse = toScreen.inverseTransform(mouseEvent.getPoint(), null);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        try {
            Point2D target = toScreen.inverseTransform(mouseWheelEvent.getPoint(), null);
            if (mouseWheelEvent.getWheelRotation() < 0) { // zoom in
                if (toScreen.getScaleX() < 2) {
                    toScreen.translate(target.getX() - target.getX() * 1.1, target.getY() - target.getY() * 1.1);
                    toScreen.scale(1.1, 1.1);
                }
            } else if (mouseWheelEvent.getWheelRotation() > 0) {// zoom out
                if (toScreen.getScaleX() > 0.7) {
                    toScreen.translate(target.getX() - target.getX() * 0.9, target.getY() - target.getY() * 0.9);
                    toScreen.scale(0.9, 0.9);
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }
}
