import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class EmailSender {
    private final String mySenderEmail;
    private final String mySenderPassword;

    public EmailSender(String theSenderEmail, String theSenderPassword) {
        mySenderEmail = theSenderEmail;
        mySenderPassword = theSenderPassword;
    }

    public void sendEmail(String recipientEmail, String subject, String body, String attachmentPath) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mySenderEmail, mySenderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mySenderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(new File(attachmentPath));

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
