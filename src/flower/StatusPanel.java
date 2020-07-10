package flower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StatusPanel extends JPanel {

    public String title;
    public ArrayList<String> texts = new ArrayList<>();
    private final App app;
    private final JLabel label;
    private final JTextArea area;
    private final JScrollPane areaScrollPane;

    public StatusPanel(App app) {
        super(new GridBagLayout());
        this.app = app;

        GridBagConstraints gbcLabel = new GridBagConstraints(0, 0, 1, 1, 1.f, 0.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 5, 5), 0, 0);
        GridBagConstraints gbcArea = new GridBagConstraints(0, 1, 2, 1, 1.f, 1.f, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 150);

        label = new JLabel();
        label.setForeground(Color.RED);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                areaScrollPane.setVisible(!areaScrollPane.isVisible());
                updateLog();
                revalidate();
                repaint();
                app.revalidate();
                app.repaint();
            }
        });

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

        title = "Ready.";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        texts.add("flower (version: " + App.version_string + ") \nSession started at " + dtf.format(LocalDateTime.now()));
        updateLog();
    }

    public void updateLog() {
        label.setText(title);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.size(); i++) {
            sb.append(texts.get(i));
            sb.append("\n\n");
        }
        area.setText(sb.toString());
    }

    public void clear() {
        texts.clear();
        title = "Ready.";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        texts.add("flower (version: " + App.version_string + ") \nSession started at " + dtf.format(LocalDateTime.now()));
        updateLog();
    }

}
