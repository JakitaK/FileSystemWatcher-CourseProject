/**
 * EmailSender.java
 * Part of the File Watcher Project.
 * This class implements the IEmailSender interface to provide functionality
 * for sending emails using SMTP (Gmail). It supports TLS encryption,
 * authentication, and optional file attachments. Designed for use in automated
 * systems that need to notify users via email.
 * @author Ibadat Sandhu, Jakita Kaur, Balkirat Singh
 * @version Spring Quarter
 */

package model;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * EmailSender is an implementation of the IEmailSender interface that sends
 * emails using SMTP (specifically Gmail) with optional attachments.
 */
public class EmailSender implements IEmailSender {

    /** The sender's email address. */
    private final String mySenderEmail;

    /** The sender's email password or app-specific password. */
    private final String mySenderPassword;

    /** SMTP property key for enabling authentication. */
    private static final String SMTP_AUTH = "mail.smtp.auth";

    /** SMTP property key for enabling STARTTLS encryption. */
    private static final String SMTP_STARTTLS = "mail.smtp.starttls.enable";

    /** SMTP property key for specifying the mail server host. */
    private static final String SMTP_HOST = "mail.smtp.host";

    /** SMTP property key for specifying the mail server port. */
    private static final String SMTP_PORT = "mail.smtp.port";
    private static final Logger LOGGER = Logger.getLogger(EmailSender.class.getName());


    /**
     * Constructs an EmailSender with the sender's email and password.
     * @param theSenderEmail    the sender's Gmail address
     * @param theSenderPassword the sender's password (preferably an app password)
     */
    public EmailSender(final String theSenderEmail, final String theSenderPassword) {
        this.mySenderEmail = theSenderEmail;
        this.mySenderPassword = theSenderPassword;
    }

    /**
     * Builds a MimeMessage object with optional file attachment.
     * @param theSession         the authenticated mail session
     * @param theTo              the recipient's email address
     * @param theSubject         the subject of the email
     * @param theBody            the body text of the email
     * @param theAttachmentPath  optional path to a file to attach (can be null)
     * @return                the constructed MimeMessage
     * @throws Exception      if the message fails to construct
     */
    public MimeMessage buildMessage(final Session theSession, final String theTo, final String theSubject,
                                    final String theBody, final String theAttachmentPath) throws Exception {
        MimeMessage message = new MimeMessage(theSession);

        // Set the sender and recipient addresses
        message.setFrom(new InternetAddress(mySenderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(theTo, true));
        message.setSubject(theSubject);

        // Create the message body
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(theBody);

        // Combine body and optional attachment
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        if (theAttachmentPath != null) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(theAttachmentPath));
            multipart.addBodyPart(attachmentPart);
        }

        // Set final content
        message.setContent(multipart);
        return message;
    }

    /**
     * Sends an email using the configured credentials and parameters.
     * @param theTo              recipient's email address
     * @param theSubject         subject line of the email
     * @param theBody            message body
     * @param theAttachmentPath  optional path to attachment file (null if none)
     */

    @Override
    public void sendEmail(final String theTo, final String theSubject, final String theBody, final String theAttachmentPath) {
        try {
            Session session = createSession(); // Create authenticated mail session
            MimeMessage message = buildMessage(session, theTo, theSubject, theBody, theAttachmentPath); // Build email
            Transport.send(message);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
        }
    }

    /**
     * Creates and configures a secure mail session for Gmail SMTP.
     * @return the authenticated mail Session
     */
    private Session createSession() {
        Properties props = new Properties();
        props.put(SMTP_AUTH, "true");
        props.put(SMTP_STARTTLS, "true");
        props.put(SMTP_HOST, "smtp.gmail.com");
        props.put(SMTP_PORT, "587");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mySenderEmail, mySenderPassword);
            }
        });
    }
}
