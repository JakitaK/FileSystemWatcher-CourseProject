/**
 * EmailSender.java
 *
 * Part of the File Watcher Project.
 *
 * This class implements the IEmailSender interface, providing the ability to
 * send an email with an attachment using SMTP (specifically, Gmail SMTP).
 * It supports authentication, TLS encryption, and attachment handling.
 *
 * @author Ibadat Sandhu, Jakita Kaur, Balkirat Singh
 * @version Spring Quarter
 */

package model;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;

/**
 * EmailSender is an implementation of the IEmailSender interface that sends
 * an email using SMTP authentication and TLS.
 */
public class EmailSender implements IEmailSender {

    /** The sender's email address. */
    private final String mySenderEmail;

    /** The sender's email password. */
    private final String mySenderPassword;

    /**
     * Constructs an EmailSender with the given email credentials.
     *
     * @param theSenderEmail    the sender's email address
     * @param theSenderPassword the sender's email password
     */
    public EmailSender(final String theSenderEmail, final String theSenderPassword) {
        this.mySenderEmail = theSenderEmail;
        this.mySenderPassword = theSenderPassword;
    }
    /**
     * Sends an email with the specified recipient, subject, body, and attachment.
     *
     * @param theRecipientEmail the recipient's email address
     * @param theSubject        the email subject
     * @param theBody           the email body text
     * @param theAttachmentPath the file path to the attachment
     */

    @Override
    public void sendEmail(final String theRecipientEmail, final String theSubject, final String theBody, final String theAttachmentPath) {
        final Properties myProps = new Properties();
        myProps.put("mail.smtp.auth", "true");
        myProps.put("mail.smtp.starttls.enable", "true");
        myProps.put("mail.smtp.host", "smtp.gmail.com");
        myProps.put("mail.smtp.port", "587");

        final Session mySession = Session.getInstance(myProps, new Authenticator() {
           @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mySenderEmail, mySenderPassword);
            }
        });

        try {
            final Message myMessage = new MimeMessage(mySession);
            myMessage.setFrom(new InternetAddress(mySenderEmail));
            myMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(theRecipientEmail));
            myMessage.setSubject(theSubject);

            // Email body
            final MimeBodyPart myMessageBodyPart = new MimeBodyPart();
            myMessageBodyPart.setText(theBody);

            // Attachment
            final MimeBodyPart myAttachmentPart = new MimeBodyPart();
            myAttachmentPart.attachFile(new File(theAttachmentPath));

            final Multipart myMultipart = new MimeMultipart();
            myMultipart.addBodyPart(myMessageBodyPart);
            myMultipart.addBodyPart(myAttachmentPart);

            myMessage.setContent(myMultipart);

            Transport.send(myMessage);
            System.out.println("Email sent successfully.");

        } catch (final Exception myException) {
            myException.printStackTrace();
        }
    }
}
