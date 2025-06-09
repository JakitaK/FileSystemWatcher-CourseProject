// EmailSenderTest.java
package model;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailSenderTest {

    @Test
    public void testBuildMessageWithoutAttachment() throws Exception {
        EmailSender sender = new EmailSender("test@sender.com", "password");

        // Use dummy session
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage msg = sender.buildMessage(session, "receiver@test.com", "Test", "Hello", null);

        assertEquals("Test", msg.getSubject());
        assertEquals("test@sender.com", ((InternetAddress) msg.getFrom()[0]).getAddress());
    }

    @Test
    public void testBuildMessageWithAttachment() throws Exception {
        EmailSender sender = new EmailSender("test@sender.com", "password");

        Session session = Session.getDefaultInstance(new Properties());

        // Replace with a real file path or create a temp file
        String path = "src/test/resources/sample.txt";
        MimeMessage msg = sender.buildMessage(session, "receiver@test.com", "With Attachment", "Body", path);

        assertEquals("With Attachment", msg.getSubject());
        assertEquals("test@sender.com", ((InternetAddress) msg.getFrom()[0]).getAddress());
    }

    @Test
    public void testInvalidEmailThrowsAddressException() {
        assertThrows(AddressException.class, () -> {
            InternetAddress invalid = new InternetAddress("!!!invalid-email", true);  // true enables validation
            invalid.validate();  // This forces format validation and throws AddressException
        });
    }


    @Test
    public void testBuildMessageWithNullParams() {
        EmailSender sender = new EmailSender("test@sender.com", "password");

        Session session = Session.getDefaultInstance(new Properties());

        assertThrows(NullPointerException.class, () -> sender.buildMessage(session, null, null, null, null));
    }


    @Test
    public void testSendEmailCallsTransportSend() throws Exception {
        EmailSender sender = new EmailSender("test@sender.com", "password");

        // Mock Transport.send()
        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any(MimeMessage.class))).thenAnswer(invocation -> null);

            sender.sendEmail("receiver@test.com", "Subject", "Body", null);

            mockedTransport.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }
    }

    @Test
    public void testSendEmailWithAttachmentCallsTransportSend() throws Exception {
        EmailSender sender = new EmailSender("test@sender.com", "password");

        // Replace with a real file path or create a dummy test file
        String path = "src/test/resources/sample.txt";

        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
            mockedTransport.when(() -> Transport.send(any(MimeMessage.class))).thenAnswer(invocation -> null);

            sender.sendEmail("receiver@test.com", "With File", "Attached here", path);

            mockedTransport.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }
    }

    @Test
    public void testSendEmailSuccess() throws Exception {
        EmailSender sender = new EmailSender("test@sender.com", "password");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            // No exception thrown by Transport.send
            transportMock.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);

            sender.sendEmail("receiver@test.com", "Subject", "Body", null);

            transportMock.verify(() -> Transport.send(any(Message.class)));
        }
    }
    
}
