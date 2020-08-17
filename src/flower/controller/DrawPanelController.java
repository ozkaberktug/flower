package flower.controller;

import flower.App;
import flower.model.elements.AbstractBlock;
import flower.model.elements.CommandBlock;
import flower.model.elements.IfBlock;
import flower.model.elements.InputBlock;
import flower.model.elements.LabelBlock;
import flower.model.elements.Line;
import flower.model.elements.OutputBlock;
import flower.model.elements.StartBlock;
import flower.model.elements.StopBlock;
import flower.util.Command;

import javax.swing.AbstractAction;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import static flower.view.ViewConstants.TILESIZE;

public class DrawPanelController implements MouseMotionListener, MouseListener, MouseWheelListener {

    public static final int NO_OPERATION = 0;
    public static final int DRAW_LINE = 1;
    public static final int DRAG_BLOCK = 3;

    public final AbstractAction undoAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            App.project.undo();
        }
    };

    public final AbstractAction redoAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            App.project.redo();
        }
    };

    private AffineTransform toScreen = new AffineTransform(1, 0, 0, 1, 0, 0);
    private Point2D mouse = null;
    private boolean dragging = false;
    private int click = MouseEvent.NOBUTTON;
    private int mode = NO_OPERATION;
    private final Line pen = new Line();
    private AbstractBlock hoveringBlock = null;
    private String blockToAdd = null;
    private boolean toggleGrids = true;
    private boolean toggleQuality = false;

    public DrawPanelController() {
        pen.setGhost(true);
    }

    public void setToggleGrids(boolean b) {toggleGrids = b;}
    public void setToggleQuality(boolean b) {toggleQuality = b;}
    public void setBlockToAdd(String block) {blockToAdd = block;}

    public boolean isToggleQuality() {return toggleQuality;}
    public boolean isToggleGrids() {return toggleGrids;}
    public AffineTransform getTransform() {return toScreen;}
    public Point2D getMousePos() {return mouse;}
    public AbstractBlock getHoveringBlock() {return hoveringBlock;}
    public int getMode() {return mode;}
    public Line getPen() {return pen;}

    public Point getCellCoords(Point2D point2D) {
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
        for (AbstractBlock ab : App.project.blocks)
            if (ab.getInnerBounds().contains(m)) return ab;
        return null;
    }

    public void clear() {
        blockToAdd = null;
        toggleGrids = true;
        toScreen = new AffineTransform(1, 0, 0, 1, 0, 0);
        mouse = null;
        dragging = false;
        click = MouseEvent.NOBUTTON;
        pen.begin = null;
        pen.end = null;
        mode = NO_OPERATION;
        hoveringBlock = null;
    }

    public void relocate() {
        toScreen.setToIdentity();
        if (!App.project.blocks.isEmpty()) {
            Rectangle rect = App.project.blocks.get(0).getOuterBounds();
            toScreen.translate(-rect.x * TILESIZE, -rect.y * TILESIZE);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (App.isInputProcessing() && !dragging) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                AbstractBlock ab = getBlockType();
                if (ab != null) ab.showDialog();
            }
            if (e.getButton() == MouseEvent.BUTTON1 && blockToAdd != null) {
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
                App.project.add(new Command() {
                    @Override
                    public void execute() {
                        App.project.blocks.add(block);
                        App.statusPanel.controller.setStatus(block.getTypeString() + " added", StatusPanelController.INFO);
                        App.statusPanel.controller.pushLog(String.format("%s block created with id %d at %d, %d", block.getTypeString(), block.getId(), cellCoords.x, cellCoords.y), StatusPanelController.INFO);
                    }
                    @Override
                    public void undo() {
                        App.project.blocks.remove(block);
                        App.statusPanel.controller.setStatus("Undo: " + block.getTypeString() + " added", StatusPanelController.INFO);
                        App.statusPanel.controller.pushLog(String.format("Undo: %s block created with id %d at %d, %d", block.getTypeString(), block.getId(), cellCoords.x, cellCoords.y), StatusPanelController.INFO);
                    }
                });
                App.selectPanel.controller.clear();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (click != MouseEvent.NOBUTTON) return;
        click = e.getButton();

        if (App.isInputProcessing() && click == MouseEvent.BUTTON1) {  // left mouse click
            AbstractBlock b = getBlockType();   // get the block under the mouse
            for (AbstractBlock ab : App.project.blocks) // clear all the selections
                ab.setSelected(false);
            if (b == null) {    // empty space clicked
                mode = DRAW_LINE;
                pen.end = pen.begin = getCellCoords(mouse);
            } else {    // block clicked
                mode = DRAG_BLOCK;
                pen.end = pen.begin = getCellCoords(mouse);
                b.setSelected(true);
            }
        }

        if (App.isInputProcessing() && click == MouseEvent.BUTTON3) {  // right mouse click - delete item
            AbstractBlock b = getBlockType();   // get the block under the mouse
            if (b == null) {    // it is a line remove it
                for (Line line : App.project.lines) {
                    if (line.containsInclusive(getCellCoords(mouse))) {

                        App.project.add(new Command() {
                            @Override
                            public void execute() {
                                App.project.lines.remove(line);
                                App.statusPanel.controller.setStatus("Line deleted", StatusPanelController.INFO);
                                App.statusPanel.controller.pushLog("Line " + line.toString(), StatusPanelController.INFO);

                            }
                            @Override
                            public void undo() {
                                App.project.lines.add(line);
                                App.statusPanel.controller.setStatus("Undo: Line deleted", StatusPanelController.INFO);
                                App.statusPanel.controller.pushLog("Undo: Line " + line.toString(), StatusPanelController.INFO);
                            }
                        });

                        break;
                    }
                }
            } else {    // it is a block remove it

                App.project.add(new Command() {
                    @Override
                    public void execute() {
                        App.statusPanel.controller.setStatus(b.getTypeString() + " deleted", StatusPanelController.INFO);
                        App.statusPanel.controller.pushLog(String.format("Block with id %d deleted", b.getId()), StatusPanelController.INFO);
                        App.project.blocks.remove(b);
                    }
                    @Override
                    public void undo() {
                        App.statusPanel.controller.setStatus("Undo: " + b.getTypeString() + " deleted", StatusPanelController.INFO);
                        App.statusPanel.controller.pushLog(String.format("Undo: Block with id %d deleted", b.getId()), StatusPanelController.INFO);
                        App.project.blocks.add(b);
                    }
                });

            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (click != e.getButton()) return; // only one click at a time
        click = MouseEvent.NOBUTTON;
        dragging = false;

        if (App.isInputProcessing() && e.getButton() == MouseEvent.BUTTON1 && mode == DRAW_LINE) {
            Point tmp = getCellCoords(mouse);
            if (tmp.x == pen.begin.x || tmp.y == pen.begin.y) {
                pen.end = tmp;
                if (!pen.begin.equals(pen.end)) {   // lines should be at least 2 block long

                    Line created = new Line(pen.begin, pen.end);

                    // check if pt begin/end fall on other lines
                    // and divide the line into two
                    boolean division = true;
                    while (division) {
                        division = false;
                        for (Line line : App.project.lines) {
                            // use exclusive method bc division not required on end points.
                            if (line.containsExclusive(created.begin)) {
                                Line d1 = new Line(line.begin, created.begin);
                                Line d2 = new Line(created.begin, line.end);

                                App.project.add(new Command() {
                                    @Override
                                    public void execute() {
                                        App.project.lines.add(d1);
                                        App.project.lines.add(d2);
                                        App.project.lines.remove(line);
                                        App.statusPanel.controller.pushLog("Line " + line.toString() + " spliced into " + d1.toString() + " and " + d2.toString(), StatusPanelController.INFO);

                                    }
                                    @Override
                                    public void undo() {
                                        App.project.lines.remove(d1);
                                        App.project.lines.remove(d2);
                                        App.project.lines.add(line);
                                        App.statusPanel.controller.pushLog("Undo: Line " + line.toString() + " spliced into " + d1.toString() + " and " + d2.toString(), StatusPanelController.INFO);
                                    }
                                });
                                division = true;
                                break;
                            }
                            if (line.containsExclusive(created.end)) {
                                Line d1 = new Line(line.begin, created.end);
                                Line d2 = new Line(created.end, line.end);
                                App.project.add(new Command() {
                                    @Override
                                    public void execute() {
                                        App.project.lines.add(d1);
                                        App.project.lines.add(d2);
                                        App.project.lines.remove(line);
                                        App.statusPanel.controller.pushLog("Line " + line.toString() + " spliced into " + d1.toString() + " and " + d2.toString(), StatusPanelController.INFO);
                                    }
                                    @Override
                                    public void undo() {
                                        App.project.lines.remove(d1);
                                        App.project.lines.remove(d2);
                                        App.project.lines.add(line);
                                        App.statusPanel.controller.pushLog("Undo: Line " + line.toString() + " spliced into " + d1.toString() + " and " + d2.toString(), StatusPanelController.INFO);
                                    }
                                });
                                division = true;
                                break;
                            }
                        }
                    }

                    App.project.add(new Command() {
                        @Override
                        public void execute() {
                            App.project.lines.add(created);
                            App.statusPanel.controller.pushLog("Line added " + created.toString(), StatusPanelController.INFO);
                        }
                        @Override
                        public void undo() {
                            App.project.lines.remove(created);
                            App.statusPanel.controller.pushLog("Undo: Line added " + created.toString(), StatusPanelController.INFO);
                        }
                    });

                }
            }
        }

        // reset
        if (getBlockType() != null) App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        pen.begin = pen.end = null;
        mode = NO_OPERATION;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (hoveringBlock != null) {
            hoveringBlock.setHovered(false);
            hoveringBlock = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        try {
            dragging = true;
            Point2D target = toScreen.inverseTransform(mouseEvent.getPoint(), null);
            Point2D dist = new Point2D.Double(target.getX() - mouse.getX(), target.getY() - mouse.getY());
            if (click == MouseEvent.BUTTON2) {
                toScreen.translate(dist.getX(), dist.getY());
                App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            mouse = toScreen.inverseTransform(mouseEvent.getPoint(), null);

            if (App.isInputProcessing()) {
                if (mode == DRAW_LINE) {
                    Point tmp = getCellCoords(mouse);
                    if (tmp.x == pen.begin.x || tmp.y == pen.begin.y) pen.end = tmp;
                }
                if (mode == DRAG_BLOCK) {
                    pen.end = getCellCoords(mouse);
                    Point d = new Point(pen.end.x - pen.begin.x, pen.end.y - pen.begin.y);
                    for (AbstractBlock ab : App.project.blocks) {
                        if (ab.isSelected()) {
                            ab.moveTo(d);
//                        for (AbstractBlock other : App.project.blocks) {
//                            if (ab.getId() != other.getId() && ab.getOuterBounds().intersects(other.getOuterBounds())) {
//                                ab.moveTo(new Point(-d.x, -d.y));
//                            }
//                        }
                        }
                    }
                    pen.begin = pen.end;
                    App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
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
                App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                ab.setHovered(true);
                hoveringBlock = ab;
            } else if (blockToAdd != null) {
                App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                App.drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
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
