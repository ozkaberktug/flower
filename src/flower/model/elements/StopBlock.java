package flower.model.elements;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import static flower.view.ViewConstants.HEAD_FONT;
import static flower.view.ViewConstants.TILESIZE;

public class StopBlock extends AbstractBlock {

    public StopBlock(Point offset) {
        super();
        type = STOP_BLOCK;
        area = new Rectangle(offset.x, offset.y, 5, 2);
        code = "STOP";
    }

    public StopBlock() {
        super();
        type = STOP_BLOCK;
        area = new Rectangle();
        code = "";
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        super.draw(graphics2D);
        graphics2D.setFont(HEAD_FONT);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, (area.y * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
    }

    @Override
    public void showDialog(Point location) { }

    @Override
    public void normalizeSize() { }

    @Override
    public Shape getShape() {
        return new RoundRectangle2D.Float(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE, TILESIZE * 2, TILESIZE * 2);
    }

    @Override
    public Point[] getInputPins() {
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + 2, area.y - 1);
        return ret;
    }

    @Override
    public Point[] getOutputPins() {
        return null;
    }

}
