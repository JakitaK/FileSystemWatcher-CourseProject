package controller;

import view.MainWindowFile;
import java.awt.*;

/**
 * The entry point for the File System Watcher application.
 * This class initializes and launches the main GUI window MainWindowFile,
 * The actual query functionality and sub-windows are handled through buttons
 * or interactions inside MainWindowFile.
 * @author Ibadat Sandhu, Jakita Kaur and Balkirat Singh
 * @version Spring Quarter
 */
public class FileSystemMain {

    /**
     * The main method launches the File System Watcher GUI.
     * This method schedules the creation of the main application window on the
     * Event Dispatch Thread using EventQueue.invokeLater(Runnable) to ensure
     * thread safety in Swing.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Ensuring GUI is created on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {

            // Creating and showing the main window of the application
            MainWindowFile mainWindow = new MainWindowFile();
            mainWindow.setLocationRelativeTo(null); // Centering the window on the screen
            mainWindow.setVisible(true);            // Making the window visible
        });
    }
}
