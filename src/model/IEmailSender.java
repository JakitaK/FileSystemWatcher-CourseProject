/**
 * IEmailSender.java
 * Part of the File Watcher Project.
 * This interface defines the contract for sending emails, including the
 * recipient's email address, email subject, email body, and the file attachment path.
 * Implementations of this interface handle the actual sending logic.
 *
 * @author Ibadat Sandhu, Jakita Kaur, Balkirat Singh
 * @version Spring Quarter
 */

package model;

/**
 * IEmailSender defines an interface for sending emails with an attachment.
 */
public interface IEmailSender {
    /**
     * Sends an email to the specified recipient with a subject, body, and attachment.
     *
     * @param theRecipientEmail the recipient's email address
     * @param theSubject        the email subject
     * @param theBody           the email body text
     * @param theAttachmentPath the file path to the attachment to include
     */
    void sendEmail(final String theRecipientEmail, final String theSubject, final String theBody, final String theAttachmentPath) throws Exception;
}