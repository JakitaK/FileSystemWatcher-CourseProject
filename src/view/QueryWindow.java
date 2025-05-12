package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class QueryWindow extends JPanel implements PropertyChangeListener {

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

    public QueryWindow() {
        BorderLayout theLayout = new BorderLayout();
        setLayout(theLayout);

        // Top button panel setup with padding and horizontal layout
        JPanel myButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        myButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        myComboBox = new JComboBox<>(new String[]{
                "Choose query", "Query 1 - All rows", "Query 2 - Top 5", "Query 3 - By Date"
        });
        myComboBox.setBackground(Color.WHITE);

        myEmailButton = new JButton("Send Email");
        myCsvButton = new JButton("Export to CSV");
        myMainWindowButton = new JButton("Return to Main Window");
        myResetButton = new JButton("Reset Database");

        Color buttonBgColor = Color.BLACK;
        Color buttonFgColor = Color.WHITE;

        JButton[] buttons = { myEmailButton, myCsvButton, myMainWindowButton, myResetButton };
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
        String[] columnNames = {"File Name", "Path", "Extension", "Event Type", "Date/Time"};
        myTableModel = new DefaultTableModel(columnNames, 0);
        myResultTable = new JTable(myTableModel);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Placeholder for property change handling
    }
}