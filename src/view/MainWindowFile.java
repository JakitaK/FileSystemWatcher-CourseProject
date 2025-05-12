package view;

import model.FileEvent;
import model.DatabaseManager;
import controller.FileMonitor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * Main application window for the File System Watcher.
 */
public class MainWindowFile extends JFrame implements PropertyChangeListener {

    private JComboBox<String> myExtensionComboBox;
    private JTextField myDirectoryField;
    private JButton myStartButton;
    private JButton myStopButton;
    private JButton mySaveButton;
    private JButton myQueryButton;
    private JButton myResetButton;
    private JButton myBrowseButton;
    private JTable myFileTable;
    private DefaultTableModel myTableModel;
    private JLabel myStatusLabel;
    FileMonitor myFileMonitor;

    private final List<FileEvent> myFileEvents = new ArrayList<>();

    /**
     * Constructs the main window UI for the file watcher.
     */
    public MainWindowFile() {
        super("File System Watcher");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setJMenuBar(buildMenuBar());

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel configPanel = buildConfigPanel();
        JScrollPane tablePanel = buildTablePanel();

        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        myStatusLabel = new JLabel("Database not connected.");
        myStatusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(myStatusLabel, BorderLayout.SOUTH);
    }

    /**
     * Builds the menu bar with File, About, Email, and Help menus.
     * @return the JMenuBar component
     */
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem startItem = new JMenuItem("Start");
        startItem.addActionListener(e -> myStartButton.doClick());
        JMenuItem stopItem = new JMenuItem("Stop");
        stopItem.addActionListener(e -> myStopButton.doClick());
        JMenuItem queryItem = new JMenuItem("Query Database");
        queryItem.addActionListener(e -> myQueryButton.doClick());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(startItem);
        fileMenu.add(stopItem);
        fileMenu.add(queryItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu aboutMenu = new JMenu("About");
        JMenuItem aboutItem = new JMenuItem("About this app");
        aboutItem.addActionListener(e -> showAboutDialog());
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

        JMenu databaseMenu = new JMenu("Database");
        JMenuItem connectItem = new JMenuItem("Connect to DataBase");
        connectItem.addActionListener(e -> {
            DatabaseManager myDatabaseManager = new DatabaseManager("data/file_events.db");
            //JOptionPane.showMessageDialog(this, "Database connected successfully.");
            myStatusLabel.setText("Database is connected.");
        });
        databaseMenu.add(connectItem);
        menuBar.add(databaseMenu);

        menuBar.add(new JMenu("Email"));
        menuBar.add(new JMenu("Help"));

        return menuBar;
    }

    /**
     * Builds the top configuration panel with extension selector, directory field, and control buttons.
     * @return the configuration panel
     */
    private JPanel buildConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel extensionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        extensionPanel.add(new JLabel("Monitor by extension:"));
        myExtensionComboBox = new JComboBox<>(new String[] {
                "All extensions", "Custom extension",".txt", ".java", ".pdf", ".doc", ".png", ".log"
        });

        myExtensionComboBox.setBackground(Color.WHITE);
        //(optional if you want to change the color of the text from that grey to a black)
        // myExtensionComboBox.setForeground(Color.BLACK);


        myExtensionComboBox.addActionListener(e -> {
            String selected = (String) myExtensionComboBox.getSelectedItem();
            if ("Custom extension".equals(selected)) {
                String input = JOptionPane.showInputDialog(this,
                        "Enter your custom file extension (e.g., .log, .csv):",
                        ".custom");
                if (input != null && input.startsWith(".")) {
                    myExtensionComboBox.insertItemAt(input, 2); // inserts under 'Custom extension'
                    myExtensionComboBox.setSelectedItem(input); // selects the new value
                } else if (input != null) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid extension. It should start with a dot (e.g., .csv).",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    myExtensionComboBox.setSelectedIndex(0); // Reset to 'All extensions'
                }
            }
        });

        extensionPanel.add(myExtensionComboBox);

        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirPanel.add(new JLabel("Directory to monitor:"));
        myDirectoryField = new JTextField(35);
        myBrowseButton = new JButton("Browse");

        //changes to the Browse button cosmetically
        myBrowseButton.setBackground(Color.BLACK);
        myBrowseButton.setForeground(Color.WHITE);
        myBrowseButton.setFocusPainted(false);

        myBrowseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                myDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        dirPanel.add(myDirectoryField);
        dirPanel.add(myBrowseButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        myStartButton = new JButton("Start Monitoring");
        myStopButton = new JButton("Stop Monitoring");
        mySaveButton = new JButton("Save to Database");
        myQueryButton = new JButton("Query Database");
        myResetButton = new JButton("Reset");

        JButton[] buttons = { myStartButton, myStopButton, mySaveButton, myQueryButton, myResetButton };
        for (JButton button : buttons) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new Dimension(160, 35));
            buttonPanel.add(button);
        }

        myBrowseButton = new JButton("Browse");
        myBrowseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                myDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        myStopButton.setEnabled(false);

        myStartButton.addActionListener(e -> {
            try {
                String dir = myDirectoryField.getText();
                List<String> extensions = new ArrayList<>();
                String selected = (String) myExtensionComboBox.getSelectedItem();
                if (selected != null && !selected.equals("All extensions")) {
                    extensions.add(selected);
                }
                myFileMonitor = new FileMonitor(extensions);
                myFileMonitor.addPropertyChangeListener(this);
                myFileMonitor.startMonitoring(dir);
                myStartButton.setEnabled(false);
                myStopButton.setEnabled(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error starting monitor: " + ex.getMessage());
            }
        });

        myStopButton.addActionListener(e -> {
            try {
                if (myFileMonitor != null) {
                    myFileMonitor.stopMonitoring();
                    myStartButton.setEnabled(true);
                    myStopButton.setEnabled(false);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error stopping monitor: " + ex.getMessage());
            }
        });

        myResetButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to reset the file events?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                myFileEvents.clear();
                myTableModel.setRowCount(0);
            }
        });


        myQueryButton.addActionListener(e -> openQueryWindow());

        panel.add(extensionPanel);
        panel.add(dirPanel);
        panel.add(buttonPanel);
        return panel;
    }

    /**
     * Builds the scrollable table panel for file watcher events.
     * @return a JScrollPane wrapping the table
     */
    private JScrollPane buildTablePanel() {
        myTableModel = new DefaultTableModel(new String[] {
                "File Name", "Path","Extension","Event", "Date", "Time"
        }, 0);
        myFileTable = new JTable(myTableModel);
        myFileTable.setBackground(Color.WHITE);

        //Setting preferred column width
        myFileTable.getColumnModel().getColumn(0).setPreferredWidth(120); // File Name
        myFileTable.getColumnModel().getColumn(1).setPreferredWidth(350); // Path
        myFileTable.getColumnModel().getColumn(2).setPreferredWidth(70);  // Extension
        myFileTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Date
        myFileTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Time

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < myFileTable.getColumnCount(); i++) {
            myFileTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        JTableHeader header = myFileTable.getTableHeader();
        header.setBackground(Color.WHITE);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane scrollPane = new JScrollPane(myFileTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tableWrapper = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("File System Watcher Events", SwingConstants.LEFT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 10));
        tableWrapper.add(titleLabel, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        return new JScrollPane(tableWrapper);
    }

    /**
     * Displays an about dialog with group information.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "File System Watcher v1.0\n" +
                        "Group Members: Jakita Kaur, Ibadat Sandhu, Balkirat Singh\n" +
                        "This tool monitors and logs file activity.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Opens the database query window in a new frame.
     */
    private void openQueryWindow() {
        JFrame queryFrame = new JFrame("Query Window");
        queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        queryFrame.setContentPane(new view.QueryWindow());
        queryFrame.setSize(900, 500);
        queryFrame.setLocationRelativeTo(this);
        queryFrame.setVisible(true);
    }

    private String formatEventType(String rawType) {
        return switch (rawType) {
            case "ENTRY_CREATE" -> "Created";
            case "ENTRY_MODIFY" -> "Modified";
            case "ENTRY_DELETE" -> "Deleted";
            default -> rawType;
        };
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("fileEvent".equals(evt.getPropertyName())) {
            FileEvent event = (FileEvent) evt.getNewValue();
            myFileEvents.add(event);
            String myFormattedDate = event.getEventTime().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));;
            String myFormattedTime = event.getEventTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));;
            myTableModel.addRow(new Object[]{
                    event.getFileName(),
                    event.getFilePath(),
                    event.getFileExtension(),
                    formatEventType(event.getEventType()),
                    myFormattedDate,
                    myFormattedTime,

            });
        }

    }
}
