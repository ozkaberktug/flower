package flower.blocks;

import java.awt.*;

import static flower.DrawPanel.TILESIZE;
import static flower.DrawPanel.PADDING;

public class Line {

    public Point begin;
    public Point end;
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

    public boolean isGhost() {return ghost;}

    public void setGhost(boolean sel) {ghost = sel;}

    public boolean contains(Line line) {
        if (isHorizontal() && line.isHorizontal() && begin.y == line.begin.y)
            return (begin.x <= line.begin.x && end.x >= line.end.x);
        else if (isVertical() && line.isVertical() && begin.x == line.begin.x)
            return (begin.y <= line.begin.y && end.y >= line.end.y);
        // perpendicular case which is always false
        return false;
    }

    public boolean containsInclusive(Point p) {
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

    public boolean containsExclusive(Point p) {

        if (isHorizontal() && p.y == begin.y) {
            for (int x = begin.x + 1; x < end.x; x++)
                if (x == p.x) return true;
        } else if (isVertical() && p.x == begin.x) {
            for (int y = begin.y + 1; y < end.y; y++)
                if (y == p.y) return true;
        }

        return false;
    }

    public void draw(Graphics2D graphics2D) {
        if (ghost) graphics2D.setColor(Color.GRAY);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.drawLine(begin.x * TILESIZE + PADDING, begin.y * TILESIZE + PADDING, end.x * TILESIZE + PADDING, end.y * TILESIZE + PADDING);
    }

    public boolean isVertical() { return (begin.x == end.x); }

    public boolean isHorizontal() { return (begin.y == end.y); }

    public boolean isPerpendicularTo(Line line) {
        boolean b1 = isHorizontal() && line.isVertical();
        boolean b2 = isVertical() && line.isHorizontal();
        return b1 || b2;
    }

}
