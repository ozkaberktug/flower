package flower.model;

import flower.App;
import flower.view.StatusPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import static flower.view.ViewConstants.TILESIZE;


public class Project {

    public final ArrayList<AbstractBlock> blocks;
    public final ArrayList<Line> lines;
    public String name;
    public String inputParams;
    public final ArrayList<Project> libs;

    public Project() {
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
                    export(chooser.getSelectedFile());
                }
            } else {
                export(chooser.getSelectedFile());
            }
        }
    }

    public void showOpenDialog(App app) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Flower Projects (*.fp)", "fp"));
        if (chooser.showOpenDialog(app) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                open(chooser.getSelectedFile());
                app.setTitle("flower - " + name);
            } else {
                JOptionPane.showMessageDialog(app, "No such file!");
            }
        }
    }

    public void showSaveDialog(App app) {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Flower Projects (*.fp)", "fp"));
        if (chooser.showSaveDialog(app) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                if (JOptionPane.showConfirmDialog(app, "Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    save(chooser.getSelectedFile());
                    app.setTitle("flower - " + name);
                }
            } else {
                save(chooser.getSelectedFile());
            }
        }
    }

    private void save(File ff) {

        App.statusPanel.appendLog("Writing data...", "", StatusPanel.INFO_MSG);

        // correct file extension
        if (!ff.getName().toUpperCase().endsWith(".FP")) ff = new File(ff.getAbsolutePath() + ".fp");

        name = ff.getName().substring(0, ff.getName().length() - 3);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // app tag
            Element appTag = doc.createElement("flower");
            appTag.setAttribute("version", App.version_string);
            doc.appendChild(appTag);

            // project tag and its attr
            Element projectTag = doc.createElement("project");
            projectTag.setAttribute("name", name);
            projectTag.setAttribute("inputParams", inputParams);
            appTag.appendChild(projectTag);

            // lines tag
            Element linesTag = doc.createElement("lines");
            projectTag.appendChild(linesTag);

            // iterate lines
            for (Line line : lines) {
                Element lineTag = doc.createElement("line");
                lineTag.setAttribute("pos", String.format("%d,%d,%d,%d", line.begin.x, line.begin.y, line.end.x, line.end.y));
                linesTag.appendChild(lineTag);
            }

            // blocks tag
            Element blocksTag = doc.createElement("blocks");
            projectTag.appendChild(blocksTag);

            // iterate blocks
            for (AbstractBlock block : blocks) {
                Element blockTag = doc.createElement("block");
                Rectangle area = block.getInnerBounds();
                blockTag.setAttribute("id", String.valueOf(block.getId()));
                blockTag.setAttribute("type", String.valueOf(block.getType()));
                blockTag.setAttribute("breakpoint", String.valueOf(block.isBreakpoint()));
                blockTag.setAttribute("area", String.format("%d,%d,%d,%d", area.x, area.y, area.width, area.height));
                blockTag.setAttribute("code", block.getCode());
                blocksTag.appendChild(blockTag);
            }

            // todo: in the future libs tag will be added

            // write the content
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(ff);
            transformer.transform(source, result);

            // inform user
            App.statusPanel.appendLog("Project saved!", "Project saved to " + ff.getAbsolutePath(), StatusPanel.INFO_MSG);

        } catch (Exception e) {
            App.statusPanel.appendLog("File could not read!", e.getMessage(), StatusPanel.ERROR_MSG);
        }

    }

    private void open(File ff) {

        // clear project
        clear();

        App.statusPanel.appendLog("Reading data...", "", StatusPanel.INFO_MSG);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(ff);
            doc.getDocumentElement().normalize();

            // get root node
            Element rootNode = doc.getDocumentElement();

            // check integrity
            if (!rootNode.getTagName().equals("flower")) {
                throw new RuntimeException("Not a valid FP file");
            }
            if (!rootNode.getAttribute("version").equals(App.version_string)) {
                throw new RuntimeException("File version (" + rootNode.getAttribute("version") + ") not supported!");
            }

            // get project node
            Element projectNode = (Element) rootNode.getElementsByTagName("project").item(0);
            name = projectNode.getAttribute("name");
            inputParams = projectNode.getAttribute("inputParams");

            // get lines
            NodeList linesNodeList = projectNode.getElementsByTagName("lines").item(0).getChildNodes();
            for (int i = 0; i < linesNodeList.getLength(); i++) {
                if (linesNodeList.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
                Element lineNode = (Element) linesNodeList.item(i);
                String[] points = lineNode.getAttribute("pos").split(",");
                lines.add(new Line(Integer.parseInt(points[0]), Integer.parseInt(points[1]), Integer.parseInt(points[2]), Integer.parseInt(points[3])));
            }

            // get blocks
            NodeList blocksNodeList = projectNode.getElementsByTagName("blocks").item(0).getChildNodes();
            for (int i = 0; i < blocksNodeList.getLength(); i++) {
                if (blocksNodeList.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
                Element blockNode = (Element) blocksNodeList.item(i);
                String[] area = blockNode.getAttribute("area").split(",");
                boolean breakpoint = Boolean.parseBoolean(blockNode.getAttribute("breakpoint"));
                int id = Integer.parseInt(blockNode.getAttribute("id"));
                int type = Integer.parseInt(blockNode.getAttribute("type"));
                String code = blockNode.getAttribute("code");
                AbstractBlock block;
                switch (type) {
                    case AbstractBlock.COMMAND_BLOCK:
                        block = new CommandBlock();
                        break;
                    case AbstractBlock.IF_BLOCK:
                        block = new IfBlock();
                        break;
                    case AbstractBlock.INPUT_BLOCK:
                        block = new InputBlock();
                        break;
                    case AbstractBlock.LABEL_BLOCK:
                        block = new LabelBlock();
                        break;
                    case AbstractBlock.OUTPUT_BLOCK:
                        block = new OutputBlock();
                        break;
                    case AbstractBlock.START_BLOCK:
                        block = new StartBlock();
                        break;
                    case AbstractBlock.STOP_BLOCK:
                        block = new StopBlock();
                        break;
                    default:
                        throw new RuntimeException();
                }
                block.setBreakpoint(breakpoint);
                block.setId(id);
                block.setInnerBounds(new Rectangle(Integer.parseInt(area[0]), Integer.parseInt(area[1]), Integer.parseInt(area[2]), Integer.parseInt(area[3])));
                block.setCode(code);
                block.normalizeSize();
                blocks.add(block);
            }

            // todo: in the future libs tag will be added

            // inform user
            App.statusPanel.appendLog("Project loaded!", "Project loaded from " + ff.getAbsolutePath(), StatusPanel.INFO_MSG);

        } catch (Exception e) {
            App.statusPanel.appendLog("File could not read!", e.getMessage(), StatusPanel.ERROR_MSG);
        }

    }

    private void export(File ff) {
        if (!ff.getName().toUpperCase().endsWith(".PNG")) ff = new File(ff.getAbsolutePath() + ".png");

        AffineTransform af = new AffineTransform(1, 0, 0, 1, 0, 0);
        Point ULC = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point LRC = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        final int scalingFactor = 4;

        // scale up to get better quality
        af.scale(scalingFactor, scalingFactor);

        // check if there is anything
        if (lines.isEmpty() && blocks.isEmpty()) {
            App.statusPanel.appendLog("Export failed.", "Nothing on the chart.", StatusPanel.ERROR_MSG);
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

        final int canvasWidth = (width + 2 * TILESIZE) * scalingFactor;
        final int canvasHeight = (height + 2 * TILESIZE) * scalingFactor;

        // create and set image object
        BufferedImage bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // create a background
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, canvasWidth, canvasHeight);

        // draw each element
        graphics2D.setTransform(af);
        for (Line line : lines) line.draw(graphics2D);
        for (AbstractBlock ab : blocks) ab.draw(graphics2D);
        graphics2D.dispose();

        // save and return
        try {
            ImageIO.write(bufferedImage, "PNG", ff);
        } catch (Exception e) {
            App.statusPanel.appendLog("Export failed.", e.getMessage(), StatusPanel.ERROR_MSG);
            return;
        }
        // notify the UI
        App.statusPanel.appendLog("Flowchart exported successfully.", "Exported file: " + ff.getAbsolutePath(), StatusPanel.PLAIN_MSG);
    }

    public void clear() {
        lines.clear();
        blocks.clear();
        name = "Untitled";
        inputParams = "";
        libs.clear();
    }

}
