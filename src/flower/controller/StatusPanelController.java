package flower.controller;

import flower.App;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatusPanelController extends MouseAdapter {

    public static final int INFO = 0;
    public static final int WARNING = 1;
    public static final int ERROR = 2;

    private String status;
    private final StringBuilder log = new StringBuilder();

    @Override
    public void mouseClicked(MouseEvent e) {
        App.statusPanel.getPane().setVisible(!App.statusPanel.getPane().isVisible());
        App.statusPanel.revalidate();
        App.statusPanel.repaint();
    }

    public void setStatus(String statusTxt, int msgType) {
        // todo
    }

    public void pushLog(String msgTxt, int msgType) {
        // todo
    }

    public void clearLog() { log.setLength(0); }

    public String getLog() { return log.toString(); }

    public String getStatus() {return status;}


}
