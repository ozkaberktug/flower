package flower.blocks;

import java.awt.*;

abstract public class AbstractBlock {

    private static int id_counter = 0;

    protected boolean selected;
    protected boolean hovered;
    protected boolean breakpoint;
    protected boolean processing;

    private final int id;

    public AbstractBlock() {
        id = ++id_counter;
    }

    /* do-able functions */
    abstract public void draw(Graphics2D graphics2D);

    abstract public void showDialog(Point location);

    abstract public void moveTo(Point delta);

    /* getter functions */
    abstract public Rectangle getInnerBounds();

    public Rectangle getOuterBounds() {
        Rectangle inner = getInnerBounds();
        return new Rectangle(inner.x - 1, inner.y - 1, inner.width + 1, inner.height + 1);
    }

    abstract public Point[] getInputPins();

    abstract public Point[] getOutputPins();

    abstract public String getCode();

    public int getId() {
        return id;
    }

    /* setter functions */
    public void setSelected(boolean val) {selected = val;}

    public void setHovered(boolean val) {hovered = val;}

    public void setBreakpoint(boolean val) {breakpoint = val;}

    public void setProcessing(boolean val) {processing = val;}

    /* test functions */
    public boolean isSelected() {return selected;}

    public boolean isHovered() {return hovered;}

    public boolean isBreakpoint() {return breakpoint;}

    public boolean isProcessing() {return processing;}
}
