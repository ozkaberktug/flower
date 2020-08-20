package flower.model;

import flower.App;
import flower.controller.StatusPanelController;
import flower.model.elements.AbstractBlock;
import flower.model.elements.Line;
import flower.util.Command;

import java.util.ArrayList;

public class Project {

    public final ArrayList<AbstractBlock> blocks;
    public final ArrayList<Line> lines;
    public String name;
    public String inputParams;
    public final ArrayList<Project> libs;

    private final ArrayList<Command> history;
    private int historyIndex = 0;

    public Project() {
        lines = new ArrayList<>();
        blocks = new ArrayList<>();
        name = "Untitled";
        inputParams = "";
        libs = new ArrayList<>();
        history = new ArrayList<>();
    }

    public void clear() {
        lines.clear();
        blocks.clear();
        name = "Untitled";
        inputParams = "";
        libs.clear();
        history.clear();
        historyIndex = 0;
    }

    public void add(Command command) {
        history.subList(historyIndex, history.size()).clear();
        history.add(command);
        historyIndex = history.size();
        command.execute();
    }

    public void undo() {
        if (historyIndex == 0) {
            App.statusPanel.controller.setStatus("Reached to the first state", StatusPanelController.ERROR);
            return;
        }
        Command command = history.get(historyIndex - 1);
        command.undo();
        historyIndex--;
    }

    public void redo() {
        if (historyIndex == history.size()) {
            App.statusPanel.controller.setStatus("Reached to the last state", StatusPanelController.ERROR);
            return;
        }
        Command command = history.get(historyIndex);
        command.execute();
        historyIndex++;
    }

}
