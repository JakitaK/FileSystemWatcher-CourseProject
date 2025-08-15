/**
 * CSVExporter.java
 * Part of the File Watcher Project.
 * This utility class provides a method to export the contents of a JTable
 * to a CSV file, including a header line that describes the query being exported.
 *
 * @author Ibadat Sandhu, Jakita Kaur, Balkirat Singh
 * @version Spring Quarter
 */

package view;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;


/**
 * CSVExporter is a utility class that provides functionality to export the data
 * from a JTable to a CSV file. It includes the query description and table content.
 */
public class CSVExporter {

    /**
     * Exports the contents of a JTable to a CSV file with a header describing the query.
     *
     * @param theTable      The JTable to export.
     * @param theFilePath   The file path to write the CSV file to.
     * @param theQueryInfo  A string describing the query.
     * @throws IOException If writing the file fails.
     */
    public static void exportTableToCSV(final JTable theTable, final String theFilePath, final String theQueryInfo) throws IOException {
        try (FileWriter csvWriter = new FileWriter(theFilePath)) {
            csvWriter.write("Query: " + theQueryInfo + "\n\n");

            TableModel model = theTable.getModel();
            for (int i = 0; i < model.getColumnCount(); i++) {
                csvWriter.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) csvWriter.write(",");
            }
            csvWriter.write("\n");

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    csvWriter.write(value == null ? "" : value.toString());
                    if (col < model.getColumnCount() - 1) csvWriter.write(",");
                }
                csvWriter.write("\n");
            }
        }
    }

}
