package flower.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import static flower.view.DrawPanel.BOLD_STROKE;
import static flower.view.DrawPanel.PADDING;
import static flower.view.DrawPanel.TILESIZE;

public class Line {

    public Point begin;
    public Point end;
    private boolean ghost = false;

    public Line() {}

    public Line(int bx, int by, int ex, int ey) {
        begin = new Point(bx, by);
        end = new Point(ex, ey);
        normalize();
    }

    public Line(Point b, Point e) {
        begin = b;
        end = e;
        normalize();
    }

    public void normalize(){
        if ((begin.x == end.x && begin.y > end.y) || (begin.y == end.y && begin.x > end.x)) {
            Point tmp = end;
            end = begin;
            begin = tmp;
        }
    }

    public boolean isGhost() {return ghost;}

    public void setGhost(boolean sel) {ghost = sel;}

    public boolean contains(Line line) {
        if (end.y == line.end.y && begin.y == line.begin.y) return (begin.x <= line.begin.x && end.x >= line.end.x);
        else if (end.x == line.end.x && begin.x == line.begin.x)
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
        graphics2D.setStroke(BOLD_STROKE);
        if (ghost) graphics2D.setColor(Color.GRAY);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.fillOval(begin.x * TILESIZE + PADDING / 2, begin.y * TILESIZE + PADDING / 2, TILESIZE - PADDING, TILESIZE - PADDING);
        graphics2D.fillOval(end.x * TILESIZE + PADDING / 2, end.y * TILESIZE + PADDING / 2, TILESIZE - PADDING, TILESIZE - PADDING);
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
