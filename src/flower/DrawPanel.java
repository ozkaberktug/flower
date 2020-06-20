package flower;

import flower.blocks.AbstractBlock;
import flower.blocks.Line;
import flower.blocks.StartBlock;
import flower.blocks.StopBlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.function.Predicate;

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

    private Point getCellCoords(Point2D point2D) {
        int hoverY = (int) point2D.getY();
        if (hoverY < 0) hoverY -= TILESIZE;
        int hoverX = (int) point2D.getX();
        if (hoverX < 0) hoverX -= TILESIZE;
        hoverX /= TILESIZE;
        hoverY /= TILESIZE;
        return new Point(hoverX, hoverY);
    }

    private AbstractBlock getBlockType() {
        Point m = getCellCoords(mouse);
        for (AbstractBlock ab : app.project.blocks)
            if (ab.getInnerBounds().contains(m)) return ab;
        return null;
    }

    public static final int TILESIZE = 16;
    public static final int PADDING = 7;
    public static final Dimension PREFERRED_SIZE = new Dimension(TILESIZE * 40, TILESIZE * 30);
    public static final int NO_OPERATION = 0;
    public static final int DRAW_LINE = 1;
    public static final int DRAG_BLOCK = 3;

    public App app = null;
    public String blockToAdd = null;
    private final AffineTransform toScreen = new AffineTransform(1, 0, 0, 1, 0, 0);
    private Point2D mouse = null;
    private boolean dragging = false;
    private int click = MouseEvent.NOBUTTON;
    private Point ptBegin = null;
    private Point ptEnd = null;
    private int mode = NO_OPERATION;

    public DrawPanel(App app) {
        super();
        this.app = app;
        setPreferredSize(PREFERRED_SIZE);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        getInputMap().put(KeyStroke.getKeyStroke("space"), "dump");
        getActionMap().put("dump", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (AbstractBlock ab : app.project.blocks) {

                    System.out.println(ab.isSelected());
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
//        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(new Color(220, 220, 220));
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        graphics2D.setColor(Color.GRAY);
        FontMetrics fm = graphics2D.getFontMetrics();
        // print zoom percent
        String zoomTxt = String.format(" %.0f%% ", toScreen.getScaleX() * 100.f);
        graphics2D.drawString(zoomTxt, 0, getHeight() - fm.getDescent());
        // print cell coords
        if (mouse != null) {
            String cellTxt = String.format(" (%d, %d) ", getCellCoords(mouse).x, getCellCoords(mouse).y);
            graphics2D.drawString(cellTxt, getWidth() - fm.stringWidth(cellTxt), getHeight() - fm.getDescent());
        }
        graphics2D.setColor(Color.BLACK);
        graphics2D.setTransform(toScreen);
        final BasicStroke normalStroke = new BasicStroke(1.f);
        final BasicStroke boldStroke = new BasicStroke(TILESIZE / 4.f);

        // draw points
        try {
            Point2D ptULC = toScreen.inverseTransform(new Point2D.Double(0.f, 0.f), null);
            Point2D ptLRC = toScreen.inverseTransform(new Point2D.Double(getWidth(), getHeight()), null);
            for (int i = (int) (ptULC.getX() / TILESIZE) - 1; i < ptLRC.getX() / TILESIZE; i++) {
                for (int j = (int) (ptULC.getY() / TILESIZE) - 1; j < ptLRC.getY() / TILESIZE; j++) {
                    Rectangle dot = new Rectangle(i * TILESIZE + PADDING, j * TILESIZE + PADDING, TILESIZE - PADDING * 2, TILESIZE - PADDING * 2);
                    graphics2D.fillOval(dot.x, dot.y, dot.width, dot.height);
                    if (mouse != null) {
                        Rectangle cell = new Rectangle(i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE);
                        if (cell.contains(mouse)) {
                            graphics2D.setColor(Color.GREEN);
                            graphics2D.setStroke(boldStroke);
                            graphics2D.drawOval(cell.x + PADDING / 2, cell.y + PADDING / 2, cell.width - PADDING, cell.height - PADDING);
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.setStroke(boldStroke);
                        }
                    }
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

        // draw lines
        graphics2D.setStroke(boldStroke);
        if (mouse != null && mode == DRAW_LINE) {
            graphics2D.setColor(Color.GRAY);
            Line line = new Line(ptBegin, ptEnd);
            line.draw(graphics2D);
        }
        graphics2D.setColor(Color.BLACK);
        for (Line line : app.project.lines) {
            line.draw(graphics2D);
        }

        graphics2D.setStroke(normalStroke);
        for (AbstractBlock ab : app.project.blocks) {
            ab.draw(graphics2D);
        }

        graphics2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (dragging) return;
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
            AbstractBlock ab = getBlockType();
            if (ab != null) ab.showDialog();
        }
        if (e.getButton() == MouseEvent.BUTTON1 && blockToAdd != null) {
            switch (blockToAdd) {
                case "START" -> app.project.blocks.add(new StartBlock(getCellCoords(mouse)));
                case "STOP" -> app.project.blocks.add(new StopBlock(getCellCoords(mouse)));
            }
            app.selectPanel.clearSelection();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (click != MouseEvent.NOBUTTON) return;
        click = e.getButton();

        if (click == MouseEvent.BUTTON1) {  // left mouse click
            AbstractBlock b = getBlockType();   // get the block under the mouse
            for (AbstractBlock ab : app.project.blocks) // clear all the selections
                ab.setSelected(false);
            if (b == null) {    // empty space clicked
                mode = DRAW_LINE;
                ptEnd = ptBegin = getCellCoords(mouse);

            } else {    // block clicked
                mode = DRAG_BLOCK;
                ptEnd = ptBegin = getCellCoords(mouse);
                b.setSelected(true);
            }
        }

        if (click == MouseEvent.BUTTON3) {  // right mouse click - delete item
            AbstractBlock b = getBlockType();   // get the block under the mouse
            if (b == null) {    // there is a line remove it
                for (Line line : app.project.lines) {
                    if (line.contains(getCellCoords(mouse))) {
                        app.project.lines.remove(line);
                        break;
                    }
                }
            } else {    // it is a block remove it
                app.project.blocks.remove(b);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (click != e.getButton()) return; // only one click at a time
        click = MouseEvent.NOBUTTON;
        dragging = false;

        if (e.getButton() == MouseEvent.BUTTON1 && mode == DRAW_LINE) {
            Point tmp = getCellCoords(mouse);
            if (tmp.x == ptBegin.x || tmp.y == ptBegin.y) {
                ptEnd = tmp;
                if (!ptBegin.equals(ptEnd)) app.project.lines.add(new Line(ptBegin, ptEnd));
            }
        }

        if (getBlockType() != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        ptBegin = ptEnd = null;
        mode = NO_OPERATION;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        for (AbstractBlock b : app.project.blocks) b.setHovered(false);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        dragging = true;
        try {
            Point2D target = toScreen.inverseTransform(mouseEvent.getPoint(), null);
            Point2D dist = new Point2D.Double(target.getX() - mouse.getX(), target.getY() - mouse.getY());
            if (click == MouseEvent.BUTTON2) {
                toScreen.translate(dist.getX(), dist.getY());
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            mouse = toScreen.inverseTransform(mouseEvent.getPoint(), null);
            if (mode == DRAW_LINE) {
                Point tmp = getCellCoords(mouse);
                if (tmp.x == ptBegin.x || tmp.y == ptBegin.y) ptEnd = tmp;
            }
            if (mode == DRAG_BLOCK) {
                ptEnd = getCellCoords(mouse);
                Point d = new Point(ptEnd.x - ptBegin.x, ptEnd.y - ptBegin.y);
                for (AbstractBlock ab : app.project.blocks)
                    if (ab.isSelected()) ab.moveTo(d);
                ptBegin = ptEnd;
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        try {
            mouse = toScreen.inverseTransform(mouseEvent.getPoint(), null);
            for (AbstractBlock b : app.project.blocks) b.setHovered(false);
            AbstractBlock ab = getBlockType();
            if (ab != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ab.setHovered(true);
            } else if (blockToAdd != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
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
