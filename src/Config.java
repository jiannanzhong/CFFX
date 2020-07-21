import java.util.HashMap;

public class Config {
    public final static int WORD_TYPE_D_KEYWORD = 1;
    public final static int WORD_TYPE_S_KEYWORD = 2;
    public final static int WORD_TYPE_O_KEYWORD = 3;
    public final static int WORD_TYPE_C_KEYWORD = 4;
    public final static int WORD_TYPE_IDENTIFIER = 6;
    public final static int WORD_TYPE_RELATION_OPERATOR = 7;
    public final static int WORD_TYPE_COMPARE_OPERATOR = 8;
    public final static int WORD_TYPE_ASSIGNMENT_OPERATORS = 9;
    public final static int WORD_TYPE_LOGICAL_OPERATOR = 10;
    public final static int WORD_TYPE_ARITHMETIC_OPERATOR = 11;
    public final static int WORD_TYPE_UNARY_OPERATOR = 12;
    public final static int WORD_TYPE_DECIMAL_INTEGER = 13;
    public final static int WORD_TYPE_DELIMITER = 14;
    public final static int WORD_TYPE_SEMICOLON = 15;
    public final static int WORD_TYPE_COLON = 16;
    public final static int WORD_TYPE_COMMA = 17;
    public final static int WORD_TYPE_CONTENT_BETWEEN_QUOTATION_MARKS = 20;
    public final static int WORD_TYPE_DECIMAL_FLOAT = 21;
    public final static int WORD_TYPE_HEX_INTEGER = 22;

    public final static HashMap<Integer, String> word_name_map = new HashMap<>();

    static {
        word_name_map.put(WORD_TYPE_D_KEYWORD, "数据类型关键字");
        word_name_map.put(WORD_TYPE_S_KEYWORD, "存储类型关键字");
        word_name_map.put(WORD_TYPE_O_KEYWORD, "其他关键字");
        word_name_map.put(WORD_TYPE_C_KEYWORD, "控制语句关键字");
        word_name_map.put(WORD_TYPE_IDENTIFIER, "标识符");
        word_name_map.put(WORD_TYPE_RELATION_OPERATOR, "关系运算符");
        word_name_map.put(WORD_TYPE_COMPARE_OPERATOR, "相等运算符");
        word_name_map.put(WORD_TYPE_ASSIGNMENT_OPERATORS, "赋值运算符");
        word_name_map.put(WORD_TYPE_LOGICAL_OPERATOR, "逻辑运算符");
        word_name_map.put(WORD_TYPE_ARITHMETIC_OPERATOR, "算数运算符");
        word_name_map.put(WORD_TYPE_UNARY_OPERATOR, "单目运算符");
        word_name_map.put(WORD_TYPE_DECIMAL_INTEGER, "10进制整数");
        word_name_map.put(WORD_TYPE_DELIMITER, "界符");
        word_name_map.put(WORD_TYPE_SEMICOLON, "分号");
        word_name_map.put(WORD_TYPE_COLON, "冒号");
        word_name_map.put(WORD_TYPE_COMMA, "逗号");
        word_name_map.put(WORD_TYPE_CONTENT_BETWEEN_QUOTATION_MARKS, "引号间的内容");
        word_name_map.put(WORD_TYPE_DECIMAL_FLOAT, "浮点数");
        word_name_map.put(WORD_TYPE_HEX_INTEGER, "16进制整数");
    }
}
