package flower.resources;

import flower.App;

import javax.swing.ImageIcon;
import java.net.URL;

public class ResourceManager {

    public static final String ARROW_DOWN = "arrow-down.png";
    public static final String ARROW_UP = "arrow-up.png";
    public static final String RUN = "run.png";
    public static final String STOP = "stop.png";
    public static final String RELOCATE = "relocate.png";
    public static final String QUALITY_ON = "quality-on.png";
    public static final String QUALITY_OFF = "quality-off.png";
    public static final String GRIDS_ON = "grids-on.png";
    public static final String GRIDS_OFF = "grids-off.png";
    public static final String APP_ICON = "icon.png";

    public static final URL ABOUT_PAGE = App.class.getResource("resources/about-page.html");

    public static ImageIcon getImageIcon(String path) {
        URL ref = App.class.getResource("resources/" + path);
        return new ImageIcon(ref);
    }

}
