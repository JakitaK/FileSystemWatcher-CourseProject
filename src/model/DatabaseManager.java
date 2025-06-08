package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DatabaseManager manages file event database operations such as creating tables,
 * inserting events, and executing queries.
 */
public class DatabaseManager {

    /** Name of the database table. */
    private static final String TABLE_NAME = "file_events";

    /** Path to the SQLite database file. */
    private final String myDbPath;

    /** Connection to the SQLite database. */
    private Connection myConnection;

    /**
     * Constructs a DatabaseManager with the given database path.
     *
     * @param theDbPath the path to the SQLite database file
     */
    public DatabaseManager(final String theDbPath) {
        this.myDbPath = theDbPath;
        connect();
        createTableIfNeeded();
    }

    /**
     * Connects to the SQLite database and creates the data folder if needed.
     */
    private void connect() {
        try {
            new java.io.File("data").mkdirs();
            Class.forName("org.sqlite.JDBC");
            myConnection = DriverManager.getConnection("jdbc:sqlite:" + myDbPath);
        } catch (final Exception myException) {
            myException.printStackTrace();
        }
    }

    /**
     * Returns the database connection.
     * Primarily used by the reset functionality in the query window.
     *
     * @return the database connection
     */
    public Connection getConnection() {
        return myConnection;
    }


    /**
     * Creates the table if it doesn't exist.
     */
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
        try (Statement myStmt = myConnection.createStatement()) {
            myStmt.execute(sql);
        } catch (final SQLException myException) {
            myException.printStackTrace();
        }
    }

    /**
     * Inserts a list of file events into the database.
     *
     * @param theEvents the list of file events to insert
     */
    public void insertFileEvents(final List<FileEvent> theEvents) {
        final String mySql = """
                INSERT INTO %s (file_name, file_path, file_extension, event_type, date, time)
                VALUES (?, ?, ?, ?, ?, ?);
                """.formatted(TABLE_NAME);

        try (PreparedStatement myPstmt = myConnection.prepareStatement(mySql)) {
            for (FileEvent myEvent : theEvents) {
                myPstmt.setString(1, myEvent.getFileName());
                myPstmt.setString(2, myEvent.getFilePath());
                myPstmt.setString(3, myEvent.getFileExtension());
                myPstmt.setString(4, myEvent.getEventType());

                DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter myTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                myPstmt.setString(5, myEvent.getEventTime().format(myDateFormatter));
                myPstmt.setString(6, myEvent.getEventTime().format(myTimeFormatter));

                myPstmt.addBatch();
            }
            myPstmt.executeBatch();
        } catch (final SQLException myException) {
            myException.printStackTrace();
        }
    }


    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            if (myConnection != null && !myConnection.isClosed()) {
                myConnection.close();
            }
        } catch (final SQLException myException) {
            myException.printStackTrace();
        }
    }

    /**
     * Queries all rows from the database.
     *
     * @return the result set of all rows
     */
    public ResultSet queryAllRows() {
        try {
            final String mySql = "SELECT file_name, file_path, file_extension, event_type, date || ' ' || time AS datetime FROM file_events";
            Statement myStmt = myConnection.createStatement();
            return myStmt.executeQuery(mySql);
        } catch (SQLException myException) {
            myException.printStackTrace();
            return null;
        }
    }

    /**
     * Queries the top 5 recent file events.
     *
     * @return the result set of the top 5 rows
     */
    public ResultSet queryTop5() {
        try {
            final String mySql = """
            SELECT file_name, file_path, file_extension, event_type,
                   date || ' ' || time AS datetime
            FROM file_events
            ORDER BY date DESC, time DESC
            LIMIT 5
        """;
            final Statement myStmt = myConnection.createStatement();
            return myStmt.executeQuery(mySql);
        } catch (final SQLException myException) {
            myException.printStackTrace();
            return null;
        }
    }

    /**
     * Queries the top 10 recent file events.
     *
     * @return the result set of the top 10 rows
     */
    public ResultSet queryTop10() {
        try {
            final String mySql = """
            SELECT file_name, file_path, file_extension, event_type,
                   date || ' ' || time AS datetime
            FROM file_events
            ORDER BY date DESC, time DESC
            LIMIT 10
        """;
            final Statement myStmt = myConnection.createStatement();
            return myStmt.executeQuery(mySql);
        } catch (final SQLException myException) {
            myException.printStackTrace();
            return null;
        }
    }

    /**
     * Queries file events by file extension.
     *
     * @param theExtension the file extension to filter by
     * @return the result set of matching rows
     */
    public ResultSet queryByExtension(final String theExtension) {
        try {
            final String mySql = """
        SELECT file_name, file_path, file_extension, event_type,
               date || ' ' || time AS datetime
        FROM file_events
        WHERE file_extension = ?
        ORDER BY datetime DESC
        """;
            final PreparedStatement myPstmt = myConnection.prepareStatement(mySql);
            myPstmt.setString(1, theExtension);
            return myPstmt.executeQuery();
        } catch (SQLException myException) {
            myException.printStackTrace();
            return null;
        }
    }

    /**
     * Queries file events by event types.
     *
     * @param theEventTypes the list of event types to filter by
     * @return the result set of matching rows
     */
    public ResultSet queryByEventTypes(final List<String> theEventTypes) {
        try {
            final String myPlaceholders = String.join(", ", theEventTypes.stream().map(e -> "?").toArray(String[]::new));

            final String mySql = """
        SELECT file_name, file_path, file_extension, event_type,
               date || ' ' || time AS datetime
        FROM file_events
        WHERE event_type IN (%s)
        ORDER BY datetime DESC
        """.formatted(myPlaceholders);

            final PreparedStatement myPstmt = myConnection.prepareStatement(mySql);
            for (int i = 0; i < theEventTypes.size(); i++) {
                myPstmt.setString(i + 1, theEventTypes.get(i));
            }

            return myPstmt.executeQuery();
        } catch (final SQLException myException) {
            myException.printStackTrace();
            return null;
        }
    }

    /**
     * Queries file events by date.
     *
     * @param theDate the date to filter by (yyyy-MM-dd)
     * @return the result set of matching rows
     */
    public ResultSet queryByDate(final String theDate) {
        try {
            final String mySql = """
            SELECT file_name, file_path, file_extension, event_type,
                   date || ' ' || time AS datetime
            FROM file_events
            WHERE date = ?
            ORDER BY time DESC
        """;
            final PreparedStatement myPstmt = myConnection.prepareStatement(mySql);
            myPstmt.setString(1, theDate); // "yyyy-MM-dd"
            return myPstmt.executeQuery();
        } catch (final SQLException myException) {
            myException.printStackTrace();
            return null;
        }
    }





}
