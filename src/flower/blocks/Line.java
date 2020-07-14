package flower.blocks;

import java.awt.*;
import java.util.ArrayList;

import static flower.DrawPanel.TILESIZE;
import static flower.DrawPanel.PADDING;

public class Line {

    public Point begin;
    public Point end;
    public ArrayList<Point> hub = new ArrayList<>();
    private boolean ghost = false;

    public Line() {}

    public Line(Point b, Point e) {
        begin = b;
        end = e;
        if ((begin.x == end.x && begin.y > end.y) || (begin.y == end.y && begin.x > end.x)) {
            Point tmp = end;
            end = begin;
            begin = tmp;
        }
    }

    public Line(Point b, Point e, ArrayList<Point> h) {
        begin = b;
        end = e;
        if ((begin.x == end.x && begin.y > end.y) || (begin.y == end.y && begin.x > end.x)) {
            Point tmp = end;
            end = begin;
            begin = tmp;
        }
        hub.addAll(h);
    }

    public boolean isGhost() {return ghost;}

    public void setGhost(boolean sel) {ghost = sel;}

    public boolean contains(Point p) {
        boolean sameX = false;
        boolean sameY = false;

        for (int x = begin.x; x <= end.x; x++) {
            if (x == p.x) {
                sameX = true;
                break;
            }
        }

        for (int y = begin.y; y <= end.y; y++) {
            if (y == p.y) {
                sameY = true;
                break;
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

    public boolean isVertical() { return (begin.y == end.y); }

    public boolean isHorizontal() { return (begin.x == end.x); }

}
