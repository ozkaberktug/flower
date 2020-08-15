package flower.controller;

import flower.App;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusPanelController extends MouseAdapter {

    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    private String status = "Ready";
    private final StringBuilder log = new StringBuilder();

    @Override
    public void mouseClicked(MouseEvent e) {
        App.statusPanel.getPane().setVisible(!App.statusPanel.getPane().isVisible());
        update();
    }

    private void update() {
        App.statusPanel.revalidate();
        App.statusPanel.repaint();
    }

    public void setStatus(String statusTxt, int msgType) {
        status = statusTxt;
        App.statusPanel.getLabel().setText(status);
        if (msgType == ERROR) App.statusPanel.getLabel().setForeground(Color.RED);
        if (msgType == WARNING) App.statusPanel.getLabel().setForeground(Color.ORANGE);
        if (msgType == INFO) App.statusPanel.getLabel().setForeground(Color.BLACK);
        update();
    }

    public void pushLog(String msgTxt, int msgType) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (msgType == ERROR) log.append("[ERROR]");
        if (msgType == WARNING) log.append("[WARNING]");
        if (msgType == INFO) log.append("[INFO]");
        log.append("[");
        log.append(dtf.format(LocalDateTime.now()));
        log.append("] ");
        log.append(msgTxt);
        log.append("\n\n");
        App.statusPanel.getArea().setText(log.toString());
        update();
    }

    public String getLog() { return log.toString(); }
    public String getStatus() {return status;}

}
