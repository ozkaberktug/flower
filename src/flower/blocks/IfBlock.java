package flower.blocks;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;

import static flower.DrawPanel.*;

public class IfBlock extends AbstractBlock {
    private final Rectangle area;
    private String code;  // code will be one line expression

    public IfBlock(Point offset) {
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
        shape.moveTo(area.x * TILESIZE, (area.y + area.height / 2.f) * TILESIZE);
        shape.lineTo((area.x + area.width / 2.f) * TILESIZE, (area.y + area.height) * TILESIZE);
        shape.lineTo((area.x + area.width) * TILESIZE, (area.y + area.height / 2.f) * TILESIZE);
        shape.lineTo((area.x + area.width / 2.f) * TILESIZE, area.y * TILESIZE);
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
        graphics2D.fillOval((area.x - 1) * TILESIZE + PADDING / 2, (area.y + area.height / 2) * TILESIZE + PADDING / 2, PADDING, PADDING);
        graphics2D.fillOval((area.x + area.width) * TILESIZE + PADDING / 2, (area.y + area.height / 2) * TILESIZE + PADDING / 2, PADDING, PADDING);
    }

    @Override
    public void showDialog(Point location) {
        JFrame frame = new JFrame("#" + getId() + " - If Block");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocation(location);

        JPanel contents = new JPanel(new BorderLayout(10, 10));
        contents.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel inputTxt = new JLabel("Enter expression:");
        contents.add(inputTxt, BorderLayout.PAGE_START);
        JTextField codeField = new JTextField(code, 20);
        codeField.setFont(CODE_FONT);
        codeField.addActionListener(e -> {
            if (!codeField.getText().isBlank()) code = codeField.getText();
            area.width = Math.max((int) (code.length() * 0.8f), 9);
            if (area.width % 2 == 0) area.width++;
            frame.dispose();
        });
        contents.add(codeField, BorderLayout.CENTER);

        frame.setContentPane(contents);
        frame.pack();
        frame.setVisible(true);
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
        Point[] ret = new Point[2];
        ret[0] = new Point(area.x - 1, area.y + area.height / 2);
        ret[1] = new Point(area.x + area.width, area.y + area.height / 2);
        return ret;
    }

    @Override
    public String getCode() {
        return code;
    }
}
