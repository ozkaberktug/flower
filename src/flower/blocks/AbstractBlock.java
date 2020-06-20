package flower.blocks;

import java.awt.*;

abstract public class AbstractBlock {

    private static int id_counter = 0;

    protected boolean selected;
    protected boolean hovered;
    protected boolean breakpoint;

    private final int id;

    public AbstractBlock() {
        id = ++id_counter;
    }

    /* do-able functions */
    abstract public void draw(Graphics2D graphics2D);

    abstract public void showDialog(Point location);

    abstract public void moveTo(Point delta);

    /* getter functions */
    abstract public Rectangle getBounds();

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

    /* test functions */
    public boolean isSelected() {return selected;}

    public boolean isHovered() {return hovered;}

    public boolean isBreakpoint() {return breakpoint;}


}
