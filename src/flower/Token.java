package flower;

public class Token {
    public static final int NUMBER = 1;
    public static final int VARIABLE = 2;
    public static final int OPERATOR = 3;
    public static final int LEFT_PARENTHESIS = 4;
    public static final int RIGHT_PARENTHESIS = 5;
    public static final String[] op_table = {"+", "-", "*", "/"};
    public static final String[] comp_table = {">", "<", "==", "!=", "<=", ">="};
    public static final int[] pred_table = {0, 0, 1, 1};
    public static final double EPSILON = 0.000001;

    public String data;
    public int type;

    public static boolean hasGreaterPred(String t1, String t2) {
        int pred1 = 0, pred2 = 0;
        for (int i = 0; i < op_table.length; i++) {
            if (t1.equals(op_table[i])) pred1 = i;
            if (t2.equals(op_table[i])) pred2 = i;
        }
        // will return false for compare operations
        return (pred_table[pred1] - pred_table[pred2] > 0);
    }

    public static double compute(String op, double left_operand, double right_operand) {
        double fRetVal;
        switch (op) {
            case "+":
                fRetVal = right_operand + left_operand;
                break;
            case "-":
                fRetVal = right_operand - left_operand;
                break;
            case "*":
                fRetVal = right_operand * left_operand;
                break;
            case "/":
                fRetVal = right_operand / left_operand;
                break;
            default:
                throw new RuntimeException("Unknown error./Something bad happened!");
        }
        return fRetVal;
    }

    public static boolean compare(String op, double left_operand, double right_operand) {
        boolean bRetVal;
        switch (op) {
            case "<":
                bRetVal = right_operand < left_operand;
                break;
            case "<=":
                bRetVal = right_operand <= left_operand;
                break;
            case ">":
                bRetVal = right_operand > left_operand;
                break;
            case ">=":
                bRetVal = right_operand >= left_operand;
                break;
            case "==":
                bRetVal = Math.abs(right_operand - left_operand) <= EPSILON;
                break;
            case "!=":
                bRetVal = Math.abs(right_operand - left_operand) > EPSILON;
                break;
            default:
                throw new RuntimeException("Unknown error./Something bad happened!");
        }
        return bRetVal;
    }

}