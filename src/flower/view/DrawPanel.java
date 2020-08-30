package flower.view;

import flower.App;
import flower.controller.DrawPanelController;
import flower.model.elements.AbstractBlock;
import flower.model.elements.Line;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import static flower.controller.DrawPanelController.DRAW_LINE;
import static flower.view.ViewConstants.BACKGROUND_COLOR;
import static flower.view.ViewConstants.BOLD_STROKE;
import static flower.view.ViewConstants.HEAD_FONT;
import static flower.view.ViewConstants.PADDING;
import static flower.view.ViewConstants.TILESIZE;

@SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
public class DrawPanel extends JPanel implements Runnable {

    public static final Dimension PREFERRED_SIZE = new Dimension(TILESIZE * 40, TILESIZE * 30);
    public final DrawPanelController controller = new DrawPanelController();

    public DrawPanel() {
        super(null, true);
        setPreferredSize(PREFERRED_SIZE);
        addMouseListener(controller);
        addMouseMotionListener(controller);
        addMouseWheelListener(controller);
        setBorder(BorderFactory.createRaisedBevelBorder());
        setOpaque(true);
        setBackground(BACKGROUND_COLOR);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        if (controller.isToggleQuality()) {
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics2D.setFont(HEAD_FONT);
        graphics2D.setColor(Color.GRAY);
        FontMetrics fm = graphics2D.getFontMetrics();

        // print zoom percent
        String zoomTxt = String.format(" %.0f%% ", controller.getTransform().getScaleX() * 100.f);
        graphics2D.drawString(zoomTxt, 0, getHeight() - fm.getDescent());
        // print cell coords
        if (controller.getMousePos() != null) {
            String cellTxt = String.format(" (%d, %d) ", controller.getCellCoords(controller.getMousePos()).x, controller.getCellCoords(controller.getMousePos()).y);
            graphics2D.drawString(cellTxt, getWidth() - fm.stringWidth(cellTxt), getHeight() - fm.getDescent());
        }
        graphics2D.setColor(Color.BLACK);

        // below fixes the transformation issues
        // Note to myself.. NEVER apply setTransformation directly!
        AffineTransform transform = graphics2D.getTransform();
        transform.translate(controller.getTransform().getTranslateX(), controller.getTransform().getTranslateY());
        transform.scale(controller.getTransform().getScaleX(), controller.getTransform().getScaleY());
        graphics2D.setTransform(transform);

        // draw points
        if (controller.isToggleGrids()) {
            try {
                Point2D ptULC = controller.getTransform().inverseTransform(new Point2D.Double(0.f, 0.f), null);
                Point2D ptLRC = controller.getTransform().inverseTransform(new Point2D.Double(getWidth(), getHeight()), null);
                for (int i = (int) (ptULC.getX() / TILESIZE) - 1; i < ptLRC.getX() / TILESIZE; i++) {
                    for (int j = (int) (ptULC.getY() / TILESIZE) - 1; j < ptLRC.getY() / TILESIZE; j++) {
                        Rectangle dot = new Rectangle(i * TILESIZE + PADDING, j * TILESIZE + PADDING, TILESIZE - PADDING * 2, TILESIZE - PADDING * 2);
                        graphics2D.fillOval(dot.x, dot.y, dot.width, dot.height);
                        if (controller.getMousePos() != null && controller.getHoveringBlock() == null) {
                            Rectangle cell = new Rectangle(i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE);
                            if (cell.contains(controller.getMousePos())) {
                                graphics2D.setColor(Color.GREEN);
                                graphics2D.setStroke(BOLD_STROKE);
                                graphics2D.drawOval(cell.x + PADDING / 2, cell.y + PADDING / 2, cell.width - PADDING, cell.height - PADDING);
                                graphics2D.setColor(Color.BLACK);
                                graphics2D.setStroke(BOLD_STROKE);
                            }
                        }
                    }
                }
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }
        }

        // draw lines and blocks
        if (controller.getMousePos() != null && controller.getMode() == DRAW_LINE) controller.getPen().draw(graphics2D);
        for (Line line : App.project.lines) line.draw(graphics2D);
        graphics2D.setColor(Color.BLACK);
        for (AbstractBlock ab : App.project.blocks) ab.draw(graphics2D);
        graphics2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        do {
            final long minDelay = 40; // ms
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

}
