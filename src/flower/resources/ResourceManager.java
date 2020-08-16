package flower.resources;

import flower.App;

import javax.swing.ImageIcon;
import java.net.URL;

public class ResourceManager {

    public static final String ARROW_DOWN = "arrow-down.png";
    public static final String ARROW_UP = "arrow-up.png";

    public static ImageIcon getImageIcon(String path) {
        URL ref = App.class.getResource("resources/"+path);
        ImageIcon icon = new ImageIcon(ref);
        return icon;
    }

}
