package flower;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Interpreter extends Thread {

    private final App app;

    public boolean isRunning = false;

    public Interpreter(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        isRunning = true;
        app.statusPanel.title = "Running...";
        app.statusPanel.texts.add("Simulation started at " + dtf.format(LocalDateTime.now()));
        app.statusPanel.updateLog();
        long beginTime = System.currentTimeMillis();
        while (isRunning) {
            if (Thread.currentThread().isInterrupted()) {
                isRunning = false;
                break;
            }
            // todo
        }
        double diffTime = (System.currentTimeMillis() - beginTime) / 1000.f;
        app.statusPanel.title = "Finished in " + String.format("%.2f", diffTime) + " seconds.";
        app.statusPanel.texts.add("Simulation finished. Took " + String.format("%.4f", diffTime) + " seconds to complete.");
        app.statusPanel.updateLog();
    }

}
