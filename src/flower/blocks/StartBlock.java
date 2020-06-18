package flower.blocks;

import java.awt.*;
import java.util.ArrayList;

import static flower.DrawPanel.TILESIZE;

public class StartBlock extends AbstractBlock {

    Rectangle area;
    String code = "START";

    public StartBlock(Point offset) {
        area = new Rectangle(offset, new Dimension(4, 2));
    }

    public StartBlock(int x, int y) {
        area = new Rectangle(x, y, 5, 2);
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.CYAN);
        graphics2D.fillOval(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, (area.y * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void moveTo(Point delta) {
        area.x += delta.x;
        area.y += delta.y;
    }

    @Override
    public Rectangle getInnerBounds() {
        return area;
    }

    @Override
    public Rectangle getOuterBounds() {
        return area;
    }

    @Override
    public ArrayList<Point> getInputPins() {
        return null;
    }

    @Override
    public ArrayList<Point> getOutputPins() {
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }
}
