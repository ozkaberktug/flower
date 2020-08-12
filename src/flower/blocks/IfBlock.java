package flower.blocks;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import static flower.DrawPanel.TILESIZE;

public class IfBlock extends AbstractBlock {

    public static final int TRUE_OUTPUT = 0;
    public static final int FALSE_OUTPUT = 1;

    public IfBlock(Point offset) {
        super();
        type = IF_BLOCK;
        area = new Rectangle(offset.x, offset.y, 9, 5);
        code = "";
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        super.draw(graphics2D);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, (area.y * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
        // WARNING HARDCODED VALUES BELOW
        graphics2D.drawString("T", area.x * TILESIZE, (area.y + area.width / 2) * TILESIZE);
        graphics2D.drawString("F", (area.x + area.width) * TILESIZE, (area.y + area.width / 2) * TILESIZE);
    }

    @Override
    public Shape getShape() {
        GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        shape.moveTo(area.x * TILESIZE, (area.y + area.height / 2.f) * TILESIZE);
        shape.lineTo((area.x + area.width / 2.f) * TILESIZE, (area.y + area.height) * TILESIZE);
        shape.lineTo((area.x + area.width) * TILESIZE, (area.y + area.height / 2.f) * TILESIZE);
        shape.lineTo((area.x + area.width / 2.f) * TILESIZE, area.y * TILESIZE);
        shape.closePath();
        return shape;
    }

    @Override
    public Point[] getInputPins() {
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + area.width / 2, area.y - 1);
        return ret;
    }

    @Override
    public Point[] getOutputPins() {
        Point[] ret = new Point[2];
        ret[0] = new Point(area.x - 1, area.y + area.height / 2);
        ret[1] = new Point(area.x + area.width, area.y + area.height / 2);
        return ret;
    }

}
