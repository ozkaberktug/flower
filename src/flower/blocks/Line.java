package flower.blocks;

import java.awt.*;

import static flower.DrawPanel.TILESIZE;
import static flower.DrawPanel.PADDING;

public class Line {

    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public final Point begin;
    public final Point end;
    private boolean selected = false;

    public Line(int x1, int y1, int x2, int y2) {
        begin = new Point(x1, y1);
        end = new Point(x2, y2);
    }

    public Line(Point b, Point e) {
        begin = b;
        end = e;
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
        graphics2D.drawLine(begin.x * TILESIZE + PADDING, begin.y * TILESIZE + PADDING, end.x * TILESIZE + PADDING, end.y * TILESIZE + PADDING);
    }

    public int getDirection() {
        if(begin.x == end.x) {
            if(begin.y <= end.y) return SOUTH;
            return NORTH;
        }
        else { //begin.y == end.y
            if(begin.x <= end.x) return WEST;
            return EAST;
        }
    }

}
