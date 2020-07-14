package flower;

import flower.blocks.AbstractBlock;
import flower.blocks.Line;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static flower.DrawPanel.*;

public class Project {

    private final App app;

    public final ArrayList<AbstractBlock> blocks;
    public final ArrayList<Line> lines;
    public final String name;
    public final String inputParams;
    public final ArrayList<Project> libs;

    public Project(App app) {
        this.app = app;
        lines = new ArrayList<>();
        blocks = new ArrayList<>();
        name = "Untitled";
        inputParams = "";
        libs = new ArrayList<>();
    }

    public void showExportDialog(App app) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("PNG files", "png"));
        if (chooser.showSaveDialog(app) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(app, "Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    app.project.export(chooser.getSelectedFile());
                }
            } else {
                app.project.export(chooser.getSelectedFile());
            }
        }
    }

    private void export(File ff) {
        AffineTransform af = new AffineTransform(1, 0, 0, 1, 0, 0);
        Point ULC = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point LRC = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        // check if there is anything
        if (lines.isEmpty() && blocks.isEmpty()) {
            app.statusPanel.appendLog("Export failed.", "Nothing on the chart.", StatusPanel.ERROR_MSG);
            return;
        }

        // check line bounds
        for (Line line : lines) {
            if (line.begin.x < ULC.x) ULC.x = line.begin.x;
            if (line.begin.y < ULC.y) ULC.y = line.begin.y;
            if (line.end.x < ULC.x) ULC.x = line.end.x;
            if (line.end.y < ULC.y) ULC.y = line.end.y;
            if (line.begin.x > LRC.x) LRC.x = line.begin.x;
            if (line.begin.y > LRC.y) LRC.y = line.begin.y;
            if (line.end.x > LRC.x) LRC.x = line.end.x;
            if (line.end.y > LRC.y) LRC.y = line.end.y;
        }

        // check block bounds
        for (AbstractBlock ab : blocks) {
            Rectangle bound = ab.getInnerBounds();
            if (bound.x < ULC.x) ULC.x = bound.x;
            if (bound.y < ULC.y) ULC.y = bound.y;
            if (bound.x + bound.width > LRC.x) LRC.x = bound.x + bound.width;
            if (bound.y + bound.height > LRC.y) LRC.y = bound.y + bound.height;
        }

        // move the drawing to 0,0 position
        final int width = Math.abs(LRC.x - ULC.x) * TILESIZE;
        final int height = Math.abs(LRC.y - ULC.y) * TILESIZE;
        af.translate(-(ULC.x - 1) * TILESIZE, -(ULC.y - 1) * TILESIZE);

        // draw the image
        BufferedImage bufferedImage = new BufferedImage(width + 2 * TILESIZE, height + 2 * TILESIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, width + 2 * TILESIZE, height + 2 * TILESIZE);
        graphics2D.setTransform(af);
        graphics2D.setStroke(BOLD_STROKE);
        graphics2D.setColor(Color.BLACK);
        for (Line line : lines) line.draw(graphics2D);
        for (AbstractBlock ab : blocks) ab.draw(graphics2D);
        graphics2D.dispose();

        // save and return
        try {
            if (!ff.getName().toUpperCase().endsWith("PNG")) ff = new File(ff.getAbsolutePath() + ".png");
            ImageIO.write(bufferedImage, "PNG", ff);
        } catch (Exception e) {
            app.statusPanel.appendLog("Export failed.", e.getMessage(), StatusPanel.ERROR_MSG);
            return;
        }
        app.statusPanel.appendLog("Flowchart exported successfully.", "Exported file: " + ff.getAbsolutePath(), StatusPanel.PLAIN_MSG);
    }

}
