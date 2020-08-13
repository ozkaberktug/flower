package flower.model.elements;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static flower.view.ViewConstants.COMMENT_FONT;
import static flower.view.ViewConstants.DASHED_STROKE;
import static flower.view.ViewConstants.TILESIZE;

public class LabelBlock extends AbstractBlock {

    public LabelBlock(Point offset) {
        super();
        type = LABEL_BLOCK;
        code = "Type your comment";
        area = new Rectangle(offset.x, offset.y, code.length() / 2, 1);
    }

    public LabelBlock() {
        super();
        type = LABEL_BLOCK;
        code = "";
        area = new Rectangle();
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
        String title = "Block #" + getId();

        JTextField codeField = new JTextField(code, 20);
        codeField.setFont(COMMENT_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter your comment:"), codeField};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!codeField.getText().isEmpty() && !codeField.getText().matches("\\s+")) code = codeField.getText();
            area.width = code.length() / 2;
            if (area.width % 2 == 0) area.width++;
        }
    }

    @Override
    public Shape getShape() {
        return new Rectangle(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);

    }

    @Override
    public Point[] getInputPins() {
        return null;
    }

    @Override
    public Point[] getOutputPins() {
        return null;
    }

}
