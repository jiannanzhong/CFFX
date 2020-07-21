import java.util.ArrayList;

public class Analyzer {
    //下面是四个关键字表
    private final String[] d_keyword = {"char", "double", "enum", "float", "int", "long", "short", "signed", "struct", "unsigned", "union", "void"};
    private final String[] c_keyword = {"break", "case", "continue", "default", "do", "else", "for", "goto", "if", "return", "switch", "while"};
    private final String[] s_keyword = {"auto", "const", "extern", "register", "static"};
    private final String[] o_keyword = {"sizeof", "typedef", "volatile"};

    private final ArrayList<String[]> words = new ArrayList<>();  //ArrayList存单词
    private final ArrayList<String[]> errors = new ArrayList<>();   //ArrayList存错误

    public ArrayList<String[]> getWords() {
        return words;
    }

    public ArrayList<String[]> getErrors() {
        return errors;
    }

    /**
     * 判断是否为数据类型关键字
     *
     * @param str 检测字符串
     * @return boolean
     */
    boolean isDKeyword(String str) {
        for (String s : d_keyword) {
            if (str.matches(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为存储类型关键字
     *
     * @param str 检测字符串
     * @return boolean
     */
    boolean isSKeyword(String str) {
        for (String s : s_keyword) {
            if (str.matches(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为控制语句关键字
     *
     * @param str 检测字符串
     * @return boolean
     */
    boolean isCKeyword(String str) {
        for (String s : c_keyword) {
            if (str.matches(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为其他类型关键字
     *
     * @param str 检测字符串
     * @return boolean
     */
    boolean isOKeyword(String str) {
        for (String s : o_keyword) {
            if (str.matches(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断提取缓冲区内容的时机，即该字符作为缓冲区是否需要提取的标志
     *
     * @param ch 字符
     * @return boolean
     */
    boolean NotEndOfErrorOrWord(char ch) {
        return !(ch + "").matches("[, <=;(){}+*/%&:'|\\[\\]\\t\\n\\-]");
//        return ch != '|' && ch != '[' && ch != ']' && ch != '\t' && ch != '\n' && ch != '\"' && ch != '-' && !(ch + "").matches("[,| |<|=|;|(|)|{|}|+|*|/|%|&|:|']");
    }

    /**
     * 查找关键字中的类型，有找到则返回对应类型
     *
     * @param str 检测字符串
     * @return int
     */
    int search(String str) {
        if (isDKeyword(str)) {
            return Config.WORD_TYPE_D_KEYWORD;
        } else if (isSKeyword(str)) {
            return Config.WORD_TYPE_S_KEYWORD;
        } else if (isOKeyword(str)) {
            return Config.WORD_TYPE_O_KEYWORD;
        } else if (isCKeyword(str)) {
            return Config.WORD_TYPE_C_KEYWORD;
        }
        return 0;
    }

    /**
     * 词法分析器
     */
    void analyzer(String codes) {
        int line = 1;   //当前行数，默认第一行
        int num = 1;    //第num个被识别出来的单词，默认是1
        int p = 0;      //作为游标，从第一个元素开始，逐个遍历整个String codes
        int len = codes.length();   //获取String codes的长度，以便预判结尾，提前做处理以防出错
        String c = "";   //缓冲区
        char ch;         //游标指向的字符会存到这里
        short status = 0;      //记录某个状态，共有0-10个状态，0是缓冲区没有读取内容的状态，1-10是读取中
        boolean isError = false; //判断引号间内容是否有错
        int hang = 0; //引号部分专用的行数记录，在状态9、10中配合line使用
        while (p < len) {
            ch = codes.charAt(p);  //根据游标读取字符
//			System.out.println(ch+"  "+status);
            if (0 == status) //如果状态是0，即缓冲区为空
            {
                if (ch == '\n') {   //遇到回车符就行数自增
                    line++;
                } else if (ch == ' ' || ch == '\t') {
                    ;     //空格略过
                } else if ((ch + "").matches("[a-zA-Z]")) {   //第一个读入的字符是字母，进入状态1
                    status = 1; //关键字或者部分标识符
                } else if (ch == '_') {        //第一个读入的字符是下划线，进入状态2
                    status = 2; //标识符
                } else if (ch == '<' || ch == '>') {      //第一个读入的字符是>或者<，进入状态3
                    c += ch;                   //存入缓冲区
                    if (p + 1 == len) {            //如果是最后一个字符了，那就提取
                        String[] str = {num + "", Config.WORD_TYPE_COMPARE_OPERATOR + "", c};
                        words.add(str);
                    } else {                     //否则进入状态3,并且把游标后移，跳过一次循环，在下次循环时读下一个字符（不然直接进入后面的状态3就错了）
                        status = 3;
                        p++;
                        continue;
                    }
                } else if (ch == '=' || ch == '!') {   //如果第一个字符是=或者!
                    c += ch;        //存缓冲区
                    if (p + 1 == len) {         //如果已经是最后一个字符了
                        int k = 0;
                        if (ch == '=') {     //是=则类型9
                            k = Config.WORD_TYPE_ASSIGNMENT_OPERATORS;
                        } else {
                            k = Config.WORD_TYPE_LOGICAL_OPERATOR;         //否则是!，则类型10
                        }
                        String[] str = {num + "", k + "", c};
                        words.add(str);
                    } else {             //否则进入状态4
                        status = 4;
                        p++;
                        continue;
                    }
                } else if (ch == '&' || ch == '|') {             //&或者|开头
                    c += ch;
                    if (p + 1 == len) {            //是末尾了，就提取缓冲区，存为类型10
                        String[] str = {num + "", Config.WORD_TYPE_LOGICAL_OPERATOR + "", c};
                        words.add(str);
                    } else {
                        status = 5;         //否则进入状态5
                        p++;
                        continue;
                    }
                } else if (ch == '-' || (ch + "").matches("[+*%/]")) {   //如果是  - + * % /开头
                    c += ch;
                    if (p + 1 == len) {
                        String[] str = {num + "", Config.WORD_TYPE_ARITHMETIC_OPERATOR + "", c};
                        words.add(str);
                    } else {
                        status = 6;
                        p++;
                        continue;
                    }
                } else if ((ch + "").matches("[0-9]")) {             //数字开头
                    status = 7;
                } else if ((ch + "").matches("[\\[\\]{}()]")) {         //界符
                    c += ch;
                    String[] str = {num + "", Config.WORD_TYPE_DELIMITER + "", c};
                    words.add(str);
                    num++;
                    c = "";
                } else if (ch == ';') {
                    c += ch;
                    String[] str = {num + "", Config.WORD_TYPE_SEMICOLON + "", c};
                    words.add(str);
                    num++;
                    c = "";
                } else if (ch == ':') {
                    c += ch;
                    String[] str = {num + "", Config.WORD_TYPE_COLON + "", c};
                    words.add(str);
                    num++;
                    c = "";
                } else if (ch == ',') {
                    c += ch;
                    String[] str = {num + "", Config.WORD_TYPE_COMMA + "", c};
                    words.add(str);
                    num++;
                    c = "";
                } else if (ch == '\'') {
                    String[] str = {num + "", Config.WORD_TYPE_DELIMITER + "", "" + ch};
                    words.add(str);
                    num++;
                    status = 9;
                    p++;
                    if (p == len) {
                        String[] strs = {line + "", "缺少右单引号"};
                        errors.add(strs);
                    }
                    continue;
                } else if (ch == '\"') {
                    String[] str = {num + "", Config.WORD_TYPE_DELIMITER + "", "" + ch};
                    words.add(str);
                    num++;
                    status = 10;
                    p++;
                    if (p == len) {
                        String[] strs = {line + "", "缺少右双引号"};
                        errors.add(strs);
                    }
                    continue;
                } else {
                    status = 8;
                }
            }
            switch (status) {
                case 1:          //情况1 ，字母开头，可能是标识符、关键字或者错误
                {
                    boolean pop = false;
                    if (NotEndOfErrorOrWord(ch)) {
                        c += ch;
                        if (p + 1 == len) {   //到末尾
                            pop = true;
                        }
                    } else {   //不满足
                        pop = true;
                        p--;
                    }
                    if (pop) {
                        int k = search(c);
                        if (k != 0) {
                            String[] str = {num + "", k + "", c};
                            words.add(str);
                            num++;
                        } else if (c.matches("^[a-z|A-Z][a-zA-Z0-9_]*")) {
                            String[] str = {num + "", Config.WORD_TYPE_IDENTIFIER + "", c};
                            words.add(str);
                            num++;
                        } else {
                            String[] str = {line + "", c};
                            errors.add(str);
                        }
                        c = "";
                        status = 0;
                    }
                }
                break;
                case 2: {          //情况2，下划线开头，可能是标识符或者错误
                    boolean pop = false;
                    if (NotEndOfErrorOrWord(ch)) {
                        c += ch;
                        if (p + 1 == len) {
                            pop = true;
                        }
                    } else {
                        pop = true;
                        p--;
                    }
                    if (pop) {
                        if (c.matches("[a-zA-Z0-9_]+")) {
                            String[] str = {num + "", Config.WORD_TYPE_IDENTIFIER + "", c};
                            words.add(str);
                            num++;
                        } else {
                            String[] str = {line + "", c};
                            errors.add(str);
                        }
                        c = "";
                        status = 0;
                    }
                }
                break;
                case 3: {          //情况3，关系运算符
                    if (ch == '=') {
                        c += ch;
                    } else {
                        p--;
                    }
                    String[] str = {num + "", Config.WORD_TYPE_RELATION_OPERATOR + "", c};
                    words.add(str);
                    num++;
                    c = "";
                    status = 0;
                }
                break;
                case 4: {          //情况4，=号、！号开头，可能是相等运算符，也可能是单个的逻辑运算符！，或者赋值号=
                    if (ch == '=') {
                        c += ch;
                    } else {
                        p--;
                    }
                    int k = 0;
                    if (c.contentEquals("!")) {
                        k = Config.WORD_TYPE_LOGICAL_OPERATOR;
                    } else if (c.contentEquals("=")) {
                        k = Config.WORD_TYPE_ASSIGNMENT_OPERATORS;
                    } else {
                        k = Config.WORD_TYPE_COMPARE_OPERATOR;
                    }

                    String[] str = {num + "", k + "", c};
                    words.add(str);
                    num++;
                    c = "";
                    status = 0;
                }
                break;
                case 5:          //情况5，&和|开头，可能是逻辑运算符，也可能是赋值运算符&=或者|=
                {
                    if ((c.contentEquals("&") && ch == '&') || (c.contentEquals("|") && ch == '|') || ch == '=') {
                        c += ch;
                    } else {
                        p--;
                    }
                    int k = 0;
                    if (ch == '=') {
                        k = Config.WORD_TYPE_ASSIGNMENT_OPERATORS;
                    } else {
                        k = Config.WORD_TYPE_LOGICAL_OPERATOR;
                    }
                    String[] str = {num + "", k + "", c};
                    words.add(str);
                    num++;
                    c = "";
                    status = 0;
                }
                break;
                case 6:          //情况6，+、-、*、/、%开头，可能是单字符的运算符，可能是赋值运算符，例如+=，也可能是单目运算符++、--
                {
                    int k = 0;
                    if (ch == '=') {
                        c += ch;
                        k = Config.WORD_TYPE_ASSIGNMENT_OPERATORS;
                    } else if (c.contentEquals("+") && ch == '+') {
                        c += ch;
                        k = Config.WORD_TYPE_UNARY_OPERATOR;
                    } else if (c.contentEquals("-") && ch == '-') {
                        c += ch;
                        k = Config.WORD_TYPE_UNARY_OPERATOR;
                    } else {
                        k = Config.WORD_TYPE_ARITHMETIC_OPERATOR;
                        p--;
                    }
                    String[] str = {num + "", k + "", c};
                    words.add(str);
                    num++;
                    c = "";
                    status = 0;
                }
                break;
                case 7:          //情况7，数字开头，可能是数字(十六进制整数，十进制的整数、浮点数)，也可能是错误，比如00是错误，0a也是错误
                {
                    boolean pop = false;
                    if (NotEndOfErrorOrWord(ch)) {
                        c += ch;
                        if (p + 1 == len) {   //到末尾
                            pop = true;
                        }
                    } else {   //不满足
                        pop = true;
                        p--;
                    }
                    if (pop) {
                        if (c.contentEquals("0") || c.matches("[1-9][0-9]*")) {
                            String[] str = {num + "", Config.WORD_TYPE_DECIMAL_INTEGER + "", c};
                            words.add(str);
                            num++;
                        } else if (c.matches("[1-9]+[.][0-9]+") || c.matches("[0][.][0-9]+")) {
                            String[] str = {num + "", Config.WORD_TYPE_DECIMAL_FLOAT + "", c};
                            words.add(str);
                            num++;
                        } else if (c.matches("[0][x|X][0-9a-fA-F][0-9|a-fA-F]")) {
                            String[] str = {num + "", Config.WORD_TYPE_HEX_INTEGER + "", c};
                            words.add(str);
                            num++;
                        } else {
                            String[] str = {line + "", c};
                            errors.add(str);
                        }
                        c = "";
                        status = 0;
                    }
                }
                break;
                case 8:          //情况8，不是任何单词的开头的开头，均为错误
                {
                    boolean pop = false;
                    if (NotEndOfErrorOrWord(ch)) {
                        c += ch;
                        if (p + 1 == len) {
                            pop = true;
                        }
                    } else {
                        pop = true;
                        p--;
                    }
                    if (pop) {
                        String[] str = {line + "", c};
                        errors.add(str);
                        c = "";
                        status = 0;
                    }
                }
                break;
                case 9:          //情况9，单引号开头，把引号内的内容当做单词，引号也是单词。如果内容中的引号前有\，则该引号作为内容的一部分
                {
                    if (ch != '\'' || (!c.equals("") && c.charAt(c.length() - 1) == '\\')) {  //非单引号或者前个字符是反斜杠
//					System.out.println("123");
                        if (ch != '\n')  //不是换行符，则读入缓冲区
                            c += ch;
                        else {     //否则报错，然后line自增
                            String[] str = {(hang + line) + "", "引号内不得有换行"};
                            errors.add(str);
//					line++;
                            hang++;
                            isError = true;
                        }
                        if (p + 1 == len) { //末尾的话提前处理
                            String[] str = {line + "", c};
                            errors.add(str);
                            c = "";
                            status = 0;
                            isError = false;
                        }
                    } else {  //否则缓冲区不是空的话，就提取
                        if (!c.equals("") && !c.contentEquals("\n")) {
                            if (!isError) {
                                String[] str = {num + "", Config.WORD_TYPE_CONTENT_BETWEEN_QUOTATION_MARKS + "", c};
//							System.out.println("123");
                                num++;
                                words.add(str);
                            } else {
                                String[] str = {line + "", c};
                                line += hang;
                                hang = 0;
                                errors.add(str);
                                isError = false;
                            }
                        }
                        String[] str2 = {num + "", Config.WORD_TYPE_DELIMITER + "", "" + ch};   //同时把当前的引号也存起来
                        words.add(str2);
                        num++;
                        c = "";
                        status = 0;
                    }
                }
                break;
                case 10:          //情况10，与情况9类似，这里是双引号
                {
                    if (ch != '\"' || (!c.equals("") && c.charAt(c.length() - 1) == '\\')) {
//					System.out.println(ch+"888888888888");
                        if (ch != '\n')
                            c += ch;
                        else {
                            String[] str = {line + "", "引号内不得有换行"};
                            errors.add(str);
//					line++;
                            hang++;
                            isError = true;
                        }
                        if (p + 1 == len) {
                            String[] str = {line + "", c};
                            errors.add(str);
                            c = "";
                            status = 0;
                            isError = false;
                        }
                    } else {
                        if (!c.equals("") && !c.contentEquals("\n")) {
                            if (!isError) {
                                String[] str = {num + "", Config.WORD_TYPE_CONTENT_BETWEEN_QUOTATION_MARKS + "", c};
                                num++;
                                words.add(str);
                            } else {
                                String[] str = {line + "", c};
                                line += hang;
                                hang = 0;
                                errors.add(str);
                                isError = false;
                            }
                        }
                        String[] str2 = {num + "", Config.WORD_TYPE_DELIMITER + "", "" + ch};
                        words.add(str2);
                        num++;
                        c = "";
                        status = 0;
                    }
                }
                break;
            }

            p++;
        }
    }
}
