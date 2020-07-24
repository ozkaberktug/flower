package flower;

public class Token {
    public static final int NUMBER = 1;
    public static final int VARIABLE = 2;
    public static final int OPERATOR = 3;
    public static final int LEFT_PARENTHESIS = 4;
    public static final int RIGHT_PARENTHESIS = 5;
    public static final String[] op_table = {"+", "-", "*", "/"};
    public static final int[] pred_table = {0, 0, 1, 1};

    public String data;
    public int type;

    public static boolean hasGreaterPred(String t1, String t2) {
        int pred1 = 0, pred2 = 0;
        for (int i = 0; i < op_table.length; i++) {
            if (t1.equals(op_table[i])) pred1 = i;
            if (t2.equals(op_table[i])) pred2 = i;
        }
        return (pred_table[pred1] - pred_table[pred2] > 0);
    }

    public static double compute(String op, double right_operand, double left_operand) {
        double retVal;
        switch (op) {
            case "+":
                retVal = left_operand + right_operand;
                break;
            case "-":
                retVal = left_operand - right_operand;
                break;
            case "*":
                retVal = left_operand * right_operand;
                break;
            case "/":
                retVal = left_operand / right_operand;
                break;
            default:
                throw new RuntimeException("Unknown error./Something bad happened!");
        }
        return retVal;
    }

}