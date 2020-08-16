package flower.resources;

import flower.App;

import javax.swing.ImageIcon;
import java.net.URL;

public class ResourceManager {

    public static final String ARROW_DOWN = "arrow-down.png";
    public static final String ARROW_UP = "arrow-up.png";
    public static final String RUN_BUTTON = "run-button.png";
    public static final String STOP_BUTTON = "stop-button.png";
    public static final String RUN_BUTTON_DISABLED = "run-button-disabled.png";
    public static final String STOP_BUTTON_DISABLED = "stop-button-disabled.png";

    public static ImageIcon getImageIcon(String path) {
        URL ref = App.class.getResource("resources/"+path);
        ImageIcon icon = new ImageIcon(ref);
        return icon;
    }

}
