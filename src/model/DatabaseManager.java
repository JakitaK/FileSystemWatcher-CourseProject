package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatabaseManager {

    private static final String TABLE_NAME = "file_events";
    private final String myDbPath;
    private Connection myConnection;

    public DatabaseManager(String theDbPath) {
        this.myDbPath = theDbPath;
        connect();
        createTableIfNeeded();
    }

    private void connect() {
        try {
            new java.io.File("data").mkdirs();
            Class.forName("org.sqlite.JDBC");
            myConnection = DriverManager.getConnection("jdbc:sqlite:" + myDbPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // for the reset funcitonaly for the query window
    public Connection getConnection() {
        return myConnection;
    }



    private void createTableIfNeeded() {
        final String sql = """
                CREATE TABLE IF NOT EXISTS %s (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    file_name TEXT NOT NULL,
                    file_path TEXT NOT NULL,
                    file_extension TEXT,
                    event_type TEXT,
                    date TEXT,
                    time TEXT
                );
                """.formatted(TABLE_NAME);
        try (Statement stmt = myConnection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertFileEvents(List<FileEvent> theEvents) {
        final String sql = """
                INSERT INTO %s (file_name, file_path, file_extension, event_type, date, time)
                VALUES (?, ?, ?, ?, ?, ?);
                """.formatted(TABLE_NAME);

        try (PreparedStatement pstmt = myConnection.prepareStatement(sql)) {
            for (FileEvent event : theEvents) {
                pstmt.setString(1, event.getFileName());
                pstmt.setString(2, event.getFilePath());
                pstmt.setString(3, event.getFileExtension());
                pstmt.setString(4, event.getEventType());

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                pstmt.setString(5, event.getEventTime().format(dateFormatter));
                pstmt.setString(6, event.getEventTime().format(timeFormatter));

                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (myConnection != null && !myConnection.isClosed()) {
                myConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet queryAllRows() {
        try {
            String sql = "SELECT file_name, file_path, file_extension, event_type, date || ' ' || time AS datetime FROM file_events";
            Statement stmt = myConnection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryTop5() {
        try {
            String sql = """
            SELECT file_name, file_path, file_extension, event_type,
                   date || ' ' || time AS datetime
            FROM file_events
            ORDER BY date DESC, time DESC
            LIMIT 5
        """;
            Statement stmt = myConnection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryTop10() {
        try {
            String sql = """
            SELECT file_name, file_path, file_extension, event_type,
                   date || ' ' || time AS datetime
            FROM file_events
            ORDER BY date DESC, time DESC
            LIMIT 10
        """;
            Statement stmt = myConnection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryByExtension(String extension) {
        try {
            String sql = """
        SELECT file_name, file_path, file_extension, event_type,
               date || ' ' || time AS datetime
        FROM file_events
        WHERE file_extension = ?
        ORDER BY datetime DESC
        """;
            PreparedStatement pstmt = myConnection.prepareStatement(sql);
            pstmt.setString(1, extension);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet queryByEventTypes(List<String> eventTypes) {
        try {
            // Build dynamic placeholders (?, ?, ...)
            String placeholders = String.join(", ", eventTypes.stream().map(e -> "?").toArray(String[]::new));

            String sql = """
        SELECT file_name, file_path, file_extension, event_type,
               date || ' ' || time AS datetime
        FROM file_events
        WHERE event_type IN (%s)
        ORDER BY datetime DESC
        """.formatted(placeholders);

            PreparedStatement pstmt = myConnection.prepareStatement(sql);
            for (int i = 0; i < eventTypes.size(); i++) {
                pstmt.setString(i + 1, eventTypes.get(i));
            }

            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ResultSet queryByDate(String date) {
        try {
            String sql = """
            SELECT file_name, file_path, file_extension, event_type,
                   date || ' ' || time AS datetime
            FROM file_events
            WHERE date = ?
            ORDER BY time DESC
        """;
            PreparedStatement pstmt = myConnection.prepareStatement(sql);
            pstmt.setString(1, date); // "yyyy-MM-dd"
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }





}
