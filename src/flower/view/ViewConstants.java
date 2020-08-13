package flower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

public class ViewConstants {

    public static final int TILESIZE = 16;
    public static final int PADDING = 7;
    public static final Font HEAD_FONT = new Font(Font.MONOSPACED, Font.BOLD, 14);
    public static final Font CODE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    public static final Font COMMENT_FONT = new Font(Font.SERIF, Font.ITALIC, 12);
    public static final BasicStroke BOLD_STROKE = new BasicStroke(TILESIZE / 4.f);
    public static final BasicStroke NORMAL_STROKE = new BasicStroke(2.f);
    public static final BasicStroke DASHED_STROKE = new BasicStroke(1.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4}, 0);
    public static final Color BACKGROUND_COLOR = new Color(220, 220, 220);

}
