package bianyi;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Main implements ActionListener {
	//下面是四个关键字表
	String Dkeyword[] = {"char","double","enum","float","int","long","short","signed","struct","unsigned","union","void"};
	String control[] = {"break","case","continue","default","do","else","for","goto","if","return","switch","while"};
	String Skeyword[] = {"auto","const","extern","register","static"}; 
	String Okeyword[] = {"sizeof","typedef","volatile"};

	String jiefu = "[{|}|(|)]";  //界符
	String single = "[;|:|,]";   //单个的字符
	String number = "[0-9]";     //单个数字，用于判断数字（数字的开头一定是单个数字）
	
	//创建界面组件
	JFrame jf;
	JPanel jp;
	JTextArea jta;
	JButton jb;
	JCheckBox jcb;
	JPanel jp1,jp2;
	String[] head1 = {"单词序号","类别","单词"};
	String[] head2 = {"错误行","错误"};
	JTable table1,table2;
	JScrollPane jsp1,jsp2,jsp0;
	DefaultTableModel dm1,dm2;
	DefaultTableCellRenderer dtcr;
	ArrayList<String[]> al,error;
	
	/**
	 * 构造方法初始化界面
	 */
	public Main(){
	jf = new JFrame();
	jf.setTitle("词法分析器");
	jta = new JTextArea(10,10);
	jta.setLineWrap(true);
	jta.setWrapStyleWord(true);
	jb  = new JButton("词法分析");
	jcb = new JCheckBox("中文显示单词类别");
	jp = new JPanel();
	
	table1 = new JTable();
	table2 = new JTable();
	jsp0 = new JScrollPane(jta);
	jsp1 = new JScrollPane(table1);
	jsp2 = new JScrollPane(table2);
	dm1 = new DefaultTableModel(null,head1);
	dm2 = new DefaultTableModel(null,head2);
	table1.setModel(dm1);
	table2.setModel(dm2);
	dtcr = new DefaultTableCellRenderer();
	dtcr.setHorizontalAlignment(JLabel.CENTER);
	table1.setDefaultRenderer(Object.class, dtcr);
	table2.setDefaultRenderer(Object.class, dtcr);
	
	jf.getContentPane();
	jf.setLayout(new GridLayout(2,2));
	jf.setSize(1000,880);
	jta.setFont(new Font("TimesRoman", Font.PLAIN, 18));
	jf.add(jsp0);
	jp.setLayout(null);
	jp.add(jb);
	jb.setBounds(230,150,200,100);
	jp.add(jcb);
	jcb.setBounds(50,130,130,150);
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
		this.analyzer();
		dm1.setRowCount(0);
		dm2.setRowCount(0);
		boolean iss = jcb.isSelected();
		for(int a = 0;a<al.size();a++){
			String strr[] = (String[]) al.get(a);
//			System.out.println(strr[0]+"  "+strr[1]+"  "+strr[2]);
			if(iss)
			strr[1] = converter(Integer.parseInt(strr[1]));
			dm1.addRow(strr);
		}		
//		System.out.println("---------------");	
		for(int a = 0;a<error.size();a++){
			String strr[] = (String[]) error.get(a);
//			System.out.println(strr[0]+"行"+"  "+strr[1]);
			dm2.addRow(strr);
		}
//		System.out.println("----end----");		
	}
	/**
	 * 判断是否为数据类型关键字
	 * @param str
	 * @return boolean
	 */
	boolean Dkeyword(String str){
	    for(int i = 0;i<Dkeyword.length;i++){
	    	if(str.matches(Dkeyword[i])){
	    		return true;
	    	}
	    }
		return false;
	}
	/**
	 * 判断是否为存储类型关键字
	 * @param str
	 * @return boolean
	 */
	boolean Skeyword(String str){
	    for(int i = 0;i<Skeyword.length;i++){
	    	if(str.matches(Skeyword[i])){
	    		return true;
	    	}
	    }
		return false;
	}
	/**
	 * 判断是否为控制语句关键字
	 * @param str
	 * @return boolean
	 */
	boolean control(String str){
	    for(int i = 0;i<control.length;i++){
	    	if(str.matches(control[i])){
	    		return true;
	    	}
	    }
		return false;
	}
	/**
	 * 判断是否为其他类型关键字
	 * @param str
	 * @return boolean
	 */
	boolean Okeyword(String str){
	    for(int i = 0;i<Okeyword.length;i++){
	    	if(str.matches(Okeyword[i])){
	    		return true;
	    	}
	    }
		return false;
	}
	/**
	 * 判断提取缓冲区内容的时机，即该字符作为缓冲区是否需要提取的标志
	 * @param ch
	 * @return boolean
	 */
	boolean NotTailOfErrorOrWord(char ch){
		if(ch!='|' && ch!='[' && ch!=']' && ch!='\t' && ch!='\n' && ch!='\"' && ch!='-' && !(ch+"").matches("[,| |<|=|;|(|)|{|}|+|*|/|%|&|:|']"))
			return true;
		return false;
	}
	/**
	 * 查找关键字中的类型，有找到则返回对应类型
	 * @param str
	 * @return int
	 */
	int search(String str){
		if(Dkeyword(str)){
			return 1;
		}
		else if(Skeyword(str)){
			return 2;
		}
		else if(Okeyword(str)){
			return 3;
		}
		else if(control(str)){
			return 4;
		}
		return 0;
	}	
	/**
	 * 默认单词类型是用数字保存，该方法可以转成中文
	 * @param i
	 * @return
	 */
	String converter(int i){
		switch(i){
		case 1:
			return "数据类型关键字";
		case 2:
			return "存储类型关键字";
		case 3:
			return "其他关键字";
		case 4:
			return "控制语句关键字";
		case 6:
			return "标识符";
		case 7:
			return "关系运算符";
		case 8:
			return "相等运算符";
		case 9:
			return "赋值运算符";
		case 10:
			return "逻辑运算符";
		case 11:
			return "算数运算符";
		case 12:
			return "单目运算符";
		case 13:
			return "10进制整数";
		case 14:
			return "界符";
		case 15:
			return "分号";
		case 16:
			return "冒号";
		case 17:
			return "逗号";
		case 20:
			return "引号间的内容";
		case 21:
			return "浮点数";
		case 22:
			return "16进制整数";
		}
		return "找不到";
	}
	/**
	 * 词法分析器
	 */
	void analyzer(){
		String codes = jta.getText(); //这里会把JTextAera的所有内容读取成一个字符串
		al = new ArrayList<String[]>();  //ArrayList存单词
		error = new ArrayList<String[]>();   //ArrayList存错误
		int line = 1;   //当前行数，默认第一行
		int num = 1;    //第num个被识别出来的单词，默认是1
		int p = 0;      //作为游标，从第一个元素开始，逐个遍历整个String codes
		int len = codes.length();   //获取String codes的长度，以便预判结尾，提前做处理以防出错
		String c = "";   //缓冲区
		char ch;         //游标指向的字符会存到这里
		short status = 0;      //记录某个状态，共有0-10个状态，0是缓冲区没有读取内容的状态，1-10是读取中
		boolean isError = false; //判断引号间内容是否有错
		int hang = 0; //引号部分专用的行数记录，在状态9、10中配合line使用
		while(p<len){
			ch = codes.charAt(p);  //根据游标读取字符
//			System.out.println(ch+"  "+status);
			if(0==status) //如果状态是0，即缓冲区为空
				{  
				if(ch=='\n'){   //遇到回车符就行数自增
					line++;
				}
				else if(ch==' '||ch=='\t'){
					;     //空格略过
				}
			    else if((ch+"").matches("[a-zA-Z]")){   //第一个读入的字符是字母，进入状态1
					status = 1; //关键字或者部分标识符
				}
				else if(ch=='_'){        //第一个读入的字符是下划线，进入状态2
					status = 2; //标识符
				}
				else if(ch=='<' || ch=='>'){      //第一个读入的字符是>或者<，进入状态3
					c+=ch;                   //存入缓冲区
					if(p+1==len){            //如果是最后一个字符了，那就提取
						String str[] = {num+"",8+"",c};
						al.add(str);
					}
					else{                     //否则进入状态3,并且把游标后移，跳过一次循环，在下次循环时读下一个字符（不然直接进入后面的状态3就错了）
						status =3;
						p++;
						continue;
					}
				}
				else if(ch=='=' || ch=='!'){   //如果第一个字符是=或者!
					c+=ch;        //存缓冲区
					if(p+1==len){         //如果已经是最后一个字符了
						int k =0;
						if(ch=='='){     //是=则类型9
							k=9;
						}
						else{
							k=10;         //否则是!，则类型10
						}
						String str[] = {num+"",k+"",c};
						al.add(str);
					}
					else{             //否则进入状态4
						status =4;
						p++;
						continue;
					}
				}
				else if(ch=='&' || ch=='|'){             //&或者|开头
					c+=ch;
					if(p+1==len){            //是末尾了，就提取缓冲区，存为类型10
						String str[] = {num+"",10+"",c};
						al.add(str);
					}
					else{
						status = 5;         //否则进入状态5
						p++;
						continue;
					}
				}
				else if(ch=='-' || (ch+"").matches("[+|*|%|/]")){   //如果是  - + * % /开头
					c+=ch;
					if(p+1==len){
						String str[] = {num+"",11+"",c};
						al.add(str);
					}
					else{
						status =6;
						p++;
						continue;
					}
				}
				else if((ch+"").matches(number)){             //数字开头
					status = 7;
				}
				else if(ch=='[' || ch==']' || (ch+"").matches(jiefu)){         //界符
					c+=ch;
					String str[] = {num+"",14+"",c};
					al.add(str);
					num++;
					c="";
				}
				else if(ch==';'){
					c+=ch;
					String str[] = {num+"",15+"",c};
					al.add(str);
					num++;
					c="";
				}
				else if(ch==':'){
					c+=ch;
					String str[] = {num+"",16+"",c};
					al.add(str);
					num++;
					c="";
				}
				else if(ch==','){
					c+=ch;
					String str[] = {num+"",17+"",c};
					al.add(str);
					num++;
					c="";
				}
				else if(ch=='\''){
					String str[] = {num+"","14",""+ch};
					al.add(str);
					num++;
					status = 9;
					p++;
					if(p==len){
						String strs[] = {line+"","缺少右单引号"};
						error.add(strs);
					}
					continue;
				}
				else if(ch=='\"'){
					String str[] = {num+"","14",""+ch};
					al.add(str);
					num++;
					status = 10;
					p++;
					if(p==len){
						String strs[] = {line+"","缺少右双引号"};
						error.add(strs);
					}
					continue;
				}
				else{
					status=8;
				}
			}
			switch(status)
			{	
			case 1:          //情况1 ，字母开头，可能是标识符、关键字或者错误
				{
				boolean pop = false;
				if( NotTailOfErrorOrWord(ch) ){
					c+=ch;
					if(p+1==len){   //到末尾
						pop = true;
					}
				}
				else{   //不满足
					pop = true;
					p--;
				}
				if(pop){
					int k = search(c);
					if(k!=0){
						String str[] = {num+"",k+"",c};
						al.add(str);
						num++;
					}
					else if(c.matches("^[a-z|A-Z][a-zA-Z0-9_]*")){
						String str[] = {num+"",6+"",c};
						al.add(str);
						num++;
					}
					else{
						String str[] = {line+"",c};
						error.add(str);
					}
					c="";
					status=0;
				}
			}
			break;
			case 2:{          //情况2，下划线开头，可能是标识符或者错误
				boolean pop = false;
				if(NotTailOfErrorOrWord(ch)){
					c+=ch;
					if(p+1==len){
						pop = true;
					}
				}
				else{
					pop = true;
					p--;
				}
				if(pop){
					if(c.matches("[a-zA-Z0-9_]+")){
						String str[] = {num+"",6+"",c};
						al.add(str);
						num++;
					}
					else{
						String str[] = {line+"",c};
						error.add(str);
					}
					c="";
					status=0;
				}
			}
			break;
			case 3:{          //情况3，关系运算符
				boolean pop = false;
				if(ch=='='){
					c+=ch;
					pop = true;
				}
				else{
					pop = true;
					p--;
				}
				if(pop){
					String str[] = {num+"",7+"",c};
					al.add(str);
					num++;
					c="";
					status=0;
				}
			}
			break;
			case 4:{          //情况4，=号、！号开头，可能是相等运算符，也可能是单个的逻辑运算符！，或者赋值号=
				boolean pop = false;
				if(ch=='='){
					c+=ch;
					pop = true;
				}
				else{
					pop = true;
					p--;
				}
				if(pop){
					int k = 0;
					if(c.contentEquals("!")){
						k=10;
					}
					else if(c.contentEquals("=")){
						k=9;
					}
					else{
						k=8;
					}
					
					String str[] = {num+"",k+"",c};
					al.add(str);
					num++;
					c="";
					status=0;
				}
			}
			break;
			case 5:          //情况5，&和|开头，可能是逻辑运算符，也可能是赋值运算符&=或者|=
				{
				boolean pop = false;
				if((c.contentEquals("&") && ch=='&') || (c.contentEquals("|") && ch=='|') || ch=='=' ){
					c+=ch;
					pop = true;
				}
				else{
					pop = true;
					p--;
				}
				if(pop){
					int k = 0;
					if(ch=='='){
						k=9;
					}
					else{
						k=10;
					}
					String str[] = {num+"",k+"",c};
					al.add(str);
					num++;
					c="";
					status=0;
				}
			}
			break;
			case 6:          //情况6，+、-、*、/、%开头，可能是单字符的运算符，可能是赋值运算符，例如+=，也可能是单目运算符++、--
				{
				boolean pop = true;
				int k = 0;
				if(ch=='='){
					c+=ch;
					pop = true;
					k=9;
				}
				else if(c.contentEquals("+") && ch=='+'){
					c+=ch;
					pop = true;
					k=12;
				}
				else if(c.contentEquals("-") && ch=='-'){
					c+=ch;
					pop = true;
					k=12;
				}
				else{
					pop = true;
					k=11;
					p--;
				}
				if(pop){
					String str[] = {num+"",k+"",c};
					al.add(str);
					num++;
					c="";
					status=0;
				}
			}
			break;
			case 7:          //情况7，数字开头，可能是数字(十六进制整数，十进制的整数、浮点数)，也可能是错误，比如00是错误，0a也是错误
				{
				boolean pop = false;
				if( NotTailOfErrorOrWord(ch) ){
					c+=ch;
					if(p+1==len){   //到末尾
						pop = true;
					}
				}
				else{   //不满足
					pop = true;
					p--;
				}
				if(pop){
					if(c.contentEquals("0")||c.matches("[1-9][0-9]*")){
						String str[] = {num+"","13",c};
						al.add(str);
						num++;
					}
					else if(c.matches("[1-9]+[.][0-9]+") || c.matches("[0][.][0-9]+")){
						String str[] = {num+"","21",c};
						al.add(str);
						num++;
					}
					else if(c.matches("[0][x|X][0-9|a-f|A-F][0-9|a-f|A-F]")){
						String str[] = {num+"","22",c};
						al.add(str);
						num++;
					}
					else{
						String str[] = {line+"",c};
						error.add(str);
					}
					c="";
					status=0;
				}
			}
			break;
			case 8:          //情况8，不是任何单词的开头的开头，均为错误
				{
				boolean pop = false;
				if( NotTailOfErrorOrWord(ch) ){
					c+=ch;
					if(p+1==len){
						pop = true;
					}
				}
				else{
					pop = true;
					p--;
				}
				if(pop){
					String str[] = {line+"",c};
					error.add(str);
					c="";
					status=0;
				}
			}
			break;
			case 9:          //情况9，单引号开头，把引号内的内容当做单词，引号也是单词。如果内容中的引号前有\，则该引号作为内容的一部分
				{
				if(ch!='\'' || (c!="" && c.charAt(c.length()-1)=='\\') ){  //非单引号或者前个字符是反斜杠
//					System.out.println("123");
					if(ch!='\n')  //不是换行符，则读入缓冲区
					c+=ch;
					else{     //否则报错，然后line自增
					String str[] = {(hang+line)+"","引号内不得有换行"};
					error.add(str);
//					line++;
					hang++;
					isError = true;
					}
					if(p+1==len){ //末尾的话提前处理
						String str[] = {line+"",c};
						error.add(str);
					    c="";
					    status=0;
					    isError = false;
					}
				}
				else{  //否则缓冲区不是空的话，就提取
					if(c!=""&&!c.contentEquals("\n")){
						if(!isError){
							String str[] = {num+"","20",c};
//							System.out.println("123");
							num++;
							al.add(str);
						}
						else{
							String str[] = {line+"",c};
							line+=hang;
							hang = 0;
							error.add(str);
							isError = false;
						}
					}
					String str2[] = {num+"","14",""+ch};   //同时把当前的引号也存起来
					al.add(str2);
					num++;
					c="";
					status=0;
				}
			}
			break;
			case 10:          //情况10，与情况9类似，这里是双引号
				{
				if(ch!='\"' || (c!="" && c.charAt(c.length()-1)=='\\') ){
//					System.out.println(ch+"888888888888");
					if(ch!='\n')
					c+=ch;
					else{
					String str[] = {line+"","引号内不得有换行"};
					error.add(str);
//					line++;
					hang++;
					isError = true;
					}
					if(p+1==len){
						String str[] = {line+"",c};
						error.add(str);
					    c="";
					    status=0;
					    isError = false;
					}
				}
				else{
					if(c!=""&&!c.contentEquals("\n")){
					  if(!isError){
						  String str[] = {num+"","20",c};
						  num++;
						  al.add(str);
					  }
					  else{
						  String str[] = {line+"",c};
							line+=hang;
							hang = 0;
							error.add(str);
							isError = false;
					  }
					}
					String str2[] = {num+"","14",""+ch};
					al.add(str2);
					num++;
					c="";
					status=0;
				}
			}
				break;
		}

			p++;
		}
	}
}
