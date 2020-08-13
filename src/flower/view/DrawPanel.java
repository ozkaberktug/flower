package flower.view;

import flower.App;
import flower.model.AbstractBlock;
import flower.model.CommandBlock;
import flower.model.IfBlock;
import flower.model.InputBlock;
import flower.model.LabelBlock;
import flower.model.Line;
import flower.model.OutputBlock;
import flower.model.StartBlock;
import flower.model.StopBlock;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

@SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
public class DrawPanel extends JPanel implements Runnable, MouseMotionListener, MouseListener, MouseWheelListener {

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
    public static final Font HEAD_FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);
    public static final Font CODE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    public static final Font COMMENT_FONT = new Font(Font.SERIF, Font.ITALIC, 12);
    public static final BasicStroke BOLD_STROKE = new BasicStroke(TILESIZE / 4.f);
    public static final BasicStroke NORMAL_STROKE = new BasicStroke(2.f);
    public static final BasicStroke DASHED_STROKE = new BasicStroke(1.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4}, 0);
    public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);

    public final App app;
    public String blockToAdd = null;
    public boolean toggleGrids = true;
    public boolean toggleQuality = false;
    public boolean toggleInputProcessing = true;

    private AffineTransform toScreen = new AffineTransform(1, 0, 0, 1, 0, 0);
    private Point2D mouse = null;
    private boolean dragging = false;
    private int click = MouseEvent.NOBUTTON;
    private int mode = NO_OPERATION;
    private final Line createdLine = new Line();
    private AbstractBlock hoveringBlock = null;

    public DrawPanel(App app) {
        super(null, true);
        this.app = app;
        setPreferredSize(PREFERRED_SIZE);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setBorder(BorderFactory.createRaisedBevelBorder());
        setOpaque(true);
        setBackground(BACKGROUND_COLOR);
        createdLine.setGhost(true);
    }

    public void clear() {
        blockToAdd = null;
        toggleGrids = true;
        toScreen = new AffineTransform(1, 0, 0, 1, 0, 0);
        mouse = null;
        dragging = false;
        click = MouseEvent.NOBUTTON;
        createdLine.begin = null;
        createdLine.end = null;
        mode = NO_OPERATION;
        hoveringBlock = null;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        if (toggleQuality) {
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics2D.setFont(HEAD_FONT);
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

        // below fixes the transformation issues
        // Note to myself.. NEVER apply setTransformation directly!
        AffineTransform transform = graphics2D.getTransform();
        transform.translate(toScreen.getTranslateX(), toScreen.getTranslateY());
        transform.scale(toScreen.getScaleX(),toScreen.getScaleY());
        graphics2D.setTransform(transform);


        // draw points
        if (toggleGrids) {
            try {
                Point2D ptULC = toScreen.inverseTransform(new Point2D.Double(0.f, 0.f), null);
                Point2D ptLRC = toScreen.inverseTransform(new Point2D.Double(getWidth(), getHeight()), null);
                for (int i = (int) (ptULC.getX() / TILESIZE) - 1; i < ptLRC.getX() / TILESIZE; i++) {
                    for (int j = (int) (ptULC.getY() / TILESIZE) - 1; j < ptLRC.getY() / TILESIZE; j++) {
                        Rectangle dot = new Rectangle(i * TILESIZE + PADDING, j * TILESIZE + PADDING, TILESIZE - PADDING * 2, TILESIZE - PADDING * 2);
                        graphics2D.fillOval(dot.x, dot.y, dot.width, dot.height);
                        if (mouse != null && hoveringBlock == null) {
                            Rectangle cell = new Rectangle(i * TILESIZE, j * TILESIZE, TILESIZE, TILESIZE);
                            if (cell.contains(mouse)) {
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
        if (mouse != null && mode == DRAW_LINE) createdLine.draw(graphics2D);
        for (Line line : app.project.lines) line.draw(graphics2D);
        graphics2D.setColor(Color.BLACK);
        for (AbstractBlock ab : app.project.blocks) ab.draw(graphics2D);
        graphics2D.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (dragging) return;
        if (toggleInputProcessing && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
            AbstractBlock ab = getBlockType();
            if (ab != null) ab.showDialog(e.getLocationOnScreen());
        }
        if (toggleInputProcessing && e.getButton() == MouseEvent.BUTTON1 && blockToAdd != null) {
            AbstractBlock block;
            Point cellCoords = getCellCoords(mouse);
            switch (blockToAdd) {
                case "START":
                    block = new StartBlock(cellCoords);
                    break;
                case "STOP":
                    block = new StopBlock(cellCoords);
                    break;
                case "COMMAND":
                    block = new CommandBlock(cellCoords);
                    break;
                case "IF":
                    block = new IfBlock(cellCoords);
                    break;
                case "INPUT":
                    block = new InputBlock(cellCoords);
                    break;
                case "OUTPUT":
                    block = new OutputBlock(cellCoords);
                    break;
                case "LABEL":
                    block = new LabelBlock(cellCoords);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            app.project.blocks.add(block);
            app.statusPanel.appendLog(blockToAdd + " added", "Created " + blockToAdd + " [id: " + block.getId() + "] at " + cellCoords.x + ", " + cellCoords.y, StatusPanel.INFO_MSG);
            app.selectPanel.clearSelection();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (click != MouseEvent.NOBUTTON) return;
        click = e.getButton();

        if (toggleInputProcessing && click == MouseEvent.BUTTON1) {  // left mouse click
            AbstractBlock b = getBlockType();   // get the block under the mouse
            for (AbstractBlock ab : app.project.blocks) // clear all the selections
                ab.setSelected(false);
            if (b == null) {    // empty space clicked
                mode = DRAW_LINE;
                createdLine.end = createdLine.begin = getCellCoords(mouse);
            } else {    // block clicked
                mode = DRAG_BLOCK;
                createdLine.end = createdLine.begin = getCellCoords(mouse);
                b.setSelected(true);
            }
        }

        if (toggleInputProcessing && click == MouseEvent.BUTTON3) {  // right mouse click - delete item
            AbstractBlock b = getBlockType();   // get the block under the mouse
            if (b == null) {    // it is a line remove it
                for (Line line : app.project.lines) {
                    if (line.containsInclusive(getCellCoords(mouse))) {
                        app.project.lines.remove(line);
                        app.statusPanel.appendLog("Line removed.", String.format("Line deleted: from (%d, %d) to (%d, %d)", line.begin.x, line.begin.y, line.end.x, line.end.y), StatusPanel.INFO_MSG);
                        break;
                    }
                }
            } else {    // it is a block remove it
                app.project.blocks.remove(b);
                app.statusPanel.appendLog("Block removed.", "Block with id " + b.getId() + " deleted.", StatusPanel.INFO_MSG);
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (click != e.getButton()) return; // only one click at a time
        click = MouseEvent.NOBUTTON;
        dragging = false;

        if (toggleInputProcessing && e.getButton() == MouseEvent.BUTTON1 && mode == DRAW_LINE) {
            Point tmp = getCellCoords(mouse);
            if (tmp.x == createdLine.begin.x || tmp.y == createdLine.begin.y) {
                createdLine.end = tmp;
                if (!createdLine.begin.equals(createdLine.end)) {   // lines should be at least 2 block long

                    Line created = new Line(createdLine.begin, createdLine.end);

                    // check if pt begin/end fall on other lines
                    // and divide the line into two
                    boolean division = true;
                    while (division) {
                        division = false;
                        for (Line line : app.project.lines) {
                            // use exclusive method bc division not required on end points.
                            if (line.containsExclusive(created.begin)) {
                                Line d1 = new Line(line.begin, created.begin);
                                Line d2 = new Line(created.begin, line.end);
                                app.project.lines.add(d1);
                                app.project.lines.add(d2);
                                app.project.lines.remove(line);
                                division = true;
                                break;
                            }
                            if (line.containsExclusive(created.end)) {
                                Line d1 = new Line(line.begin, created.end);
                                Line d2 = new Line(created.end, line.end);
                                app.project.lines.add(d1);
                                app.project.lines.add(d2);
                                app.project.lines.remove(line);
                                division = true;
                                break;
                            }
                        }
                    }

                    app.project.lines.add(created);

                    app.statusPanel.appendLog("Line added.", String.format("Line added: from (%d, %d) to (%d, %d)", createdLine.begin.x, createdLine.begin.y, createdLine.end.x, createdLine.end.y), StatusPanel.INFO_MSG);
                }
            }
        }

        // reset
        if (getBlockType() != null) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        createdLine.begin = createdLine.end = null;
        mode = NO_OPERATION;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (hoveringBlock != null) {
            hoveringBlock.setHovered(false);
            hoveringBlock = null;
        }
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
            if (toggleInputProcessing && mode == DRAW_LINE) {
                Point tmp = getCellCoords(mouse);
                if (tmp.x == createdLine.begin.x || tmp.y == createdLine.begin.y) createdLine.end = tmp;
            }
            if (toggleInputProcessing && mode == DRAG_BLOCK) {
                createdLine.end = getCellCoords(mouse);
                Point d = new Point(createdLine.end.x - createdLine.begin.x, createdLine.end.y - createdLine.begin.y);
                for (AbstractBlock ab : app.project.blocks) {
                    if (ab.isSelected()) {
                        ab.moveTo(d);
//                        for (AbstractBlock other : app.project.blocks) {
//                            if (ab.getId() != other.getId() && ab.getOuterBounds().intersects(other.getOuterBounds())) {
//                                ab.moveTo(new Point(-d.x, -d.y));
//                            }
//                        }
                    }
                }
                createdLine.begin = createdLine.end;
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
            if (hoveringBlock != null) {
                hoveringBlock.setHovered(false);
                hoveringBlock = null;
            }
            AbstractBlock ab = getBlockType();
            if (ab != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ab.setHovered(true);
                hoveringBlock = ab;
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
                if (toScreen.getScaleX() > 0.5) {
                    toScreen.translate(target.getX() - target.getX() * 0.9, target.getY() - target.getY() * 0.9);
                    toScreen.scale(0.9, 0.9);
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }


}
