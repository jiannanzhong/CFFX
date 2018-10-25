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
	//�������ĸ��ؼ��ֱ�
	String Dkeyword[] = {"char","double","enum","float","int","long","short","signed","struct","unsigned","union","void"};
	String control[] = {"break","case","continue","default","do","else","for","goto","if","return","switch","while"};
	String Skeyword[] = {"auto","const","extern","register","static"}; 
	String Okeyword[] = {"sizeof","typedef","volatile"};

	String jiefu = "[{|}|(|)]";  //���
	String single = "[;|:|,]";   //�������ַ�
	String number = "[0-9]";     //�������֣������ж����֣����ֵĿ�ͷһ���ǵ������֣�
	
	//�����������
	JFrame jf;
	JPanel jp;
	JTextArea jta;
	JButton jb;
	JCheckBox jcb;
	JPanel jp1,jp2;
	String[] head1 = {"�������","���","����"};
	String[] head2 = {"������","����"};
	JTable table1,table2;
	JScrollPane jsp1,jsp2,jsp0;
	DefaultTableModel dm1,dm2;
	DefaultTableCellRenderer dtcr;
	ArrayList<String[]> al,error;
	
	/**
	 * ���췽����ʼ������
	 */
	public Main(){
	jf = new JFrame();
	jf.setTitle("�ʷ�������");
	jta = new JTextArea(10,10);
	jta.setLineWrap(true);
	jta.setWrapStyleWord(true);
	jb  = new JButton("�ʷ�����");
	jcb = new JCheckBox("������ʾ�������");
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
//			System.out.println(strr[0]+"��"+"  "+strr[1]);
			dm2.addRow(strr);
		}
//		System.out.println("----end----");		
	}
	/**
	 * �ж��Ƿ�Ϊ�������͹ؼ���
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
	 * �ж��Ƿ�Ϊ�洢���͹ؼ���
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
	 * �ж��Ƿ�Ϊ�������ؼ���
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
	 * �ж��Ƿ�Ϊ�������͹ؼ���
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
	 * �ж���ȡ���������ݵ�ʱ���������ַ���Ϊ�������Ƿ���Ҫ��ȡ�ı�־
	 * @param ch
	 * @return boolean
	 */
	boolean NotTailOfErrorOrWord(char ch){
		if(ch!='|' && ch!='[' && ch!=']' && ch!='\t' && ch!='\n' && ch!='\"' && ch!='-' && !(ch+"").matches("[,| |<|=|;|(|)|{|}|+|*|/|%|&|:|']"))
			return true;
		return false;
	}
	/**
	 * ���ҹؼ����е����ͣ����ҵ��򷵻ض�Ӧ����
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
	 * Ĭ�ϵ��������������ֱ��棬�÷�������ת������
	 * @param i
	 * @return
	 */
	String converter(int i){
		switch(i){
		case 1:
			return "�������͹ؼ���";
		case 2:
			return "�洢���͹ؼ���";
		case 3:
			return "�����ؼ���";
		case 4:
			return "�������ؼ���";
		case 6:
			return "��ʶ��";
		case 7:
			return "��ϵ�����";
		case 8:
			return "��������";
		case 9:
			return "��ֵ�����";
		case 10:
			return "�߼������";
		case 11:
			return "���������";
		case 12:
			return "��Ŀ�����";
		case 13:
			return "10��������";
		case 14:
			return "���";
		case 15:
			return "�ֺ�";
		case 16:
			return "ð��";
		case 17:
			return "����";
		case 20:
			return "���ż������";
		case 21:
			return "������";
		case 22:
			return "16��������";
		}
		return "�Ҳ���";
	}
	/**
	 * �ʷ�������
	 */
	void analyzer(){
		String codes = jta.getText(); //������JTextAera���������ݶ�ȡ��һ���ַ���
		al = new ArrayList<String[]>();  //ArrayList�浥��
		error = new ArrayList<String[]>();   //ArrayList�����
		int line = 1;   //��ǰ������Ĭ�ϵ�һ��
		int num = 1;    //��num����ʶ������ĵ��ʣ�Ĭ����1
		int p = 0;      //��Ϊ�α꣬�ӵ�һ��Ԫ�ؿ�ʼ�������������String codes
		int len = codes.length();   //��ȡString codes�ĳ��ȣ��Ա�Ԥ�н�β����ǰ�������Է�����
		String c = "";   //������
		char ch;         //�α�ָ����ַ���浽����
		short status = 0;      //��¼ĳ��״̬������0-10��״̬��0�ǻ�����û�ж�ȡ���ݵ�״̬��1-10�Ƕ�ȡ��
		boolean isError = false; //�ж����ż������Ƿ��д�
		int hang = 0; //���Ų���ר�õ�������¼����״̬9��10�����lineʹ��
		while(p<len){
			ch = codes.charAt(p);  //�����α��ȡ�ַ�
//			System.out.println(ch+"  "+status);
			if(0==status) //���״̬��0����������Ϊ��
				{  
				if(ch=='\n'){   //�����س�������������
					line++;
				}
				else if(ch==' '||ch=='\t'){
					;     //�ո��Թ�
				}
			    else if((ch+"").matches("[a-zA-Z]")){   //��һ��������ַ�����ĸ������״̬1
					status = 1; //�ؼ��ֻ��߲��ֱ�ʶ��
				}
				else if(ch=='_'){        //��һ��������ַ����»��ߣ�����״̬2
					status = 2; //��ʶ��
				}
				else if(ch=='<' || ch=='>'){      //��һ��������ַ���>����<������״̬3
					c+=ch;                   //���뻺����
					if(p+1==len){            //��������һ���ַ��ˣ��Ǿ���ȡ
						String str[] = {num+"",8+"",c};
						al.add(str);
					}
					else{                     //�������״̬3,���Ұ��α���ƣ�����һ��ѭ�������´�ѭ��ʱ����һ���ַ�����Ȼֱ�ӽ�������״̬3�ʹ��ˣ�
						status =3;
						p++;
						continue;
					}
				}
				else if(ch=='=' || ch=='!'){   //�����һ���ַ���=����!
					c+=ch;        //�滺����
					if(p+1==len){         //����Ѿ������һ���ַ���
						int k =0;
						if(ch=='='){     //��=������9
							k=9;
						}
						else{
							k=10;         //������!��������10
						}
						String str[] = {num+"",k+"",c};
						al.add(str);
					}
					else{             //�������״̬4
						status =4;
						p++;
						continue;
					}
				}
				else if(ch=='&' || ch=='|'){             //&����|��ͷ
					c+=ch;
					if(p+1==len){            //��ĩβ�ˣ�����ȡ����������Ϊ����10
						String str[] = {num+"",10+"",c};
						al.add(str);
					}
					else{
						status = 5;         //�������״̬5
						p++;
						continue;
					}
				}
				else if(ch=='-' || (ch+"").matches("[+|*|%|/]")){   //�����  - + * % /��ͷ
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
				else if((ch+"").matches(number)){             //���ֿ�ͷ
					status = 7;
				}
				else if(ch=='[' || ch==']' || (ch+"").matches(jiefu)){         //���
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
						String strs[] = {line+"","ȱ���ҵ�����"};
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
						String strs[] = {line+"","ȱ����˫����"};
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
			case 1:          //���1 ����ĸ��ͷ�������Ǳ�ʶ�����ؼ��ֻ��ߴ���
				{
				boolean pop = false;
				if( NotTailOfErrorOrWord(ch) ){
					c+=ch;
					if(p+1==len){   //��ĩβ
						pop = true;
					}
				}
				else{   //������
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
			case 2:{          //���2���»��߿�ͷ�������Ǳ�ʶ�����ߴ���
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
			case 3:{          //���3����ϵ�����
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
			case 4:{          //���4��=�š����ſ�ͷ������������������Ҳ�����ǵ������߼�������������߸�ֵ��=
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
			case 5:          //���5��&��|��ͷ���������߼��������Ҳ�����Ǹ�ֵ�����&=����|=
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
			case 6:          //���6��+��-��*��/��%��ͷ�������ǵ��ַ���������������Ǹ�ֵ�����������+=��Ҳ�����ǵ�Ŀ�����++��--
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
			case 7:          //���7�����ֿ�ͷ������������(ʮ������������ʮ���Ƶ�������������)��Ҳ�����Ǵ��󣬱���00�Ǵ���0aҲ�Ǵ���
				{
				boolean pop = false;
				if( NotTailOfErrorOrWord(ch) ){
					c+=ch;
					if(p+1==len){   //��ĩβ
						pop = true;
					}
				}
				else{   //������
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
			case 8:          //���8�������κε��ʵĿ�ͷ�Ŀ�ͷ����Ϊ����
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
			case 9:          //���9�������ſ�ͷ���������ڵ����ݵ������ʣ�����Ҳ�ǵ��ʡ���������е�����ǰ��\�����������Ϊ���ݵ�һ����
				{
				if(ch!='\'' || (c!="" && c.charAt(c.length()-1)=='\\') ){  //�ǵ����Ż���ǰ���ַ��Ƿ�б��
//					System.out.println("123");
					if(ch!='\n')  //���ǻ��з�������뻺����
					c+=ch;
					else{     //���򱨴�Ȼ��line����
					String str[] = {(hang+line)+"","�����ڲ����л���"};
					error.add(str);
//					line++;
					hang++;
					isError = true;
					}
					if(p+1==len){ //ĩβ�Ļ���ǰ����
						String str[] = {line+"",c};
						error.add(str);
					    c="";
					    status=0;
					    isError = false;
					}
				}
				else{  //���򻺳������ǿյĻ�������ȡ
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
					String str2[] = {num+"","14",""+ch};   //ͬʱ�ѵ�ǰ������Ҳ������
					al.add(str2);
					num++;
					c="";
					status=0;
				}
			}
			break;
			case 10:          //���10�������9���ƣ�������˫����
				{
				if(ch!='\"' || (c!="" && c.charAt(c.length()-1)=='\\') ){
//					System.out.println(ch+"888888888888");
					if(ch!='\n')
					c+=ch;
					else{
					String str[] = {line+"","�����ڲ����л���"};
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
