import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Connection myConnection;

    public DatabaseManager(String theDatabasePath) throws SQLException {
        myConnection = DriverManager.getConnection("jdbc:sqlite:" + theDatabasePath);
        initialize();
    }

    private void initialize() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS file_events (" +
                "file_name TEXT, " +
                "file_path TEXT, " +
                "event_type TEXT, " +
                "event_time TEXT)";
        try (Statement stmt = myConnection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertEvent(FileEvent event) throws SQLException {
        String sql = "INSERT INTO file_events (file_name, file_path, event_type, event_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
            pstmt.setString(1, event.getFileName());
            pstmt.setString(2, event.getFilePath());
            pstmt.setString(3, event.getEventType());
            pstmt.setString(4, event.getEventTime().format(FORMATTER));
            pstmt.executeUpdate();
        }
    }

    public List<FileEvent> queryByExtension(String extension) throws SQLException {
        String sql = "SELECT * FROM file_events WHERE file_name LIKE ?";
        try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
            pstmt.setString(1, "%." + extension);
            ResultSet rs = pstmt.executeQuery();
            return extractEvents(rs);
        }
    }

    public List<FileEvent> queryByDateRange(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT * FROM file_events WHERE date(event_time) BETWEEN ? AND ?";
        try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
            pstmt.setString(1, start.toString());
            pstmt.setString(2, end.toString());
            ResultSet rs = pstmt.executeQuery();
            return extractEvents(rs);
        }
    }

    public List<FileEvent> queryByActivity(String activityType) throws SQLException {
        String sql = "SELECT * FROM file_events WHERE event_type = ?";
        try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
            pstmt.setString(1, activityType);
            ResultSet rs = pstmt.executeQuery();
            return extractEvents(rs);
        }
    }

    public List<FileEvent> queryByPath(String path) throws SQLException {
        String sql = "SELECT * FROM file_events WHERE file_path LIKE ?";
        try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
            pstmt.setString(1, path + "%");
            ResultSet rs = pstmt.executeQuery();
            return extractEvents(rs);
        }
    }

    public void clearDatabase() throws SQLException {
        String sql = "DELETE FROM file_events";
        try (Statement stmt = myConnection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private List<FileEvent> extractEvents(ResultSet rs) throws SQLException {
        List<FileEvent> events = new ArrayList<>();
        while (rs.next()) {
            String fileName = rs.getString("file_name");
            String filePath = rs.getString("file_path");
            String eventType = rs.getString("event_type");
            LocalDateTime eventTime = LocalDateTime.parse(rs.getString("event_time"), FORMATTER);
            events.add(new FileEvent(fileName, filePath, eventType, eventTime));
        }
        return events;
    }
}
