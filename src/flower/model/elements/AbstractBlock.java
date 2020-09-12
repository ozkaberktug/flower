package flower.model.elements;

import flower.App;
import flower.controller.StatusPanelController;
import flower.util.Command;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static flower.view.ViewConstants.CODE_FONT;
import static flower.view.ViewConstants.NORMAL_STROKE;
import static flower.view.ViewConstants.PADDING;
import static flower.view.ViewConstants.TILESIZE;

abstract public class AbstractBlock extends ChartElement {

    protected boolean hovered;
    protected boolean breakpoint;
    protected boolean processing;

    protected Rectangle area;
    protected String code;


    AbstractBlock(int type) {super(type);}


    /* do-able functions */
    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setStroke(NORMAL_STROKE);
        Shape shape = getShape();

        if (processing) graphics2D.setColor(Color.ORANGE);
        else graphics2D.setColor(Color.PINK);
        graphics2D.fill(shape);

        if (selected) graphics2D.setColor(Color.BLUE);
        else if (hovered) graphics2D.setColor(Color.YELLOW);
        else if (breakpoint) graphics2D.setColor(Color.RED);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.draw(shape);

        graphics2D.setColor(Color.ORANGE);
        if (getInputPins() != null) for (Point p : getInputPins())
            graphics2D.fillOval(p.x * TILESIZE + PADDING / 2, p.y * TILESIZE + PADDING / 2, PADDING, PADDING);
        if (getOutputPins() != null) for (Point p : getOutputPins())
            graphics2D.fillOval(p.x * TILESIZE + PADDING / 2, p.y * TILESIZE + PADDING / 2, PADDING, PADDING);

        graphics2D.setFont(CODE_FONT);
        graphics2D.setColor(Color.BLACK);
    }

    abstract public void showDialog();
    abstract public void normalizeSize();
    protected void saveChanges(String newCode) {
        App.getInstance().project.add(new Command() {
            final String backup = code;
            @Override
            public void execute() {
                code = newCode;
                normalizeSize();
                App.getInstance().statusPanel.getController().pushLog("Edited block #" + getId(), StatusPanelController.INFO);
            }
            @Override
            public void undo() {
                code = backup;
                normalizeSize();
                App.getInstance().statusPanel.getController().pushLog("Undo: Edited block #" + getId(), StatusPanelController.INFO);
            }
        });
    }


    public void moveTo(Point delta) {
        if (delta.x == 0 && delta.y == 0) return;

        App.getInstance().project.add(new Command() {
            final Point backup = new Point(area.x, area.y);
            @Override
            public void execute() {
                area.x += delta.x;
                area.y += delta.y;
                App.getInstance().statusPanel.getController().setStatus("Block moved", StatusPanelController.INFO);
                App.getInstance().statusPanel.getController().pushLog("#" + getId() + " moved to " + area.x + "," + area.y, StatusPanelController.INFO);
            }
            @Override
            public void undo() {
                area.x = backup.x;
                area.y = backup.y;
                App.getInstance().statusPanel.getController().setStatus("Undo: Block moved", StatusPanelController.INFO);
                App.getInstance().statusPanel.getController().pushLog("Undo: #" + getId() + " moved to " + area.x + "," + area.y, StatusPanelController.INFO);
            }
        });

    }

    /* getter functions */
    abstract public Shape getShape();
    abstract public Point[] getInputPins();
    abstract public Point[] getOutputPins();
    public String getCode() {return code;}
    public Rectangle getInnerBounds() { return area; }
    public Rectangle getOuterBounds() {
        Rectangle inner = getInnerBounds();
        return new Rectangle(inner.x - 1, inner.y - 1, inner.width + 1, inner.height + 1);
    }


    /* setter functions */
    public void setHovered(boolean val) {hovered = val;}
    public void setBreakpoint(boolean val) {breakpoint = val;}
    public void setProcessing(boolean val) {processing = val;}
    public void setInnerBounds(Rectangle r) {area = r;}
    public void setCode(String code) {this.code = code;}


    /* test functions */
    public boolean isHovered() {return hovered;}
    public boolean isBreakpoint() {return breakpoint;}
//    public boolean isProcessing() {return processing;}

}
