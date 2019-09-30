package com.neil;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="mailSender", urlPatterns={"/mailSender"},
        loadOnStartup=1)
public class MailSender extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hello! World!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Hello! World!</h1>");
        out.println("</body>");
        out.println("</html>");

        int sentCounter = 0;

        while (true){
            out.println("Now  wait for 30 sec...");
            out.println("Sent counter: " + sentCounter);
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            Date date = new Date();
            String strDate = sdFormat.format(date);

            String user = "neil@mail.nemu.edu.tw";//user
            String pwd = "123456";//password
            String to= "edward@mail.nemu.edu.tw";
            String from = "neil@mail.nemu.edu.tw";//寄件人的email
            /*
             * host
             * yahoo:"smtp.mail.yahoo.com"
             * outlook:"smtp-mail.outlook.com"
             */
            String host ="mail.nemu.edu.tw" ;

            String subject = "This is mail test at " + strDate;
            String body = "This is mail test sent by Java from neil's hackintosh Ryzen-2400g.\n" + "The time is : " + strDate;

            // 建立一個Properties來設定Properties
            Properties properties = System.getProperties();
            //設定傳輸協定為smtp
            properties.setProperty("mail.transport.protocol", "smtp");
            //設定mail Server
            properties.setProperty("mail.smtp.host", host);
            properties.setProperty("mail.smtp.port", "587");

            properties.put("mail.smtp.auth", "true");//需要驗證帳號密碼
            //SSL authentication
            properties.put("mail.smtp.ssl.enable", "false");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.trust", "*");

            // 建立一個Session物件，並把properties傳進去
            Session mailSession = Session.getInstance(properties, new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(user,pwd);
                }
            });

            try {
                //建立一個 MimeMessage object.
                MimeMessage message = new MimeMessage(mailSession);
                //設定郵件
                message.setFrom(new InternetAddress(from)); // 設定寄件人
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // 設定收件人
                message.setSubject(subject); // 設定主旨

                // 宣告一個multipart , 它可以使內文有不同的段落，
                //使其可以用用來夾帶內文及檔案
                Multipart multipart = new MimeMultipart();
                //第一個段落
                BodyPart messageBodyPart = new MimeBodyPart(); //宣告一個BodyPart用以夾帶內文
                messageBodyPart.setText(body);//設定內文
                multipart.addBodyPart(messageBodyPart); //把BodyPart加入Multipart

                message.setContent(multipart); //設定eMultipart為messag的Content
                Transport transport = mailSession.getTransport("smtp");
                transport.connect(host ,user, pwd);
                //傳送信件
                transport.sendMessage(message,message.getAllRecipients());

                out.println("發送成功");
                //關閉連線
                transport.close();
                sentCounter++;

            } catch (MessagingException mex) {
                mex.printStackTrace();
            }
        }
    }
}