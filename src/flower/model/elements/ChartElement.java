package flower.model.elements;

import java.awt.Graphics2D;

abstract public class ChartElement {

    public static final int START_BLOCK = 1;
    public static final int STOP_BLOCK = 2;
    public static final int COMMAND_BLOCK = 3;
    public static final int IF_BLOCK = 4;
    public static final int INPUT_BLOCK = 5;
    public static final int OUTPUT_BLOCK = 6;
    public static final int LABEL_BLOCK = 7;
    public static final int LINE = 8;

    protected boolean selected;
    public void setSelected(boolean val) {selected = val;}
    public boolean isSelected() {return selected;}


    private static int id_counter = 0;
    private int id;
    private final int type;

    public ChartElement(int type) {
        id = ++id_counter;
        this.type = type;
    }

    public int getId() { return id; }
    public int getType() {return type;}

    public void setId(int id) {this.id = id;}

    public static String getTypeString(int type) {
        switch (type) {
            case START_BLOCK:
                return "START";
            case STOP_BLOCK:
                return "STOP";
            case COMMAND_BLOCK:
                return "COMMAND";
            case IF_BLOCK:
                return "IF";
            case INPUT_BLOCK:
                return "INPUT";
            case OUTPUT_BLOCK:
                return "OUTPUT";
            case LABEL_BLOCK:
                return "LABEL";
            case LINE:
                return "Line";
        }
        return null;
    }

    abstract void draw(Graphics2D graphics2D);

}
