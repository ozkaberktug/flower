package flower.view;

import flower.controller.StatusPanelController;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class StatusPanel extends JPanel {

    public final StatusPanelController controller = new StatusPanelController();

    private final JLabel label;
    private final JTextArea area;
    private final JScrollPane areaScrollPane;

    public JLabel getLabel() {return label;}
    public JTextArea getArea() {return area;}
    public JScrollPane getPane() {return areaScrollPane;}

    public StatusPanel() {
        super(new GridBagLayout());

        GridBagConstraints gbcLabel = new GridBagConstraints(0, 0, 1, 1, 1.f, 0.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 5, 5), 0, 0);
        GridBagConstraints gbcArea = new GridBagConstraints(0, 1, 2, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 150);

        label = new JLabel();
        label.setForeground(Color.RED);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(controller);

        area = new JTextArea();
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setSelectedTextColor(Color.BLUE);
        area.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        areaScrollPane = new JScrollPane(area);
        areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setVisible(false);

        add(label, gbcLabel);
        add(areaScrollPane, gbcArea);

        setBorder(BorderFactory.createLoweredBevelBorder());

    }

}
