package flower.blocks;

import java.awt.*;
import java.util.ArrayList;

abstract public class AbstractBlock {

    public static final int CLEAR = 0;
    public static final int SELECTED = 1;
    public static final int HOVERED = 2;
    public static final int BREAKPOINT = 3;
    private int state;

    /* do-able functions */
    abstract public void draw(Graphics2D graphics2D);

    abstract public void showDialog();

    abstract public void moveTo(Point delta);

    /* getter functions */
    abstract public Rectangle getInnerBounds();

    abstract public Rectangle getOuterBounds();

    abstract public ArrayList<Point> getInputPins();

    abstract public ArrayList<Point> getOutputPins();

    abstract public String getCode();

    /* setter functions */
    public void setState(int state) { this.state = state; }

    /* test functions */
    public boolean isSelected() {return state == SELECTED;}

    public boolean isHovered() {return state == HOVERED;}

    public boolean isBreakpoint() {return state == BREAKPOINT;}


}
