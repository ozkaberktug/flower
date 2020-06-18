package flower;

import flower.blocks.AbstractBlock;
import flower.blocks.Line;

import java.util.ArrayList;

public class Project {
    public ArrayList<AbstractBlock> blocks;
    public ArrayList<Line> lines;
    public String name;
    public String inputParams;
    public ArrayList<Project> libs;

    public Project() {
        lines = new ArrayList<>();
        blocks = new ArrayList<>();
        name = "Untitled";
        inputParams = "";
        libs = new ArrayList<>();
    }
}
