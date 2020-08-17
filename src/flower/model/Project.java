package flower.model;

import flower.App;
import flower.controller.StatusPanelController;
import flower.model.elements.AbstractBlock;
import flower.model.elements.Line;
import flower.util.Command;

import java.util.ArrayList;
import java.util.Stack;

public class Project {

    public final ArrayList<AbstractBlock> blocks;
    public final ArrayList<Line> lines;
    public String name;
    public String inputParams;
    public final ArrayList<Project> libs;

    private final Stack<Command> history;

    public Project() {
        lines = new ArrayList<>();
        blocks = new ArrayList<>();
        name = "Untitled";
        inputParams = "";
        libs = new ArrayList<>();
        history = new Stack<>();
    }

    public void clear() {
        lines.clear();
        blocks.clear();
        name = "Untitled";
        inputParams = "";
        libs.clear();
        history.clear();
    }

    public void add(Command command) {
        App.statusPanel.controller.pushLog(command.info(), StatusPanelController.INFO);
        history.push(command);
        command.execute();
    }

    public void undo() {
        if (history.isEmpty()) {
            App.statusPanel.controller.setStatus("There is no operation to undo", StatusPanelController.ERROR);
            return;
        }
        Command command = history.pop();
        command.undo();
    }

}
