package flower;

import flower.blocks.*;
import jdk.internal.util.xml.impl.Input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Stack;

public class Interpreter extends Thread {

    private final App app;
    public boolean isRunning = false;


    public Interpreter(App app) {
        this.app = app;
        setUncaughtExceptionHandler((thread, exception) -> {
            String[] msg = exception.getMessage().split("/");
            this.app.statusPanel.title = "Error: " + msg[0];
            this.app.statusPanel.texts.add("An error occurred: " + msg[1]);
            this.app.statusPanel.updateLog();
            this.app.toolbarPanel.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop"));
        });
    }

    @Override
    public void run() {
        // init environment
        isRunning = true;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        app.statusPanel.title = "Running...";
        app.statusPanel.texts.add("Simulation started at " + dtf.format(LocalDateTime.now()));
        app.statusPanel.updateLog();
        long beginTime = System.currentTimeMillis();

        // check if there are any blocks
        if (app.project.blocks.isEmpty()) throw new RuntimeException("No blocks./No block to process.");

        // first search for START block
        AbstractBlock currentBlock = null;
        for (AbstractBlock block : app.project.blocks) {
            if (block instanceof StartBlock) {
                if (currentBlock == null) currentBlock = block;
                else throw new RuntimeException("Multiple START blocks./Multiple start blocks found!");
            }
        }
        if (currentBlock == null)
            throw new RuntimeException("No START block./There is no entry point in the flowchart.");

        currentBlock.setProcessing(true);

        // fetch and decode cycle
        while (isRunning) {

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // decode and get the output pin
            Point ptBegin = decode(currentBlock);

            // null out the currentBlock for the next stage
            currentBlock.setProcessing(false);
            currentBlock = null;

            // use DFS to find next block
            boolean foundBlock = false;
            Stack<Point> ss = new Stack<>();
            ArrayList<Point> visited = new ArrayList<>();
            ss.push(ptBegin);
            while (!ss.empty()) {
                Point p = ss.pop();
                visited.add(p);

                // check if we found a block input pin
                for (AbstractBlock block : app.project.blocks) {
                    if ((!(block instanceof StartBlock || block instanceof LabelBlock)) && block.getInputPins()[0].equals(p)) {
                        if (currentBlock != null)
                            throw new RuntimeException("Parallel execution./Output of one block is connected to at least two other block input.");
                        currentBlock = block;
                    }
                }


                // find which line contains this point
                for (Line line : app.project.lines) {
                    if (line.begin.equals(p)) {
                        if (!visited.contains(line.end)) ss.push(line.end);
                        for (Point ptHub : line.hub) if (!visited.contains(ptHub)) ss.push(ptHub);
                    } else if (line.end.equals(p)) {
                        if (!visited.contains(line.begin)) ss.push(line.begin);
                        for (Point ptHub : line.hub) if (!visited.contains(ptHub)) ss.push(ptHub);
                    }
                }

            }
            if (currentBlock == null) throw new RuntimeException("Dangling block./Not connected to STOP block.");
            currentBlock.setProcessing(true);
            if (currentBlock instanceof StopBlock) isRunning = false;
        }
        double diffTime = (System.currentTimeMillis() - beginTime) / 1000.f;
        app.statusPanel.title = "Finished in " + String.format("%.2f", diffTime) + " seconds.";
        app.statusPanel.texts.add("Simulation finished. Took " + String.format("%.4f", diffTime) + " seconds to complete.");
        app.statusPanel.updateLog();
        app.toolbarPanel.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop"));
    }

    private Point decode(AbstractBlock block) {
        if (block instanceof StartBlock) return block.getOutputPins()[0];
        else if (block instanceof CommandBlock) {
            //todo
            return block.getOutputPins()[0];
        }
        else if (block instanceof OutputBlock) {
            //todo
            return block.getOutputPins()[0];
        }
        else if (block instanceof InputBlock) {
            //todo
            return block.getOutputPins()[0];
        }
        else if (block instanceof IfBlock) {
            //todo
            return block.getOutputPins()[0];
        }
        return null;
    }

}
