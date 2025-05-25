package test.model;

import model.FileEvent;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileEventTest {

    @Test
    public void testFileEventConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        FileEvent event = new FileEvent("sample.txt", "/home/user", ".txt", "CREATED", now);

        assertEquals("sample.txt", event.getFileName());
        assertEquals("/home/user", event.getFilePath());
        assertEquals(".txt", event.getFileExtension());
        assertEquals("CREATED", event.getEventType());
        assertEquals(now, event.getEventTime());
    }
}
