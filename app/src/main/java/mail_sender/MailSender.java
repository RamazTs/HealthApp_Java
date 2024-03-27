package mail_sender;

import android.util.Log;

import java.security.Security;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com"; //Hostname of the SMTP mail server which you want to connect for sending emails.
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailSender(String user, String password) {
        this.user = user; //Your SMTP username. In case of GMail SMTP this is going to be your GMail email address.
        this.password = password; //Your SMTP password. In case of GMail SMTP this is going to be your GMail password.

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public void sendMail(String subject, String body,
                                      String sender, String recipients) throws Exception {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html"));
        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        message.setDataHandler(handler);

        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        Transport.send(message);
    }




    public static void sendToUserMail(String subject, String location, String weather, String severity, String content, String fromEmail, String from_code, String toEmail)
    {

        String final_content = "Hi Doctor: " + ",<br/>Weather: "+weather+". <br/><br/>Severity: <span style=\"color:red\">"+severity+"</span>. Information:" + content  + "<br/><br/>Thank you so much!<br/>";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MailSender sender = new MailSender(fromEmail,
                            from_code);
                    sender.sendMail(subject, final_content,fromEmail, toEmail);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }
}