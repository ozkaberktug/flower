package flower.blocks;

import java.awt.*;
import java.util.ArrayList;

import static flower.DrawPanel.TILESIZE;
import static flower.DrawPanel.PADDING;

public class Line {

    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public Point begin;
    public Point end;
    public ArrayList<Point> hub = null;
    private boolean ghost = false;

    public Line(Point b, Point e) {
        begin = b;
        end = e;
        hub = new ArrayList<>();
    }

    public Line(Point b, Point e, ArrayList<Point> h) {
        begin = b;
        end = e;
        hub = new ArrayList<>();
        hub.addAll(h);
    }

    public boolean isGhost() {return ghost;}

    public void setGhost(boolean sel) {ghost = sel;}

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
        if (ghost) graphics2D.setColor(Color.GRAY);
        else graphics2D.setColor(Color.BLACK);
        for (Point pt : hub)
            graphics2D.fillOval(pt.x * TILESIZE + PADDING / 2, pt.y * TILESIZE + PADDING / 2, TILESIZE - PADDING, TILESIZE - PADDING);
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
