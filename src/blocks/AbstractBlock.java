package blocks;

import java.awt.*;
import java.util.ArrayList;

abstract public class AbstractBlock {

    public static final int SELECTED = 1;
    public static final int HOVERED = 2;
    public static final int BREAKPOINT = 4;
    private int state;

    /* do-able functions */
    abstract public void draw(Graphics2D graphics2D);

    abstract public void showDialog();

    /* getter functions */
    abstract public Rectangle getInnerBounds();

    abstract public Rectangle getOuterBounds();

    abstract public ArrayList<Point> getInputPins();

    abstract public ArrayList<Point> getOutputPins();

    abstract public String getCode();

    /* setter functions */
    public void setState(int state) throws IllegalArgumentException { this.state = state; }

    /* test functions */
    public boolean isSelected() {return state == SELECTED;}

    public boolean isHovered() {return state == HOVERED;}

    public boolean isBreakpoint() {return state == BREAKPOINT;}


}
