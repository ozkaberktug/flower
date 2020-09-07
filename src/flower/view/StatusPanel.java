package flower.view;

import flower.controller.StatusPanelController;
import flower.resources.ResourceManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

    private final StatusPanelController controller = new StatusPanelController();

    private JLabel label;
    private JLabel button;
    private JTextArea area;
    private JScrollPane bottomPane;

    public JLabel getLabel() {return label;}
    public JTextArea getArea() {return area;}
    public JLabel getButton() {return button;}
    public JScrollPane getBottomPane() {return bottomPane;}

    public StatusPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createLoweredBevelBorder());
        initComponent();
    }

    public StatusPanelController getController() { return controller; }

    private void initComponent() {

        button = new JLabel(ResourceManager.getImageIcon(ResourceManager.ARROW_UP));
        button.addMouseListener(controller);

        label = new JLabel();
        label.setFont(new Font(Font.SERIF, Font.BOLD, 12));

        JPanel topPane = new JPanel();
        topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));
        topPane.add(label);
        topPane.add(Box.createHorizontalGlue());
        topPane.add(button);

        area = new JTextArea();
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setSelectedTextColor(Color.BLUE);
        area.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        bottomPane = new JScrollPane(area);
        bottomPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bottomPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        bottomPane.setVisible(false);

        GridBagConstraints gbcTopPane = new GridBagConstraints(0, 0, 1, 1, 1.f, 0.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints gbcBottomPane = new GridBagConstraints(0, 1, 1, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 150);
        add(topPane, gbcTopPane);
        add(bottomPane, gbcBottomPane);
    }

}
