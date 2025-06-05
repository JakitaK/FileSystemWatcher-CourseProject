package view;
import model.EmailSender;
import model.FileEvent;
import model.DatabaseManager;
import controller.FileMonitor;
import model.IEmailSender;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
    private boolean myEventsSaved = true;
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
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setJMenuBar(buildMenuBar());

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel configPanel = buildConfigPanel();
        JScrollPane tablePanel = buildTablePanel();

        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        myStatusLabel = new JLabel("Database is connected.");
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
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(startItem);
        fileMenu.add(stopItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);



        JMenu databaseMenu = new JMenu("Database");
        JMenuItem queryItem = new JMenuItem("Query Database");
        queryItem.addActionListener(e -> myQueryButton.doClick());
        databaseMenu.add(queryItem);
        menuBar.add(databaseMenu);

        JMenu aboutMenu = new JMenu("Help");
        JMenuItem shortcutItem = new JMenuItem("Shortcuts");
        shortcutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Keyboard Shortcuts:\n" +
                            "Alt+T or Ctrl+T - Start Monitoring\n" +
                            "Alt+P or Ctrl+P - Stop Monitoring\n" +
                            "Alt+S or Ctrl+S - Save to Database\n" +
                            "Alt+Q or Ctrl+Q - Query Database\n" +
                            "Alt+R or Ctrl+R - Reset\n",
                    "Keyboard Shortcuts",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem aboutItem = new JMenuItem("About this app");
        aboutItem.addActionListener(e -> showAboutDialog());
        aboutMenu.add(shortcutItem);
        aboutMenu.add(aboutItem);
        menuBar.add(aboutMenu);

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

        myBrowseButton.setBackground(Color.BLACK);
        myBrowseButton.setForeground(Color.WHITE);
        myBrowseButton.setFocusPainted(false);

        myBrowseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                myDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());

                // Message shown after selection
                JOptionPane.showMessageDialog(this,
                        "Directory selected. Click Start Monitoring Button to begin tracking changes.",
                        "Directory Selected",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        dirPanel.add(myDirectoryField);
        dirPanel.add(myBrowseButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        myStartButton = new JButton("Start Monitoring");
        myStopButton = new JButton("Stop Monitoring");
        mySaveButton = new JButton("Save to Database");
        myQueryButton = new JButton("Query Database");
        myResetButton = new JButton("Reset Screen");

        // Keyboard shortcuts: Mnemonics
        myStartButton.setMnemonic(KeyEvent.VK_T);
        myStopButton.setMnemonic(KeyEvent.VK_P);
        mySaveButton.setMnemonic(KeyEvent.VK_S);
        myQueryButton.setMnemonic(KeyEvent.VK_Q);
        myResetButton.setMnemonic(KeyEvent.VK_R);
        // Accelerators
        myStartButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "startMonitoring");
        myStartButton.getActionMap().put("startMonitoring", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myStartButton.doClick();
            }
        });

        myStopButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "stopMonitoring");
        myStopButton.getActionMap().put("stopMonitoring", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myStopButton.doClick();
            }
        });

        mySaveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "saveDatabase");
        mySaveButton.getActionMap().put("saveDatabase", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mySaveButton.doClick();
            }
        });

        myQueryButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "queryDatabase");
        myQueryButton.getActionMap().put("queryDatabase", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myQueryButton.doClick();
            }
        });

        myResetButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "resetScreen");
        myResetButton.getActionMap().put("resetScreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myResetButton.doClick();
            }
        });




        JButton[] buttons = { myStartButton, myStopButton, mySaveButton, myQueryButton, myResetButton };
        for (JButton button : buttons) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new Dimension(160, 35));
            buttonPanel.add(button);
        }

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

                JOptionPane.showMessageDialog(this,
                        "Files in the directory are now being monitored.",
                        "Monitoring Started",
                        JOptionPane.INFORMATION_MESSAGE);


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

                    JOptionPane.showMessageDialog(this,
                            "Monitoring has been stopped. The program is no longer observing the selected directory.",
                            "Monitoring Stopped",
                            JOptionPane.INFORMATION_MESSAGE);
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

        mySaveButton.addActionListener(e -> {
            if (myFileEvents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No file events to save.");
                return;
            }

            DatabaseManager db = new DatabaseManager("data/file_events.db");
            db.insertFileEvents(myFileEvents);
            myEventsSaved = true;
            db.close();

            JOptionPane.showMessageDialog(this, "File events saved to database.");
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
        myFileTable.getColumnModel().getColumn(0).setPreferredWidth(200); // File Name
        myFileTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Path
        myFileTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Extension
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
                "File System Watcher\n" +
                        "Group Members: Jakita Kaur, Ibadat Sandhu, Balkirat Singh\n" +
                        "Description: Monitors and logs file activity, with database querying and CSV export.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Opens the database query window in a new frame.
     */
    private void openQueryWindow() {
        IEmailSender emailSender = new EmailSender("filesystemwatcher360@gmail.com", "dayh umbg abut fyoj");
        DatabaseManager dbManager = new DatabaseManager("data/file_events.db");
        QueryWindow queryWindow = new QueryWindow(dbManager, emailSender);
        JFrame queryFrame = new JFrame("Query Window");
        queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        queryFrame.setContentPane(queryWindow);
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
            myEventsSaved = false;
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

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            // Confirm exit
            int confirmExit = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmExit != JOptionPane.YES_OPTION) {
                return; // User chose NO — cancel the close
            }

            // Ask about unsaved events only if exiting is confirmed
            if (!myEventsSaved && !myFileEvents.isEmpty()) {
                int result = JOptionPane.showConfirmDialog(this,
                        "You have unsaved file events. Do you want to save them before exiting?",
                        "Save before exit?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (result == JOptionPane.CANCEL_OPTION) {
                    return; // Stop closing
                } else if (result == JOptionPane.YES_OPTION) {
                    DatabaseManager db = new DatabaseManager("data/file_events.db");
                    db.insertFileEvents(myFileEvents);
                    db.close();
                    myEventsSaved = true;
                }
            }
        }

        super.processWindowEvent(e); // Continue closing
    }


}
