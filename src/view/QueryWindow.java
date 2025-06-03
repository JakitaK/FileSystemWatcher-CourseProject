package view;

import model.EmailSender;
import model.IEmailSender;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class QueryWindow extends JPanel implements PropertyChangeListener {

    private final model.DatabaseManager databaseManager;
    private final IEmailSender emailSender;

    /** Button to send the email. */
    final private JButton myEmailButton;
    /** Button to export the csv file. */
    final private JButton myCsvButton;
    /** Button to return to main window. */
    final private JButton myMainWindowButton;
    /** Button to reset the database.*/
    final private JButton myResetButton;
    /** Dropdown to select query type */
    final private JComboBox<String> myComboBox;
    /** Table to display results */
    private JTable myResultTable;
    private DefaultTableModel myTableModel;


    public QueryWindow(model.DatabaseManager db, IEmailSender sender) {

        this.databaseManager = db;
        this.emailSender = sender;

        BorderLayout theLayout = new BorderLayout();
        setLayout(theLayout);

        // Top button panel setup with padding and horizontal layout
        JPanel myButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        myButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        myComboBox = new JComboBox<>(new String[]{
                "Choose query", "Query 1 - All rows", "Query 2 - Top 5", "Query 3 - Top 10"
        });

        myComboBox.addActionListener(e -> {
            String selected = (String) myComboBox.getSelectedItem();
            if ("Query 1 - All rows".equals(selected)) {
                runAllRowsQuery();
            } else if ("Query 2 - Top 5".equals(selected)) {
                runTop5Query();
            } else if ("Query 3 - Top 10".equals(selected)) {
                runTop10Query();
            }
        });

        myComboBox.setBackground(Color.WHITE);

        myEmailButton = new JButton("Send Email");
        myEmailButton.setToolTipText("Email exported CSV file");
        myEmailButton.addActionListener(e -> emailQueryResults());

        myCsvButton = new JButton("Export to CSV");
        myMainWindowButton = new JButton("Return to Main Window");
        myMainWindowButton.addActionListener(e -> {
            // Find and close the top-level window (JFrame) that contains this panel
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();  // Close only the Query Window frame
            }
        });
        myResetButton = new JButton("Reset Database");

        Color buttonBgColor = Color.BLACK;
        Color buttonFgColor = Color.WHITE;

        JButton[] buttons = { myEmailButton, myCsvButton, myMainWindowButton, myResetButton };
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

        //tester code for the Reset functionality in query
        myResetButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete all records from the database?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                model.DatabaseManager localdb = new model.DatabaseManager("data/file_events.db");
                try (java.sql.Statement stmt = db.getConnection().createStatement()) {
                    stmt.executeUpdate("DELETE FROM file_events");
                    JOptionPane.showMessageDialog(this, "Database has been reset.");
                    myTableModel.setRowCount(0); // Clear the query table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error resetting database: " + ex.getMessage());
                } finally {
                    db.close();
                }
            }
        });

        for (JButton button : buttons) {
            button.setBackground(buttonBgColor);
            button.setForeground(buttonFgColor);
        }

        myButtonPanel.add(new JLabel("Query to Select:"));
        myButtonPanel.add(myComboBox);
        myButtonPanel.add(myEmailButton);
        myButtonPanel.add(myCsvButton);
        myButtonPanel.add(myMainWindowButton);
        myButtonPanel.add(myResetButton);

        // Center panel with table
        String[] columnNames = {"File Name", "Path", "Extension", "Event", "Date", "Time"};
        myTableModel = new DefaultTableModel(columnNames, 0);
        myResultTable = new JTable(myTableModel);
        myResultTable.getColumnModel().getColumn(0).setPreferredWidth(200); // File Name
        myResultTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Path
        myResultTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Extension
        myResultTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Event Type
        myResultTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Date
        myResultTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Time

        myResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        myResultTable.setBackground(Color.WHITE);
        myResultTable.getTableHeader().setBackground(Color.WHITE);

        // Align headers to the left
        DefaultTableCellRenderer leftRenderer = (DefaultTableCellRenderer)
                myResultTable.getTableHeader().getDefaultRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane tableScrollPane = new JScrollPane(myResultTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Add panels to layout
        add(myButtonPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void runAllRowsQuery() {
        model.DatabaseManager localDb = new model.DatabaseManager("data/file_events.db");
        try (java.sql.ResultSet rs = localDb.queryAllRows()) {
            myTableModel.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                String name = rs.getString("file_name");
                String path = rs.getString("file_path");
                String ext = rs.getString("file_extension");
                String event = rs.getString("event_type");
                String datetime = rs.getString("datetime");

                String[] dateTimeParts = datetime.split(" ");
                String date = dateTimeParts[0];
                String time = dateTimeParts[1];

                myTableModel.addRow(new Object[]{name, path, ext, event, date, time});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running query: " + ex.getMessage());
        } finally {
            localDb.close();
        }
    }

    private void runTop5Query() {
        model.DatabaseManager db = new model.DatabaseManager("data/file_events.db");
        try (java.sql.ResultSet rs = db.queryTop5()) {
            myTableModel.setRowCount(0);

            while (rs.next()) {
                String name = rs.getString("file_name");
                String path = rs.getString("file_path");
                String ext = rs.getString("file_extension");
                String event = rs.getString("event_type");
                String datetime = rs.getString("datetime");

                String[] dateTimeParts = datetime.split(" ");
                String date = dateTimeParts[0];
                String time = dateTimeParts[1];

                myTableModel.addRow(new Object[]{name, path, ext, event, date, time});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running top 5 query: " + ex.getMessage());
        } finally {
            db.close();
        }
    }

    private void runTop10Query() {
        model.DatabaseManager db = new model.DatabaseManager("data/file_events.db");
        try (java.sql.ResultSet rs = db.queryTop10()) {
            myTableModel.setRowCount(0);

            while (rs.next()) {
                String name = rs.getString("file_name");
                String path = rs.getString("file_path");
                String ext = rs.getString("file_extension");
                String event = rs.getString("event_type");
                String datetime = rs.getString("datetime");

                String[] dateTimeParts = datetime.split(" ");
                String date = dateTimeParts[0];
                String time = dateTimeParts[1];

                myTableModel.addRow(new Object[]{name, path, ext, event, date, time});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error running top 10 query: " + ex.getMessage());
        } finally {
            db.close();
        }
    }


    private void emailQueryResults() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV file to email");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            String recipientEmail = JOptionPane.showInputDialog(this, "Enter recipient's email address:");

            if (recipientEmail != null && !recipientEmail.trim().isEmpty()) {
                try {
                    emailSender.sendEmail(
                            recipientEmail,
                            "File System Watcher - Query Results",
                            "Attached is your query result CSV file.",
                            selectedFile.getAbsolutePath()
                    );
                    JOptionPane.showMessageDialog(this, "Email sent successfully!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Failed to send email: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Placeholder for property change handling
    }
}