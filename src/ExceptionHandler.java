import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread thread, Throwable thrown) {

        Date date = new Date();
        String home_dir = System.getProperty("user.home");
        String timestamp = String.valueOf(date.getTime());
        String logFilePath = home_dir + File.separator + "err_log-" + timestamp + ".txt";
        File logFile = new File(logFilePath);

        try {
            PrintStream ps = new PrintStream(logFile);
            thrown.printStackTrace(ps);
            // todo print other infos
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String message = "<html>" + thrown.toString() + " occurred! <br/><br/> This should not be happened. An error log has been saved to <br/> <u>" + logFilePath + "</u><br/><br/>If you send me an email (bkozkan@outlook.com), please attach this file also!";

        JOptionPane.showMessageDialog(null, message, "Exception occurred on " + thread.getName(), JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

}
