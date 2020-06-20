package flower;

import flower.blocks.AbstractBlock;
import flower.blocks.Line;

import java.util.ArrayList;

public class Project {
    public final ArrayList<AbstractBlock> blocks;
    public final ArrayList<Line> lines;
    public final String name;
    public final String inputParams;
    public final ArrayList<Project> libs;

    public Project() {
        lines = new ArrayList<>();
        blocks = new ArrayList<>();
        name = "Untitled";
        inputParams = "";
        libs = new ArrayList<>();
    }
}
