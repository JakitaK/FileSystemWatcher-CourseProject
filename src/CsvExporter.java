import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {
    public static void exportToCsv(List<FileEvent> events, String outputPath, String queryInfo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("Query Info: " + queryInfo);
            writer.println("File Name,Extension,Path,Activity,Date Time");
            for (FileEvent e : events) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        e.getFileName(),
                        getFileExtension(e.getFileName()),
                        e.getFilePath(),
                        e.getEventType(),
                        e.getEventTime().toString());
            }
        }
    }

    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot + 1) : "";
    }
}
