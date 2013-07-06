package com.email;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.andorid.shu.love.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendMail extends Activity {
	private Button btnClick;
	private EditText txtToAddress;
	private EditText txtSubject;
	private EditText txtContent;
	private static final String SAVE_INFORMATION = "save_information";
	String username;
	String password;
	private Vector<String> file = new Vector<String>();
    private String fileName="";
	public void addAttachfile(String fname){  
        file.addElement("/sdcard/lovereader/pic/" + "testpic" + ".png");  
    }  
	public void SendMail() throws MessagingException, IOException {
		// 用sharedpreference来获取数值
		SharedPreferences pre = getSharedPreferences(SAVE_INFORMATION,
				MODE_WORLD_READABLE);
		String content = pre.getString("save", "");
		String[] Information = content.split(";");
		username = Information[0];
		password = Information[1];

		// 该部分有待完善！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.qq.comm");// 存储发送邮件服务器的信息
		props.put("mail.smtp.auth", "true");// 同时通过验证
		// 基本的邮件会话
		Session session = Session.getInstance(props);
		session.setDebug(true);// 设置调试标志
		// 构造信息体
		MimeMessage message = new MimeMessage(session);

		// 发件地址
		Address fromAddress = null;
		// fromAddress = new InternetAddress("sarah_susan@sina.com");
		fromAddress = new InternetAddress(username);
		message.setFrom(fromAddress);

		// 收件地址
		Address toAddress = null;
		toAddress = new InternetAddress(txtToAddress.getText().toString());
		message.addRecipient(Message.RecipientType.TO, toAddress);

		// 解析邮件内容
        addAttachfile(fileName);
		message.setSubject(txtSubject.getText().toString());// 设置信件的标题
		message.setText(txtContent.getText().toString());// 设置信件内容
		Multipart mp = new MimeMultipart();  
        MimeBodyPart mbp = new MimeBodyPart();  
        mbp.setContent(txtContent.getText().toString(), "text/html;charset=gb2312");  
        mp.addBodyPart(mbp);    
         if(!file.isEmpty()){//有附件  
             Enumeration efile=file.elements();  
             while(efile.hasMoreElements()){   
                 mbp=new MimeBodyPart();  
                 
                 fileName=efile.nextElement().toString(); //选择出每一个附件名  
                 FileDataSource fds=new FileDataSource(fileName); //得到数据源  
                 System.out.println(fileName+"SDFSDFSFDF");
                 mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
                 mbp.setFileName(fds.getName());  //得到文件名同样至入BodyPart  
                 mp.addBodyPart(mbp);  
             }    
             file.removeAllElements();      
         }   
         message.setContent(mp); //Multipart加入到信件  
		
		message.setSentDate(new Date());
		message.saveChanges(); // implicit with send()//存储有信息

		// send e-mail message

		Transport transport = null;
		transport = session.getTransport("smtp");
		transport.connect("smtp.qq.com", username, password);

		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		System.out.println("邮件发送成功！");

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.send_email);

		txtToAddress = (EditText) findViewById(R.id.txtToAddress);
		txtSubject = (EditText) findViewById(R.id.txtSubject);
		txtContent = (EditText) findViewById(R.id.txtContent);

		txtToAddress.setText("自己的邮箱@qq.com");
		txtSubject.setText("Hello~");
		txtContent.setText("你好，我在做程序呢~");

		btnClick = (Button) findViewById(R.id.btnSEND);
		btnClick.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					SendMail();
				} catch (MessagingException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}