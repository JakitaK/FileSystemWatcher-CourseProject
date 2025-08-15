package test.view;

import org.junit.jupiter.api.*;
import view.CSVExporter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class CSVExporterTest {

    private JTable table;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create dummy data for JTable
        String[] columns = {"ID", "Name", "Age"};
        Object[][] data = {
                {1, "Alice", 30},
                {2, "Bob", 25}
        };
        table = new JTable(new DefaultTableModel(data, columns));

        // Create temporary file
        tempFile = File.createTempFile("test_export", ".csv");
        tempFile.deleteOnExit(); // Clean up after test
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testExportTableToCSV() throws IOException {
        String queryInfo = "SELECT * FROM users";
        CSVExporter.exportTableToCSV(table, tempFile.getAbsolutePath(), queryInfo);

        // Verify file content
        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.contains("Query: SELECT * FROM users"));
        assertTrue(content.contains("ID,Name,Age"));
        assertTrue(content.contains("1,Alice,30"));
        assertTrue(content.contains("2,Bob,25"));
    }

    @Test
    void testExportTableToCSVWithNulls() throws IOException {
        String[] columns = {"A", "B"};
        Object[][] data = {{null, "Value"}};
        JTable nullTable = new JTable(new DefaultTableModel(data, columns));

        File nullFile = File.createTempFile("null_test", ".csv");
        nullFile.deleteOnExit();

        CSVExporter.exportTableToCSV(nullTable, nullFile.getAbsolutePath(), "NULL test");

        String content = new String(Files.readAllBytes(nullFile.toPath()));
        assertTrue(content.contains("Query: NULL test"));
        assertTrue(content.contains("A,B"));
        assertTrue(content.contains(",Value"));
    }

}
