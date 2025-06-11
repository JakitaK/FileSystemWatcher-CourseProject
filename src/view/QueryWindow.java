/**
 * QueryWindow.java
 * Part of the File Watcher Project.
 * This class provides a graphical user interface (GUI) panel that allows users to query
 * the database for file events based on different criteria such as date, extension, and
 * event type. It also allows exporting query results to a CSV file and emailing them.
 *
 * @author Ibadat Sandhu, Jakita Kaur, Balkirat Singh
 * @version Spring Quarter
 */

package view;

import java.util.logging.Logger;
import java.util.logging.Level;
import model.DatabaseManager;
import model.IEmailSender;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;


/**
 * QueryWindow is a JPanel that enables users to perform queries on the file events
 * database. It supports queries by date, file extension, and event type. Results are
 * displayed in a JTable and can be exported as CSV or emailed to a recipient.
 * It includes GUI components such as buttons for actions, a combo box for query
 * selection, and table rendering.
 */

public class QueryWindow extends JPanel implements PropertyChangeListener {

    /** Logger for robust exception catching */
    private static final Logger LOGGER = Logger.getLogger(QueryWindow.class.getName());
    /** Email sender implementation. */
    private final IEmailSender myEmailSender;
    /** Dropdown to select query type */
    private final JComboBox<String> myComboBox;
    /** Table to display results */
    private JTable myResultTable;
    /** Table model for the query results. */
    private DefaultTableModel myTableModel;
    private static final String DATABASE_PATH = "data/file_events.db";

    /**
     * Constructs a new QueryWindow with the provided DatabaseManager and EmailSender.
     *
     * @param db     the database manager to handle queries
     * @param sender the email sender implementation
     */
    public QueryWindow(final DatabaseManager db, final IEmailSender sender) {

        this.myEmailSender = sender;

        BorderLayout theLayout = new BorderLayout();
        setLayout(theLayout);

        final JPanel myButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        myButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        myComboBox = new JComboBox<>(new String[]{
                "Choose query", "Query 1 - All rows", "Query 2 - Top 5", "Query 3 - Top 10", "Query 4 - Date", "Query 5 - File Extension", "Query 6 - Event Type"
        });

        myComboBox.addActionListener(e -> {
            final String selected = (String) myComboBox.getSelectedItem();
            if ("Query 1 - All rows".equals(selected)) {
                runAllRowsQuery();
            } else if ("Query 2 - Top 5".equals(selected)) {
                runTop5Query();
            } else if ("Query 3 - Top 10".equals(selected)) {
                runTop10Query();
            }else if ("Query 4 - Date".equals(selected)) {
                final String input = JOptionPane.showInputDialog(this, "Enter date (MM-dd-yyyy):");
                if (input != null && !input.isBlank()) {
                    try {
                        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                        DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        LocalDate parsed = LocalDate.parse(input.trim(), inputFormat);
                        String formattedDate = parsed.format(dbFormat);

                        runDateQuery(formattedDate);
                    } catch (final Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yyyy.");
                    }
                }
            } else if ("Query 5 - File Extension".equals(selected)) {
                final String ext = JOptionPane.showInputDialog(this, "Enter file extension (e.g., .txt):");
                if (ext != null && !ext.isBlank()) {
                    runExtensionQuery(ext.trim());
                }
            } else if ("Query 6 - Event Type".equals(selected)) {
                final String type = JOptionPane.showInputDialog(this, "Enter event type (e.g., CREATE, MODIFY, DELETE):");
                if (type != null && !type.isBlank()) {
                    String input = type.trim().toUpperCase();
                    if (!input.startsWith("ENTRY_")) {
                        input = "ENTRY_" + input;
                    }
                    runEventTypeQuery(Collections.singletonList(input));

                }
            }
        });

        myComboBox.setBackground(Color.WHITE);

        JButton myEmailButton = new JButton("Send Email");
        myEmailButton.setToolTipText("Email exported CSV file");
        myEmailButton.addActionListener(e -> emailQueryResults());

        JButton myCsvButton = new JButton("Export to CSV");
        JButton myMainWindowButton = new JButton("Return to Main Window");
        myMainWindowButton.addActionListener(e -> {
            final Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
        JButton myResetButton = new JButton("Reset Database");

        final Color buttonBgColor = Color.BLACK;
        final Color buttonFgColor = Color.WHITE;

        final JButton[] buttons = {myEmailButton, myCsvButton, myMainWindowButton, myResetButton};
        myCsvButton.addActionListener(e -> {
            if (myTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No data to export.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            fileChooser.setSelectedFile(new java.io.File("query_results.csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try {
                    String queryDescription = (String) myComboBox.getSelectedItem();
                    CSVExporter.exportTableToCSV(myResultTable, fileToSave.getAbsolutePath(), queryDescription);
                    JOptionPane.showMessageDialog(this, "CSV exported successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage());
                }
            }
        });

        myResetButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete all records from the database?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                try (java.sql.Statement stmt = db.getConnection().createStatement()) {
                    stmt.executeUpdate("DELETE FROM file_events");
                    JOptionPane.showMessageDialog(this, "Database has been reset.");
                    myTableModel.setRowCount(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error resetting database: " + ex.getMessage());
                } finally {
                    db.close();
                }
            }
        });

        for (final JButton button : buttons) {
            button.setBackground(buttonBgColor);
            button.setForeground(buttonFgColor);
        }

        myButtonPanel.add(new JLabel("Query to Select:"));
        myButtonPanel.add(myComboBox);
        myButtonPanel.add(myEmailButton);
        myButtonPanel.add(myCsvButton);
        myButtonPanel.add(myMainWindowButton);
        myButtonPanel.add(myResetButton);

        final String[] columnNames = {"File Name", "Path", "Extension", "Event", "Date", "Time"};
        myTableModel = new DefaultTableModel(columnNames, 0);
        myResultTable = new JTable(myTableModel);
        myResultTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        myResultTable.getColumnModel().getColumn(1).setPreferredWidth(400);
        myResultTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        myResultTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        myResultTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        myResultTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        myResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        myResultTable.setBackground(Color.WHITE);
        myResultTable.getTableHeader().setBackground(Color.WHITE);

        final DefaultTableCellRenderer leftRenderer = (DefaultTableCellRenderer)
                myResultTable.getTableHeader().getDefaultRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane tableScrollPane = new JScrollPane(myResultTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(myButtonPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

    }
    /**
     * Runs a query that retrieves all rows from the database and displays them in the table.
     */
    private void runAllRowsQuery() {
        final DatabaseManager localDb = new DatabaseManager(DATABASE_PATH);
        try (ResultSet rs = localDb.queryAllRows()) {
            populateTableFromResultSet(rs);
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running query: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }

    /**
     * Runs a query that retrieves the top 5 rows from the database and displays them in the table.
     */
    private void runTop5Query() {
        final DatabaseManager localDb = new DatabaseManager(DATABASE_PATH);
        try (ResultSet rs = localDb.queryTop5()) {
            populateTableFromResultSet(rs);
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running top 5 query: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }

    /**
     * Runs a query that retrieves the top 10 rows from the database and displays them in the table.
     */
    private void runTop10Query() {
        final DatabaseManager localDb = new DatabaseManager(DATABASE_PATH);
        try (ResultSet rs = localDb.queryTop10()) {
            populateTableFromResultSet(rs);
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running top 10 query: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }

    /**
     * Runs a query that filters the file events by the specified date.
     *
     * @param date the date to filter by, in yyyy-MM-dd format
     */
    private void runDateQuery(final String date) {
        final DatabaseManager localDb = new DatabaseManager(DATABASE_PATH);
        try (ResultSet rs = localDb.queryByDate(date)) {
            populateTableFromResultSet(rs);
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this, "Error filtering by date: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }

    /**
     * Runs a query that filters the file events by the specified file extension.
     *
     * @param extension the file extension to filter by
     */
    private void runExtensionQuery(final String extension) {
        final DatabaseManager localDb = new DatabaseManager(DATABASE_PATH);
        try (ResultSet rs = localDb.queryByExtension(extension)) {
            populateTableFromResultSet(rs);
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this, "Error filtering by extension: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }


    /**
     * Runs a query that filters the file events by the specified event type(s).
     *
     * @param types a list of event types to filter by
     */
    private void runEventTypeQuery(final java.util.List<String> types) {
        final DatabaseManager localDb = new DatabaseManager(DATABASE_PATH);
        try (ResultSet rs = localDb.queryByEventTypes(types)) {
            populateTableFromResultSet(rs);
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this, "Error filtering by event type: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }

    /**
     * Exports the displayed query results to a CSV file and sends it by email.
     */
    private void emailQueryResults() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV file to email");
        final int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();

            final String recipientEmail = JOptionPane.showInputDialog(this, "Enter recipient's email address:");

            if (recipientEmail != null && !recipientEmail.trim().isEmpty()) {
                try {
                    myEmailSender.sendEmail(
                            recipientEmail,
                            "File System Watcher - Query Results",
                            "Attached is your query result CSV file.",
                            selectedFile.getAbsolutePath()
                    );
                    JOptionPane.showMessageDialog(this, "Email sent successfully!");
                } catch (final Exception e) {
                    JOptionPane.showMessageDialog(this, "Failed to send email: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Failed to send email", e);
                }
            }
        }
    }

    /**
     * Handles property change events.
     *
     * @param evt the property change event
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
    }

    /**
     * Populates the result table using data from the given ResultSet.
     *
     * @param rs the ResultSet containing query results
     * @throws Exception if a SQL or parsing error occurs
     */
    private void populateTableFromResultSet(final ResultSet rs) throws Exception {
        myTableModel.setRowCount(0);
        while (rs.next()) {
            final String name = rs.getString("file_name");
            final String path = rs.getString("file_path");
            final String ext = rs.getString("file_extension");
            final String event = rs.getString("event_type");
            final String datetime = rs.getString("datetime");

            final String[] dateTimeParts = datetime.split(" ");
            final String dateStr = dateTimeParts[0];
            final String time = dateTimeParts[1];
            final LocalDate date = LocalDate.parse(dateStr);
            final String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

            myTableModel.addRow(new Object[]{name, path, ext, event, formattedDate, time});
        }
    }

}