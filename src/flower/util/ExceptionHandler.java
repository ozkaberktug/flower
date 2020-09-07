package flower.util;

import flower.App;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final int NORMAL = 0;
    public static final int FATAL = 1;

    // all uncaught exceptions assumed as fatal errors
    @Override
    public void uncaughtException(Thread th, Throwable ex) {
        String logFilePath = dumpToFile(ex);
        String message = "<html>" + ex.toString() + " occurred! <br/><br/> This should not be happened. An error log has been saved to <br/> <u>" + logFilePath + "</u><br/><br/>If you send me an email (bkozkan@outlook.com), please attach this file also! <br/><br> Program will be closed.";
        JOptionPane.showMessageDialog(null, message, "Exception occurred on " + th.getName(), JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public void handle(Throwable ex, int severity) {
        Thread th = Thread.currentThread();
        if (severity == FATAL) uncaughtException(th, ex);
        else if (severity == NORMAL) {
            String logFilePath = dumpToFile(ex);
            String message = "<html>" + ex.toString() + " occurred! <br/>This issue may or may not has an impact on your work. <br/>" + "An error log has been saved to <br/> <u>" + logFilePath + "</u> <br/>If you see this message more than one please contact via email (bkozkan@outlook.com) and attach this file also.";
            JOptionPane.showMessageDialog(null, message, "Exception occurred on " + th.getName(), JOptionPane.WARNING_MESSAGE);
        }
    }

    private String dumpToFile(Throwable ex) {
        Date date = new Date();
        String home_dir = System.getProperty("user.home");
        String timestamp = String.valueOf(date.getTime());
        String logFilePath = home_dir + File.separator + "err_log-" + timestamp + ".txt";
        File logFile = new File(logFilePath);

        try {
            PrintStream ps = new PrintStream(logFile);
            ps.println(App.getInstance().statusPanel.getController().getLog());
            ex.printStackTrace(ps);
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logFilePath;
    }

}
