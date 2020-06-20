package flower.blocks;

import java.awt.*;

import static flower.DrawPanel.*;

public class StartBlock extends AbstractBlock {

    private final Rectangle area;
    private final String code = "START";

    public StartBlock(Point offset) {
        super();
        area = new Rectangle(offset.x, offset.y, 5, 2);
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setFont(CODE_FONT);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.CYAN);
        graphics2D.fillRoundRect(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE, TILESIZE * 2, TILESIZE * 2);
        if (isSelected()) graphics2D.setColor(Color.BLUE);
        else if (isHovered()) graphics2D.setColor(Color.YELLOW);
        else if (isBreakpoint()) graphics2D.setColor(Color.RED);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(NORMAL_STROKE);
        graphics2D.drawRoundRect(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE, TILESIZE * 2, TILESIZE * 2);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, (area.y * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
        graphics2D.setColor(Color.ORANGE);
        graphics2D.fillOval((area.x + 2) * TILESIZE + PADDING / 2, (area.y + 2) * TILESIZE + PADDING / 2, PADDING, PADDING);
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
        return null;
    }

    @Override
    public Point[] getOutputPins() {
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + 2, area.y + 2);
        return ret;
    }

    @Override
    public String getCode() {
        return code;
    }
}
