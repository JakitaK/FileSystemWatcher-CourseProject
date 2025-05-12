package controller;

import view.QueryWindow;
import view.MainWindowFile;
import javax.swing.*;
import java.awt.*;

public class FileSystemMain {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                /*
                // Placeholder main window panel (not yet implemented)
                JPanel mainPanel = new JPanel();
                mainPanel.add(new JLabel("Main Window - Under Construction"));

                // Create and configure the main application frame
                JFrame window = new JFrame("File System Watcher");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setContentPane(mainPanel);
                window.setSize(900, 600);
                window.setLocationRelativeTo(null);
                window.setVisible(true);

                // Launch the QueryWindow directly using JPanel style like CrapsView
                final QueryWindow queryPanel = new QueryWindow();
                JFrame queryFrame = new JFrame("Query Window");
                queryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                queryFrame.setContentPane(queryPanel);
                queryFrame.setSize(1000, 550);
                queryFrame.setLocationRelativeTo(null);
                queryFrame.setVisible(true);
                */

                // Actual MainWindowFile launch only and opens query window from the button
                MainWindowFile mainWindow = new MainWindowFile();
                mainWindow.setSize(900, 600);
                mainWindow.setLocationRelativeTo(null);
                mainWindow.setVisible(true);
            }
        });
    }
}