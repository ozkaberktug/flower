package flower.blocks;

import java.awt.*;

abstract public class AbstractBlock {

    protected boolean selected;
    protected boolean hovered;
    protected boolean breakpoint;

    /* do-able functions */
    abstract public void draw(Graphics2D graphics2D);

    abstract public void showDialog();

    abstract public void moveTo(Point delta);

    /* getter functions */
    abstract public Rectangle getInnerBounds();

    abstract public Rectangle getOuterBounds();

    abstract public Point[] getInputPins();

    abstract public Point[] getOutputPins();

    abstract public String getCode();

    /* setter functions */
    public void setSelected(boolean val) {selected = val;}

    public void setHovered(boolean val) {hovered = val;}

    public void setBreakpoint(boolean val) {breakpoint = val;}

    /* test functions */
    public boolean isSelected() {return selected;}

    public boolean isHovered() {return hovered;}

    public boolean isBreakpoint() {return breakpoint;}


}
