package flower.blocks;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static flower.DrawPanel.*;

public class CommandBlock extends AbstractBlock {

    private final Rectangle area;
    private String code;
    private int codeLenMaxW = 0;
    private int codeLenMaxH = 0;

    public CommandBlock(Point offset) {
        super();
        area = new Rectangle(offset.x, offset.y, 9, 5);
        code = "";
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setFont(CODE_FONT);
        FontMetrics fm = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.PINK);
        graphics2D.fillRect(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);
        if (isSelected()) graphics2D.setColor(Color.BLUE);
        else if (isHovered()) graphics2D.setColor(Color.YELLOW);
        else if (isBreakpoint()) graphics2D.setColor(Color.RED);
        else graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(NORMAL_STROKE);
        graphics2D.drawRect(area.x * TILESIZE, area.y * TILESIZE, area.width * TILESIZE, area.height * TILESIZE);
        graphics2D.setColor(Color.BLACK);
        int index = 1;
        for (String line : code.split("\\n")) {
            graphics2D.drawString(line, (area.x * TILESIZE) + (area.width * TILESIZE - fm.stringWidth(line)) / 2, (area.y * TILESIZE) + fm.getHeight() * index + (area.height * TILESIZE - fm.getHeight() * codeLenMaxH) / 2);
            index++;
        }
        graphics2D.setColor(Color.ORANGE);
        graphics2D.fillOval((area.x + area.width / 2) * TILESIZE + PADDING / 2, (area.y - 1) * TILESIZE + PADDING / 2, PADDING, PADDING);
        graphics2D.fillOval((area.x + area.width / 2) * TILESIZE + PADDING / 2, (area.y + area.height) * TILESIZE + PADDING / 2, PADDING, PADDING);
    }

    @Override
    public void showDialog(Point location) {
        JFrame frame = new JFrame("#" + getId() + " - Command Block");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocation(location);

        JPanel contents = new JPanel(new BorderLayout(10, 10));
        contents.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel inputTxt = new JLabel("Enter statement:");
        contents.add(inputTxt, BorderLayout.PAGE_START);
        JTextArea codeArea = new JTextArea(code, 5, 40);
        codeArea.setFont(CODE_FONT);
        JButton saveBtn = new JButton();
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!codeArea.getText().isBlank()) code = codeArea.getText();
                codeLenMaxH = 0;
                codeLenMaxW = 0;
                for (String line : code.split("\\n")) {
                    if (line.length() > codeLenMaxW) codeLenMaxW = line.length();
                    codeLenMaxH++;
                }
                area.width = Math.max((int) (codeLenMaxW * 0.5f), 9);
                if (area.width % 2 == 0) area.width++;
                area.height = Math.max((int) (codeLenMaxH * 1.3f), 5);
                if (area.height % 2 == 0) area.height++;
                frame.dispose();
            }
        });
        saveBtn.setText("Save");
        JScrollPane codeScrollPane = new JScrollPane(codeArea);
        contents.add(codeScrollPane, BorderLayout.CENTER);
        contents.add(saveBtn, BorderLayout.PAGE_END);

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
        Point[] ret = new Point[1];
        ret[0] = new Point(area.x + area.width / 2, area.y + area.height);
        return ret;
    }

    @Override
    public String getCode() {
        return code;
    }
}
