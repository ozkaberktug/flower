package flower.blocks;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import static flower.DrawPanel.CODE_FONT;
import static flower.DrawPanel.TILESIZE;

public class CommandBlock extends AbstractBlock {

    private int codeLenMaxH = 0;

    public CommandBlock(Point offset) {
        super();
        area = new Rectangle(offset.x, offset.y, 9, 5);
        code = "";
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        super.draw(graphics2D);
        FontMetrics fm = graphics2D.getFontMetrics();
        int index = 1;
        for (String line : code.split("\\n")) {
            graphics2D.drawString(line, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(line)) / 2, (area.y * TILESIZE) + fm.getHeight() * index + (area.height * TILESIZE - fm.getHeight() * codeLenMaxH) / 2);
            index++;
        }
    }

    @Override
    public void showDialog(Point location) {
        String title = "Block #" + getId();

        JTextArea codeArea = new JTextArea(code, 5, 40);
        codeArea.setFont(CODE_FONT);
        JScrollPane codeScrollPane = new JScrollPane(codeArea);

        final JComponent[] inputs = new JComponent[]{new JLabel("Enter statement:"), codeScrollPane};
        int result = JOptionPane.showConfirmDialog(null, inputs, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (!codeArea.getText().isEmpty() && !codeArea.getText().matches("\\s+")) code = codeArea.getText();
            codeLenMaxH = 0;
            int codeLenMaxW = 0;
            for (String line : code.split("\\n")) {
                if (line.length() > codeLenMaxW) codeLenMaxW = line.length();
                codeLenMaxH++;
            }
            area.width = Math.max((int) (codeLenMaxW * 0.5f), 9);
            if (area.width % 2 == 0) area.width++;
            area.height = Math.max((int) (codeLenMaxH * 1.3f), 5);
            if (area.height % 2 == 0) area.height++;
        }
    }

    @Override
    public Shape getShape() {
        return new Rectangle(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);
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
