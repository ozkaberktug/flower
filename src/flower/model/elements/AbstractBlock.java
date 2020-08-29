package flower.model.elements;

import flower.App;
import flower.controller.StatusPanelController;
import flower.util.Command;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static flower.view.ViewConstants.CODE_FONT;
import static flower.view.ViewConstants.NORMAL_STROKE;
import static flower.view.ViewConstants.PADDING;
import static flower.view.ViewConstants.TILESIZE;

abstract public class AbstractBlock {

    public static final int START_BLOCK = 1;
    public static final int STOP_BLOCK = 2;
    public static final int COMMAND_BLOCK = 3;
    public static final int IF_BLOCK = 4;
    public static final int INPUT_BLOCK = 5;
    public static final int OUTPUT_BLOCK = 6;
    public static final int LABEL_BLOCK = 7;

    private static int id_counter = 0;

    protected boolean selected;
    protected boolean hovered;
    protected boolean breakpoint;
    protected boolean processing;

    protected int type;
    protected Rectangle area;
    protected String code;

    private int id;

    public AbstractBlock() {
        id = ++id_counter;
    }

    public String getTypeString() {
        switch (type) {
            case START_BLOCK:
                return "START";
            case STOP_BLOCK:
                return "STOP";
            case COMMAND_BLOCK:
                return "COMMAND";
            case IF_BLOCK:
                return "IF";
            case INPUT_BLOCK:
                return "INPUT";
            case OUTPUT_BLOCK:
                return "OUTPUT";
            case LABEL_BLOCK:
                return "LABEL";
        }
        return null;
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

    public void showDialog() {
        String title = "Block #" + getId();

        JTextField codeField = new JTextField(code, 40);
        codeField.setFont(CODE_FONT);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter expression:"), codeField};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!codeField.getText().isEmpty() && !codeField.getText().matches("\\s+")) {

                App.project.add(new Command() {
                    final String backup = code;
                    @Override
                    public void execute() {
                        code = codeField.getText();
                        normalizeSize();
                        App.statusPanel.controller.pushLog("Edited block #" + getId(), StatusPanelController.INFO);
                    }
                    @Override
                    public void undo() {
                        code = backup;
                        normalizeSize();
                        App.statusPanel.controller.pushLog("Undo: Edited block #" + getId(), StatusPanelController.INFO);
                    }
                });
            }

        }
    }

    public void moveTo(Point delta) {
//        if(delta.x == 0 && delta.y == 0) return;
//        String msgTxt = String.format("Block with id %d has been moved from %d, %d to %d, %d", getId(), area.x, area.y, area.x + delta.x, area.y + delta.y);
//        App.statusPanel.controller.pushLog(msgTxt, StatusPanelController.INFO);

        App.project.add(new Command() {
            Point backup = new Point(area.x, area.y);
            @Override
            public void execute() {
                area.x += delta.x;
                area.y += delta.y;
                App.statusPanel.controller.pushLog("#" + getId() + " moved to " + area.x + "," + area.y, StatusPanelController.INFO);
            }
            @Override
            public void undo() {
                area.x = backup.x;
                area.y = backup.y;
                App.statusPanel.controller.pushLog("Undo: #" + getId() + " moved to " + area.x + "," + area.y, StatusPanelController.INFO);
            }
        });

    }

    public void normalizeSize() {
        area.width = Math.max((int) (code.length() * 0.8f), 9);
        if (area.width % 2 == 0) area.width++;
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

    public int getType() {return type;}

    public int getId() {
        return id;
    }

    /* setter functions */
    public void setSelected(boolean val) {selected = val;}

    public void setHovered(boolean val) {hovered = val;}

    public void setBreakpoint(boolean val) {breakpoint = val;}

    public void setProcessing(boolean val) {processing = val;}

    // only called from open method
    public void setId(int id) {this.id = id;}

    // only called from open method
    public void setInnerBounds(Rectangle r) {area = r;}

    public void setCode(String code) {this.code = code;}

    /* test functions */
    public boolean isSelected() {return selected;}

    public boolean isHovered() {return hovered;}

    public boolean isBreakpoint() {return breakpoint;}

    public boolean isProcessing() {return processing;}

}
