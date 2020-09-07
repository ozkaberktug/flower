package flower.util;

import flower.App;
import flower.controller.StatusPanelController;
import flower.model.elements.AbstractBlock;
import flower.model.elements.CommandBlock;
import flower.model.elements.IfBlock;
import flower.model.elements.InputBlock;
import flower.model.elements.LabelBlock;
import flower.model.elements.Line;
import flower.model.elements.OutputBlock;
import flower.model.elements.StartBlock;
import flower.model.elements.StopBlock;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
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

import static flower.view.ViewConstants.TILESIZE;

public class FileOperations {

    public static void save(File ff) {
        App.getInstance().statusPanel.controller.setStatus("Saving...", StatusPanelController.INFO);
        App.getInstance().statusPanel.controller.pushLog("Saving project...", StatusPanelController.INFO);

        // correct file extension
        if (!ff.getName().toUpperCase().endsWith(".FP")) ff = new File(ff.getAbsolutePath() + ".fp");

        App.getInstance().project.name = ff.getName().substring(0, ff.getName().length() - 3);

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
            projectTag.setAttribute("name", App.getInstance().project.name);
            projectTag.setAttribute("inputParams", App.getInstance().project.inputParams);
            appTag.appendChild(projectTag);

            // lines tag
            Element linesTag = doc.createElement("lines");
            projectTag.appendChild(linesTag);

            // iterate lines
            for (Line line : App.getInstance().project.lines) {
                Element lineTag = doc.createElement("line");
                lineTag.setAttribute("pos", String.format("%d,%d,%d,%d", line.begin.x, line.begin.y, line.end.x, line.end.y));
                linesTag.appendChild(lineTag);
            }

            // blocks tag
            Element blocksTag = doc.createElement("blocks");
            projectTag.appendChild(blocksTag);

            // iterate blocks
            for (AbstractBlock block : App.getInstance().project.blocks) {
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
            App.getInstance().statusPanel.controller.setStatus("Project saved successfully", StatusPanelController.INFO);
            App.getInstance().statusPanel.controller.pushLog("Project saved to " + ff.getAbsolutePath(), StatusPanelController.INFO);

        } catch (Exception e) {
            App.getInstance().exceptionHandler.handle(e, ExceptionHandler.NORMAL);
            App.getInstance().statusPanel.controller.setStatus("Project could not saved", StatusPanelController.ERROR);
            App.getInstance().statusPanel.controller.pushLog("Project could not saved due to " + e.toString(), StatusPanelController.ERROR);
        }

    }

    public static void open(File ff) {
        App.getInstance().statusPanel.controller.setStatus("Loading...", StatusPanelController.INFO);
        App.getInstance().statusPanel.controller.pushLog("Loading project", StatusPanelController.INFO);

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
            App.getInstance().project.name = projectNode.getAttribute("name");
            App.getInstance().project.inputParams = projectNode.getAttribute("inputParams");

            // get lines
            NodeList linesNodeList = projectNode.getElementsByTagName("lines").item(0).getChildNodes();
            for (int i = 0; i < linesNodeList.getLength(); i++) {
                if (linesNodeList.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
                Element lineNode = (Element) linesNodeList.item(i);
                String[] points = lineNode.getAttribute("pos").split(",");
                App.getInstance().project.lines.add(new Line(Integer.parseInt(points[0]), Integer.parseInt(points[1]), Integer.parseInt(points[2]), Integer.parseInt(points[3])));
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
                App.getInstance().project.blocks.add(block);
            }

            // todo: in the future libs tag will be added

            App.getInstance().drawPanel.controller.relocate();

            // inform user
            App.getInstance().statusPanel.controller.setStatus("Project loaded successfully", StatusPanelController.INFO);
            App.getInstance().statusPanel.controller.pushLog("Project loaded from " + ff.getAbsolutePath(), StatusPanelController.INFO);

        } catch (Exception e) {
            App.getInstance().exceptionHandler.handle(e, ExceptionHandler.NORMAL);
            App.getInstance().statusPanel.controller.setStatus("Project could not load", StatusPanelController.ERROR);
            App.getInstance().statusPanel.controller.pushLog("Project could not load due to " + e.toString(), StatusPanelController.ERROR);
        }

    }

    public static void export(File ff) {
        App.getInstance().statusPanel.controller.setStatus("Exporting...", StatusPanelController.INFO);
        App.getInstance().statusPanel.controller.pushLog("Exporting project...", StatusPanelController.INFO);

        if (!ff.getName().toUpperCase().endsWith(".PNG")) ff = new File(ff.getAbsolutePath() + ".png");

        AffineTransform af = new AffineTransform(1, 0, 0, 1, 0, 0);
        Point ULC = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point LRC = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        final int scalingFactor = 4;

        // scale up to get better quality
        af.scale(scalingFactor, scalingFactor);

        // check if there is anything
        if (App.getInstance().project.lines.isEmpty() && App.getInstance().project.blocks.isEmpty()) {
            App.getInstance().statusPanel.controller.setStatus("There is no element to be export", StatusPanelController.ERROR);
            App.getInstance().statusPanel.controller.pushLog("Project could not exported because there is no element", StatusPanelController.ERROR);
            return;
        }

        // check line bounds
        for (Line line : App.getInstance().project.lines) {
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
        for (AbstractBlock ab : App.getInstance().project.blocks) {
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
        for (Line line : App.getInstance().project.lines) line.draw(graphics2D);
        for (AbstractBlock ab : App.getInstance().project.blocks) ab.draw(graphics2D);
        graphics2D.dispose();

        // save and return
        try {
            ImageIO.write(bufferedImage, "PNG", ff);

        } catch (Exception e) {
            App.getInstance().exceptionHandler.handle(e, ExceptionHandler.NORMAL);
            App.getInstance().statusPanel.controller.setStatus("Project could not export", StatusPanelController.ERROR);
            App.getInstance().statusPanel.controller.pushLog("Project could not export due to " + e.toString(), StatusPanelController.ERROR);
        }

        App.getInstance().statusPanel.controller.setStatus("Project exported successfully", StatusPanelController.INFO);
        App.getInstance().statusPanel.controller.pushLog("Project exported to " + ff.getAbsolutePath(), StatusPanelController.INFO);
    }

}
