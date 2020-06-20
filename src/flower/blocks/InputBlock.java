package flower.blocks;

import java.awt.*;
import java.awt.geom.GeneralPath;

import static flower.DrawPanel.*;

public class InputBlock extends AbstractBlock {
    private final Rectangle area;
    private final String code;

    public InputBlock(Point offset) {
        super();
        area = new Rectangle(offset.x, offset.y, 9, 5);
        code = "";
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setFont(CODE_FONT);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.PINK);

        // create polygon
        GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        shape.moveTo(area.x * TILESIZE, (area.y + area.height * 0.5f) * TILESIZE);
        shape.lineTo(area.x * TILESIZE, (area.y + area.height) * TILESIZE);
        shape.lineTo((area.x + area.width) * TILESIZE, (area.y + area.height) * TILESIZE);
        shape.lineTo((area.x + area.width) * TILESIZE, area.y * TILESIZE);
        shape.closePath();

        graphics2D.fill(shape);
        if (isSelected()) graphics2D.setColor(Color.BLUE);
        else if (isHovered()) graphics2D.setColor(Color.YELLOW);
        else if (isBreakpoint()) graphics2D.setColor(Color.RED);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(NORMAL_STROKE);
        graphics2D.draw(shape);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, (area.y * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
        graphics2D.setColor(Color.ORANGE);
        graphics2D.fillOval((area.x + area.width / 2) * TILESIZE + PADDING / 2, (area.y - 1) * TILESIZE + PADDING / 2, PADDING, PADDING);
        graphics2D.fillOval((area.x + area.width / 2) * TILESIZE + PADDING / 2, (area.y + area.height) * TILESIZE + PADDING / 2, PADDING, PADDING);
    }

    @Override
    public void showDialog(Point location) {

    }

    @Override
    public void moveTo(Point delta) {
        area.x += delta.x;
        area.y += delta.y;
    }

    @Override
    public Rectangle getBounds() {
        return area;
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

    @Override
    public String getCode() {
        return code;
    }
}
