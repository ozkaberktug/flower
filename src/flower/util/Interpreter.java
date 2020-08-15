package flower.util;

import flower.App;
import flower.model.elements.AbstractBlock;
import flower.model.elements.CommandBlock;
import flower.model.elements.IfBlock;
import flower.model.elements.InputBlock;
import flower.model.elements.Line;
import flower.model.elements.OutputBlock;
import flower.model.elements.StartBlock;
import flower.model.elements.StopBlock;

import javax.swing.JOptionPane;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter extends Thread {

    public boolean isRunning = false;
    private final HashMap<String, Double> symbolTable;


    public Interpreter() {
        setUncaughtExceptionHandler((thread, exception) -> {
            String[] msg = exception.getMessage().split("/");

            App.toolbarPanel.controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop"));
        });
        symbolTable = new HashMap<>();
    }

    @Override
    public void run() {
        // init environment
        isRunning = true;

        long beginTime = System.currentTimeMillis();

        // check if there are any blocks
        if (App.project.blocks.isEmpty()) throw new RuntimeException("No blocks./No block to process.");

        // first search for START block
        AbstractBlock currentBlock = null;
        for (AbstractBlock block : App.project.blocks) {
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
            Point ptBegin = decodeBlock(currentBlock);

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
                for (AbstractBlock block : App.project.blocks) {
                    Point[] inputPins = block.getInputPins();
                    if (inputPins != null && inputPins[0].equals(p)) {
                        if (currentBlock != null)
                            throw new RuntimeException("Parallel execution./Output of one block is connected to at least two other [ID1:" + currentBlock.getId() + " ID2:" + block.getId() + "] block input.");
                        currentBlock = block;
                    }
                }


                // find which line(s) contains this point
                for (Line line : App.project.lines) {
                    if (line.begin.equals(p) || line.end.equals(p)) {
                        if (!visited.contains(line.begin)) {
                            ss.push(line.begin);
                            visited.add(line.begin);
                        }
                        if (!visited.contains(line.end)) {
                            ss.push(line.end);
                            visited.add(line.end);
                        }
                    }
                }

            }
            if (currentBlock == null) throw new RuntimeException("Dangling block./Not connected to STOP block.");
            currentBlock.setProcessing(true);
            if (currentBlock instanceof StopBlock) isRunning = false;
        }
        double diffTime = (System.currentTimeMillis() - beginTime) / 1000.f;
        String.format("%.4f", diffTime);

        App.toolbarPanel.controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Stop"));
    }

    private Point decodeBlock(AbstractBlock block) {

        if (block instanceof StartBlock) {

            // no-processing return the output pin directly
            return block.getOutputPins()[0];

        } else if (block instanceof CommandBlock) {

            // line by line
            for (String code : block.getCode().split("\\n")) {

                Token[] tokens = getTokens(code.toCharArray());
                // Format in Command Block is: <id> = <expr>
                // therefore, first token is a variable and second token must be an equal sign!

                // error checks
                if (tokens.length < 3) throw new RuntimeException("syntax error / syntax error");
                if (tokens[0].type != Token.VARIABLE) throw new RuntimeException("var name invalid/var name invalid");
                if (!tokens[1].data.equals("=")) throw new RuntimeException("assignment error/equal sign not present");

                // do calc and put it assign
                symbolTable.put(tokens[0].data, evalExpr(tokens, 2));
            }

            // move to next block
            return block.getOutputPins()[0];

        } else if (block instanceof OutputBlock) {

            // get comma separated values (if any)
            if (block.getCode().contains(",")) {
                for (String expr : block.getCode().split(",")) {
                    String msg = expr + " is " + evalExpr(getTokens(expr.toCharArray()), 0);
                    JOptionPane.showMessageDialog(null, msg);
                }
            } else {
                String msg = block.getCode() + " is " + evalExpr(getTokens(block.getCode().toCharArray()), 0);
                JOptionPane.showMessageDialog(null, msg);
            }

            // move to next block
            return block.getOutputPins()[0];

        } else if (block instanceof InputBlock) {

            // get comma separated values (if any)
            if (block.getCode().contains(",")) {
                for (String expr : block.getCode().split(",")) {
                    Token[] t = getTokens(expr.toCharArray());
                    if (t.length != 1 && t[0].type != Token.VARIABLE) throw new RuntimeException("invalid var name!");
                    String msg = "Please enter a value for " + expr;
                    String value = JOptionPane.showInputDialog(null, msg);
                    symbolTable.put(t[0].data, Double.parseDouble(value));
                }
            } else {
                Token[] t = getTokens(block.getCode().toCharArray());
                if (t.length != 1 && t[0].type != Token.VARIABLE) throw new RuntimeException("invalid var name!");
                String msg = "Please enter a value for " + block.getCode();
                String value = JOptionPane.showInputDialog(null, msg);
                symbolTable.put(t[0].data, Double.parseDouble(value));
            }

            // move to next block
            return block.getOutputPins()[0];

        } else if (block instanceof IfBlock) {

            // the syntax for if block is: <expr> <comp_op> <expr>
            // therefore first find <comp_op> position
            Token[] tokens = getTokens(block.getCode().toCharArray());
            boolean b = false;
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].type == Token.COMPARE) {
                    double left = evalExpr(tokens, 0, i);
                    double right = evalExpr(tokens, i + 1);
                    b = Token.compare(tokens[i].data, left, right);
                    break;
                }
            }

            // move to next block
            return (b) ? block.getOutputPins()[IfBlock.TRUE_OUTPUT] : block.getOutputPins()[IfBlock.FALSE_OUTPUT];

        }
        return null;
    }

    private double evalExpr(Token[] tokens, int start) {
        return evalExpr(tokens, start, tokens.length);
    }

    private double evalExpr(Token[] tokens, int start, int stop) {
        Stack<Double> operand = new Stack<>();
        Stack<Token> operator = new Stack<>();

        for (int i = start; i < stop; i++) {
            Token token = tokens[i];

            // token is operand
            if (token.type == Token.VARIABLE || token.type == Token.NUMBER) {
                if (token.type == Token.VARIABLE) {
                    if (!symbolTable.containsKey(token.data)) throw new RuntimeException("Variable/not known!");
                    operand.push(symbolTable.get(token.data));
                }
                if (token.type == Token.NUMBER) {
                    operand.push(Double.parseDouble(token.data));
                }
            }

            // if left parenthesis push into stack
            else if (token.type == Token.LEFT_PARENTHESIS) {
                operator.push(token);
            }

            // if token is operator
            else if (token.type == Token.OPERATOR) {
                // while the top of the operator stack is not of smaller precedence than this character
                while (!operator.isEmpty() && Token.hasGreaterPred(operator.peek(), token)) {
                    Token op = operator.pop();
                    double right_val = operand.pop();
                    double left_val = operand.pop();
                    operand.push(Token.compute(op, left_val, right_val));
                }
                operator.push(token);
            }

            // if token is right parenthesis
            else if (token.type == Token.RIGHT_PARENTHESIS) {
                while (operator.peek().type != Token.LEFT_PARENTHESIS) {
                    Token op = operator.pop();
                    double right_val = operand.pop();
                    double left_val = operand.pop();
                    operand.push(Token.compute(op, left_val, right_val));
                }
                operator.pop(); // pop left parenthesis
            }

        }

        // no more token to read
        while (!operator.isEmpty()) {
            Token op = operator.pop();
            double right_val = operand.pop();
            double left_val = operand.pop();
            operand.push(Token.compute(op, left_val, right_val));
        }

        return operand.peek();
    }

    private Token[] getTokens(char[] text) {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < text.length) {

            StringBuilder strToken = new StringBuilder();
            Token token = new Token();

            if (Character.isWhitespace(text[i])) {
                i++;
            } else if (text[i] == '+' || text[i] == '-' || text[i] == '*' || text[i] == '/') {
                strToken.append(text[i]);
                token.data = strToken.toString();
                token.type = Token.OPERATOR;
                tokens.add(token);
                i++;
            } else if ((text[i] == '<' && text[i + 1] != '=') || (text[i] == '>' && text[i + 1] != '=')) {
                strToken.append(text[i]);
                token.data = strToken.toString();
                token.type = Token.COMPARE;
                tokens.add(token);
                i++;
            } else if ((text[i] == '=' && text[i + 1] == '=') || (text[i] == '!' && text[i + 1] == '=') || (text[i] == '<' && text[i + 1] == '=') || (text[i] == '>' && text[i + 1] == '=')) {
                strToken.append(text[i]);
                strToken.append(text[i + 1]);
                token.data = strToken.toString();
                token.type = Token.COMPARE;
                tokens.add(token);
                i += 2;
            } else if ((text[i] == '=' && text[i + 1] != '=') || text[i] == ',') {
                strToken.append(text[i]);
                token.data = strToken.toString();
                token.type = Token.OTHER;
                tokens.add(token);
                i++;
            } else if (text[i] == '(') {
                strToken.append(text[i]);
                token.data = strToken.toString();
                token.type = Token.LEFT_PARENTHESIS;
                tokens.add(token);
                i++;
            } else if (text[i] == ')') {
                strToken.append(text[i]);
                token.data = strToken.toString();
                token.type = Token.RIGHT_PARENTHESIS;
                tokens.add(token);
                i++;
            } else if (Character.isDigit(text[i])) {
                while (i < text.length) {
                    if (!(Character.isDigit(text[i]) || text[i] == '.')) break;
                    strToken.append(text[i]);
                    i++;
                }
                token.data = strToken.toString();
                token.type = Token.NUMBER;
                tokens.add(token);
            } else if (Character.isAlphabetic(text[i])) {
                while (i < text.length) {
                    if (!(Character.isDigit(text[i]) || Character.isAlphabetic(text[i]))) break;
                    strToken.append(text[i]);
                    i++;
                }
                token.data = strToken.toString();
                token.type = Token.VARIABLE;
                tokens.add(token);
            } else {
                throw new RuntimeException("Unidentified character./Could not parse: " + Arrays.toString(text));
            }

        }

        return tokens.toArray(new Token[0]);
    }

}