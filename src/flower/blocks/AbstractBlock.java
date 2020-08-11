package flower.blocks;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static flower.DrawPanel.CODE_FONT;
import static flower.DrawPanel.NORMAL_STROKE;
import static flower.DrawPanel.PADDING;
import static flower.DrawPanel.TILESIZE;

abstract public class AbstractBlock {

    private static int id_counter = 0;

    protected boolean selected;
    protected boolean hovered;
    protected boolean breakpoint;
    protected boolean processing;

    protected Rectangle area;
    protected String code;

    private final int id;

    public AbstractBlock() {
        id = ++id_counter;
    }

    /* do-able functions */
    public void draw(Graphics2D graphics2D) {
        graphics2D.setStroke(NORMAL_STROKE);
        Shape shape = getShape();

        if (processing) graphics2D.setColor(Color.ORANGE);
        else graphics2D.setColor(Color.PINK);
        graphics2D.fill(shape);

        if (selected) graphics2D.setColor(Color.BLUE);
        else if (hovered) graphics2D.setColor(Color.YELLOW);
        else if (breakpoint) graphics2D.setColor(Color.RED);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.draw(shape);

        graphics2D.setColor(Color.ORANGE);
        if (getInputPins() != null) for (Point p : getInputPins())
            graphics2D.fillOval(p.x * TILESIZE + PADDING / 2, p.y * TILESIZE + PADDING / 2, PADDING, PADDING);
        if (getOutputPins() != null) for (Point p : getOutputPins())
            graphics2D.fillOval(p.x * TILESIZE + PADDING / 2, p.y * TILESIZE + PADDING / 2, PADDING, PADDING);

        graphics2D.setFont(CODE_FONT);
        graphics2D.setColor(Color.BLACK);
    }

    public void showDialog(Point location) {
        String title = "Block #" + getId();

        JTextField codeField = new JTextField(code, 40);
        codeField.setFont(CODE_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter expression:"), codeField};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!codeField.getText().isEmpty() && !codeField.getText().matches("\\s+")) code = codeField.getText();
            area.width = Math.max((int) (code.length() * 0.8f), 9);
            if (area.width % 2 == 0) area.width++;
        }
    }

    public void moveTo(Point delta) {
        area.x += delta.x;
        area.y += delta.y;
    }

    /* getter functions */
    public Rectangle getInnerBounds() {
        return area;
    }

    public Rectangle getOuterBounds() {
        Rectangle inner = getInnerBounds();
        return new Rectangle(inner.x - 1, inner.y - 1, inner.width + 1, inner.height + 1);
    }

    abstract public Shape getShape();

    abstract public Point[] getInputPins();

    abstract public Point[] getOutputPins();

    public String getCode() {return code;}

    public int getId() {
        return id;
    }

    /* setter functions */
    public void setSelected(boolean val) {selected = val;}

    public void setHovered(boolean val) {hovered = val;}

    public void setBreakpoint(boolean val) {breakpoint = val;}

    public void setProcessing(boolean val) {processing = val;}

    /* test functions */
    public boolean isSelected() {return selected;}

    public boolean isHovered() {return hovered;}

    public boolean isBreakpoint() {return breakpoint;}

    public boolean isProcessing() {return processing;}
}
