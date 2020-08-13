package flower.controller;

import flower.App;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class StatusPanelController extends MouseAdapter {

    public static final int PLAIN_MSG = 0;
    public static final int INFO_MSG = 1;
    public static final int ERROR_MSG = 2;

    private String title;
    private final ArrayList<String> texts = new ArrayList<>();

    @Override
    public void mouseClicked(MouseEvent e) {
        App.statusPanel.getPane().setVisible(!App.statusPanel.getPane().isVisible());
        updateLog();
        App.statusPanel.revalidate();
        App.statusPanel.repaint();
//                app.revalidate();
//                app.repaint();
    }

    public void appendLog(String title) {
        App.statusPanel.getLabel().setForeground(Color.BLACK);
        this.title = title;
        updateLog();
    }

    public void appendLog(String title, String text, int msgType) {
        this.title = title;
        App.statusPanel.getLabel().setForeground(Color.BLACK);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String prefix = "";
        if (msgType == StatusPanelController.INFO_MSG)
            prefix = "[" + dtf.format(LocalDateTime.now()) + "] " + title + "\n    ";
        if (msgType == StatusPanelController.ERROR_MSG) {
            App.statusPanel.getLabel().setForeground(Color.RED);
            this.title = "Error: " + title;
            prefix = "[" + dtf.format(LocalDateTime.now()) + "] An error occurred: " + title + "\n      ";
        }
        this.texts.add(prefix + text);
        updateLog();
    }

    public void updateLog() {
        App.statusPanel.getLabel().setText(title);
        StringBuilder sb = new StringBuilder();
        for (String text : texts) {
            sb.append(text);
            sb.append("\n");
        }
        App.statusPanel.getArea().setText(sb.toString());
    }

    public void clear() {
        App.statusPanel.getLabel().setForeground(Color.BLACK);
        texts.clear();
        title = "Ready.";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        texts.add("flower (version: " + App.version_string + ") \nSession started at " + dtf.format(LocalDateTime.now()));
        updateLog();
    }

    public String getLog() {
        StringBuilder sb = new StringBuilder();
        for (String text : texts) {
            sb.append(text);
            sb.append("\n");
        }
        return sb.toString();
    }

}
