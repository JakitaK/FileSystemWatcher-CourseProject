package view;

import model.FileEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application window for the File System Watcher.
 */
public class MainWindowFile extends JFrame {

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

        JLabel statusLabel = new JLabel("Database not connected.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
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
                "All extensions", "Custom extension", ".txt", ".java", ".pdf", ".doc", ".png", ".log"
        });
        extensionPanel.add(myExtensionComboBox);

        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirPanel.add(new JLabel("Directory to monitor:"));
        myDirectoryField = new JTextField(35);
        myBrowseButton = new JButton("Browse");
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

        myStopButton.setEnabled(false);
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
                "File Name", "Path", "Extension", "Date", "Time"
        }, 0);
        myFileTable = new JTable(myTableModel);
        myFileTable.setBackground(Color.WHITE);

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
}
