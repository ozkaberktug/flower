package flower;

import flower.blocks.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Stack;

public class Interpreter extends Thread {

    private final App app;
    public boolean isRunning = false;


    public Interpreter(App app) {
        this.app = app;
        setUncaughtExceptionHandler((thread, exception) -> {
            String[] msg = exception.getMessage().split("/");
            this.app.statusPanel.appendLog(msg[0], msg[1], StatusPanel.ERROR_MSG);
            this.app.toolbarPanel.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop"));
        });
    }

    @Override
    public void run() {
        // init environment
        isRunning = true;
        app.statusPanel.appendLog("Running...", "Simulation started.", StatusPanel.INFO_MSG);
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

            // decode and get the output pin
            Point ptBegin = decode(currentBlock);

            // null out the currentBlock for the next stage
            currentBlock.setProcessing(false);
            currentBlock = null;

            // use DFS to find next block
            Stack<Point> ss = new Stack<>();
            ArrayList<Point> visited = new ArrayList<>();
            ss.push(ptBegin);
            while (!ss.empty()) {
                Point p = ss.pop();
                visited.add(p);

                // check if we found a block input pin
                for (AbstractBlock block : app.project.blocks) {
                    Point[] inputPins = block.getInputPins();
                    if (inputPins != null && inputPins[0].equals(p)) {
                        if (currentBlock != null)
                            throw new RuntimeException("Parallel execution./Output of one block is connected to at least two other block input.");
                        currentBlock = block;
                    }
                }


//                // find which line(s) contains this point
//                for (Line line : app.project.lines) {
//                    if (line.begin.equals(p) || line.end.equals(p)) {
//                        for(Line l : app.project.hubs.get(p)) {
//                            if(!visited.contains(l.begin)) ss.push(l.begin);
//                            if(!visited.contains(l.end)) ss.push(l.end);
//                        }
//                    }
//                }

            }
            if (currentBlock == null) throw new RuntimeException("Dangling block./Not connected to STOP block.");
            currentBlock.setProcessing(true);
            if (currentBlock instanceof StopBlock) isRunning = false;
        }
        double diffTime = (System.currentTimeMillis() - beginTime) / 1000.f;
        app.statusPanel.appendLog("Simulation finished.", "Took " + String.format("%.4f", diffTime) + " seconds to complete.", StatusPanel.INFO_MSG);
        app.toolbarPanel.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop"));
    }

    private Point decode(AbstractBlock block) {
        if (block instanceof StartBlock) return block.getOutputPins()[0];
        else if (block instanceof CommandBlock) {
            //todo
            return block.getOutputPins()[0];
        } else if (block instanceof OutputBlock) {
            //todo
            return block.getOutputPins()[0];
        } else if (block instanceof InputBlock) {
            //todo
            return block.getOutputPins()[0];
        } else if (block instanceof IfBlock) {
            //todo
            return block.getOutputPins()[0];
        }
        return null;
    }

}
