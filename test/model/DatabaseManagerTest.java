package model;

import model.DatabaseManager;
import model.FileEvent;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private DatabaseManager db;

    @BeforeEach
    void setUp() {
        // ✅ Use in-memory SQLite DB
        db = new DatabaseManager(":memory:");
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    private FileEvent createTestEvent(String name, String ext, String type, String path, LocalDateTime time) {
        return new FileEvent(name, path, ext, type, time);
    }

    @Test
    void testInsertAndQueryAllRows() throws SQLException {
        FileEvent event = createTestEvent("file.txt", ".txt", "ENTRY_CREATE", "/test/path", LocalDateTime.now());
        db.insertFileEvents(List.of(event));

        try (ResultSet rs = db.queryAllRows()) {
            assertNotNull(rs);
            assertTrue(rs.next());
            assertEquals("file.txt", rs.getString("file_name"));
        }
    }

    @Test
    void testQueryTop5AndTop10() throws SQLException {
        for (int i = 1; i <= 6; i++) {
            db.insertFileEvents(List.of(
                    createTestEvent("file" + i + ".txt", ".txt", "ENTRY_MODIFY", "/some/path", LocalDateTime.now().minusDays(i))
            ));
        }

        try (ResultSet rs5 = db.queryTop5()) {
            int count = 0;
            while (rs5.next()) count++;
            assertEquals(5, count);
        }

        try (ResultSet rs10 = db.queryTop10()) {
            int count = 0;
            while (rs10.next()) count++;
            assertEquals(6, count); // We inserted 6
        }
    }

    @Test
    void testQueryByExtension() throws SQLException {
        db.insertFileEvents(List.of(
                createTestEvent("one.java", ".java", "ENTRY_CREATE", "/code", LocalDateTime.now()),
                createTestEvent("two.txt", ".txt", "ENTRY_CREATE", "/docs", LocalDateTime.now())
        ));

        try (ResultSet rs = db.queryByExtension(".java")) {
            assertTrue(rs.next());
            assertEquals(".java", rs.getString("file_extension"));
            assertFalse(rs.next());
        }
    }

    @Test
    void testQueryByEventTypes() throws SQLException {
        db.insertFileEvents(List.of(
                createTestEvent("file1.txt", ".txt", "ENTRY_CREATE", "/some", LocalDateTime.now()),
                createTestEvent("file2.txt", ".txt", "ENTRY_DELETE", "/some", LocalDateTime.now())
        ));

        try (ResultSet rs = db.queryByEventTypes(Arrays.asList("ENTRY_CREATE"))) {
            assertTrue(rs.next());
            assertEquals("ENTRY_CREATE", rs.getString("event_type"));
            assertFalse(rs.next());
        }
    }

    @Test
    void testQueryByDate() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        db.insertFileEvents(List.of(
                createTestEvent("file.txt", ".txt", "ENTRY_MODIFY", "/x", now)
        ));

        try (ResultSet rs = db.queryByDate(dateStr)) {
            assertTrue(rs.next());
            assertEquals("file.txt", rs.getString("file_name"));
        }
    }

    @Test
    void testCloseDoesNotThrow() {
        assertDoesNotThrow(() -> db.close());
    }

    @Test
    void testDoubleCloseIsSafe() {
        db.close();
        assertDoesNotThrow(() -> db.close());
    }
}
