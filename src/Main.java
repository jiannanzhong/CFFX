import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main implements ActionListener {
    //创建界面组件
    JFrame jf;
    JPanel jp;
    JTextArea jta;
    JButton jb;
    JCheckBox jcb;
    //    JPanel jp1, jp2;
    String[] head1 = {"单词序号", "类别", "单词"};
    String[] head2 = {"错误行", "错误"};
    JTable table1, table2;
    JScrollPane jsp1, jsp2, jsp0;
    DefaultTableModel dm1, dm2;
    DefaultTableCellRenderer dtcr;

    /**
     * 构造方法初始化界面
     */
    public Main() {
        jf = new JFrame();
        jf.setTitle("词法分析器");
        jta = new JTextArea(10, 10);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jb = new JButton("词法分析");
        jcb = new JCheckBox("中文显示单词类别");
        jp = new JPanel();

        table1 = new JTable();
        table2 = new JTable();
        jsp0 = new JScrollPane(jta);
        jsp1 = new JScrollPane(table1);
        jsp2 = new JScrollPane(table2);
        dm1 = new DefaultTableModel(null, head1);
        dm2 = new DefaultTableModel(null, head2);
        table1.setModel(dm1);
        table2.setModel(dm2);
        dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(JLabel.CENTER);
        table1.setDefaultRenderer(Object.class, dtcr);
        table2.setDefaultRenderer(Object.class, dtcr);

        jf.getContentPane();
        jf.setLayout(new GridLayout(2, 2));
        jf.setSize(1000, 880);
        jta.setFont(new Font("TimesRoman", Font.PLAIN, 18));
        jf.add(jsp0);
        jp.setLayout(null);
        jp.add(jb);
        jb.setBounds(230, 150, 200, 100);
        jp.add(jcb);
        jcb.setBounds(50, 130, 130, 150);
        jf.add(jp);
        jf.add(jsp1);
        jf.add(jsp2);

        jb.addActionListener(this);
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        jf.setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    public void actionPerformed(ActionEvent e) {
        Analyzer alz = new Analyzer();
        alz.analyzer(jta.getText());
        dm1.setRowCount(0);
        dm2.setRowCount(0);
        boolean needConvert = jcb.isSelected();
        for (String[] strr : alz.getWords()) {
//			System.out.println(strr[0]+"  "+strr[1]+"  "+strr[2]);
            if (needConvert)
                strr[1] = Config.word_name_map.get(Integer.parseInt(strr[1]));
            if (strr[1] == null) {
                strr[1] = "找不到";
            }
            dm1.addRow(strr);
        }
//		System.out.println("---------------");
        for (String[] strr : alz.getErrors()) {
//			System.out.println(strr[0]+"行"+"  "+strr[1]);
            dm2.addRow(strr);
        }
//		System.out.println("----end----");
    }
}
