package flower;

public class Interpreter extends Thread {

    private final App app;

    public boolean isRunning = false;

    public Interpreter(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        isRunning = true;
        System.out.println("Thread started");
        while (isRunning) {
            if (Thread.currentThread().isInterrupted()) {
                isRunning = false;
                break;
            }
            // todo
//            System.out.println("?");
        }
        System.out.println("Thread killed");
    }

}
