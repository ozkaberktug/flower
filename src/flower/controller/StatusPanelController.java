package flower.controller;

import flower.App;
import flower.resources.ResourceManager;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusPanelController extends MouseAdapter {

    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    private String status = "";
    private final StringBuilder log = new StringBuilder();

    @Override
    public void mouseClicked(MouseEvent e) {
        if (App.getInstance().statusPanel.getBottomPane().isVisible()) {
            App.getInstance().statusPanel.getBottomPane().setVisible(false);
            App.getInstance().statusPanel.getButton().setIcon(ResourceManager.getImageIcon(ResourceManager.ARROW_UP));
        } else {
            App.getInstance().statusPanel.getBottomPane().setVisible(true);
            App.getInstance().statusPanel.getButton().setIcon(ResourceManager.getImageIcon(ResourceManager.ARROW_DOWN));
        }
        update();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        App.getInstance().statusPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        App.getInstance().statusPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void update() {
        App.getInstance().statusPanel.revalidate();
        App.getInstance().statusPanel.repaint();
    }

    public void setStatus(String statusTxt, int msgType) {
        status = statusTxt;
        App.getInstance().statusPanel.getLabel().setText(status);
        if (msgType == ERROR) App.getInstance().statusPanel.getLabel().setForeground(Color.RED);
        if (msgType == WARNING) App.getInstance().statusPanel.getLabel().setForeground(Color.ORANGE);
        if (msgType == INFO) App.getInstance().statusPanel.getLabel().setForeground(Color.BLACK);
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
        log.append("\n");
        App.getInstance().statusPanel.getArea().setText(log.toString());
        update();
    }

    public String getLog() { return log.toString(); }
    public String getStatus() {return status;}

}
