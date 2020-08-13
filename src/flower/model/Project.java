package flower.model;

import flower.model.elements.AbstractBlock;
import flower.model.elements.Line;

import java.util.ArrayList;

public class Project {

    public final ArrayList<AbstractBlock> blocks;
    public final ArrayList<Line> lines;
    public String name;
    public String inputParams;
    public final ArrayList<Project> libs;

    public Project() {
        lines = new ArrayList<>();
        blocks = new ArrayList<>();
        name = "Untitled";
        inputParams = "";
        libs = new ArrayList<>();
    }

    public void clear() {
        lines.clear();
        blocks.clear();
        name = "Untitled";
        inputParams = "";
        libs.clear();
    }

}
