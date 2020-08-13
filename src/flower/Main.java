package flower;

import flower.App;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        if (args.length == 2 && args[1].equals("--version")) System.out.println(App.version_string);

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

}
