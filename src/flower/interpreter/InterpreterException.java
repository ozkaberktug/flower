package flower.interpreter;

import flower.controller.StatusPanelController;

public class InterpreterException extends RuntimeException {

    public final String description;
    public final String tip;
    public final int severity;

    public InterpreterException(String tip, String desc) {
        description = desc;
        this.tip = tip;
        severity = StatusPanelController.ERROR;
    }

}
