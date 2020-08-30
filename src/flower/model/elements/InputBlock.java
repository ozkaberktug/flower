package flower.model.elements;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import static flower.view.ViewConstants.TILESIZE;

public class InputBlock extends AbstractBlock {

    public InputBlock(Point offset) {
        super();
        type = INPUT_BLOCK;
        area = new Rectangle(offset.x, offset.y, 9, 5);
        code = "";
    }

    public InputBlock() {
        super();
        type = INPUT_BLOCK;
        area = new Rectangle();
        code = "";
    }

    public InputBlock(InputBlock block) {
        super();
        this.code = block.code;
        this.type = block.type;
        this.area = block.area;
        this.breakpoint = block.breakpoint;
        this.hovered = block.hovered;
        this.selected = block.selected;
        this.processing = block.processing;
    }


    @Override
    public void draw(Graphics2D graphics2D) {
        super.draw(graphics2D);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, ((area.y + 1) * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
    }

    @Override
    public Shape getShape() {
        GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        shape.moveTo(area.x * TILESIZE, (area.y + area.height * 0.5f) * TILESIZE);
        shape.lineTo(area.x * TILESIZE, (area.y + area.height) * TILESIZE);
        shape.lineTo((area.x + area.width) * TILESIZE, (area.y + area.height) * TILESIZE);
        shape.lineTo((area.x + area.width) * TILESIZE, area.y * TILESIZE);
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
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + area.width / 2, area.y + area.height);
        return ret;
    }

}
