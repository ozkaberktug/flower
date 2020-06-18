package flower.blocks;

import java.awt.*;

import static flower.DrawPanel.TILESIZE;
import static flower.DrawPanel.PADDING;

public class Line {

    public Point begin;
    public Point end;
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
        graphics2D.drawLine(begin.x * TILESIZE + PADDING, begin.y * TILESIZE + PADDING, end.x * TILESIZE + PADDING, end.y * TILESIZE + PADDING);
    }

}
