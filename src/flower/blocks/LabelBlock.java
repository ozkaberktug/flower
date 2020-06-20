package flower.blocks;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import static flower.DrawPanel.*;

public class LabelBlock extends AbstractBlock {

    private final Rectangle area;
    private String code = "Type your comment";

    public LabelBlock(Point offset) {
        super();
        area = new Rectangle(offset.x, offset.y, 5, 2);
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setFont(COMMENT_FONT);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.BLACK);
        if (isHovered()) {
            graphics2D.setStroke(DASHED_STROKE);
            graphics2D.drawRect(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);
        }
        graphics2D.drawString(code, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(code)) / 2, (area.y * TILESIZE) + (area.height * TILESIZE + fm.getAscent()) / 2);
    }

    @Override
    public void showDialog(Point location) {
        JFrame frame = new JFrame("#" + getId() + " - Label Block");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocation(location);

        JPanel contents = new JPanel(new BorderLayout(10, 10));
        contents.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel inputTxt = new JLabel("Enter your comment:");
        contents.add(inputTxt, BorderLayout.PAGE_START);
        JTextField inputField = new JTextField(code, 20);
        inputField.setFont(COMMENT_FONT);
        inputField.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inputField.getText().isBlank()) code = inputField.getText();
                frame.dispose();
            }
        });
        contents.add(inputField, BorderLayout.CENTER);

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
        return null;
    }

    @Override
    public Point[] getOutputPins() {
        return null;
    }

    @Override
    public String getCode() {
        return code;
    }

}
