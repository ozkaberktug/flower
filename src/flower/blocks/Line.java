package flower.blocks;

import java.awt.*;

import static flower.DrawPanel.TILESIZE;
import static flower.DrawPanel.PADDING;

public class Line {

    public static final int HUB_NONE = 0;
    public static final int HUB_BEGIN = 1;
    public static final int HUB_END = 2;
    public static final int HUB_BOTH = HUB_BEGIN | HUB_END;
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public final Point begin;
    public final Point end;
    public final int hub;
    private boolean selected = false;

    public Line(Point b, Point e, int hub) {
        begin = b;
        end = e;
        this.hub = hub;
    }

    public boolean isSelected() {return selected;}

    public void setSelected(boolean sel) {selected = sel;}

    public boolean contains(Point p) {
        boolean sameX = false;
        boolean sameY = false;

        if (begin.x <= end.x) {
            for (int x = begin.x; x <= end.x; x++) {
                if (x == p.x) {
                    sameX = true;
                    break;
                }
            }
        } else {
            for (int x = begin.x; x >= end.x; x--) {
                if (x == p.x) {
                    sameX = true;
                    break;
                }
            }
        }

        if (begin.y <= end.y) {
            for (int y = begin.y; y <= end.y; y++) {
                if (y == p.y) {
                    sameY = true;
                    break;
                }
            }
        } else {
            for (int y = begin.y; y >= end.y; y--) {
                if (y == p.y) {
                    sameY = true;
                    break;
                }
            }
        }

        return sameX && sameY;
    }

    public void draw(Graphics2D graphics2D) {
        if (selected) graphics2D.setColor(Color.BLUE);
        else graphics2D.setColor(Color.BLACK);
        if((hub & HUB_BEGIN) == HUB_BEGIN)
            graphics2D.fillOval(begin.x * TILESIZE, begin.y * TILESIZE, TILESIZE, TILESIZE);
        if((hub & HUB_END) == HUB_END)
            graphics2D.fillOval(end.x * TILESIZE, end.y * TILESIZE, TILESIZE, TILESIZE);
        graphics2D.drawLine(begin.x * TILESIZE + PADDING, begin.y * TILESIZE + PADDING, end.x * TILESIZE + PADDING, end.y * TILESIZE + PADDING);
    }

    public int getDirection() {
        if (begin.x == end.x) {
            if (begin.y <= end.y) return SOUTH;
            return NORTH;
        } else { //begin.y == end.y
            if (begin.x <= end.x) return WEST;
            return EAST;
        }
    }

}
