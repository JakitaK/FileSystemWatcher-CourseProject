package model;

public interface IEmailSender {
    void sendEmail(String recipientEmail, String subject, String body, String attachmentPath);
}