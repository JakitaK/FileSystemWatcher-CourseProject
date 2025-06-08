package controller;

import model.FileEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The FileMonitor class monitors a specified directory for file changes,
 * such as creation, modification, and deletion of files. It notifies registered
 * listeners with FileEvent objects whenever a relevant event is detected.
 *
 * @author Ibadat Sandhu, Jakita Kaur and Balkirat Singh
 * @version
 */
public class FileMonitor {

    private WatchService myWatcher;
    private boolean myMonitoring;
    private ExecutorService myExecutor;
    private final List<String> myExtensions;
    private final PropertyChangeSupport myChangeSupport;

    /**
     * Constructs a FileMonitor instance with a list of file extensions to watch.
     * @param extensions a list of file extensions (e.g., ".txt", ".java") to filter; an empty list watches all files
     */
    public FileMonitor(List<String> extensions) {
        this.myExtensions = extensions;
        this.myChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * This method starts monitoring the specified directory path for file events.
     * @param path the directory to monitor
     * @throws IOException if the watch service cannot be initialized or the path is invalid
     */
    public void startMonitoring(String path) throws IOException {
        myWatcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(path);

        // Registering the directory with the WatchService to monitor for create, modify, and delete events
        dir.register(myWatcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );

        myMonitoring = true;
        myExecutor = Executors.newSingleThreadExecutor();

        // Running monitoring logic in a separate thread
        myExecutor.submit(() -> {
            while (myMonitoring) {
                WatchKey key;
                try {
                    key = myWatcher.take(); // Blocking until events are available
                } catch (InterruptedException e) {
                    return; // Exiting the loop if interrupted
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path changed = (Path) event.context();
                    String fileName = changed.toString();

                    // Filtering based on extensions, if provided
                    if (myExtensions.isEmpty() || matchesExtension(fileName)) {
                        String extension = getExtension(fileName);
                        String fullPath = dir.resolve(changed).toAbsolutePath().toString();
                        String type = kind.name();

                        // Creating and firing a FileEvent to all registered listeners
                        FileEvent fileEvent = new FileEvent(
                                fileName,
                                fullPath,
                                extension,
                                type,
                                LocalDateTime.now()
                        );

                        myChangeSupport.firePropertyChange("fileEvent", null, fileEvent);
                    }
                }
                key.reset(); // Resetting key to continue watching
            }
        });
    }

    /**
     * This method stops monitoring and releases resources associated with the WatchService.
     * @throws IOException if an error occurs while closing the WatchService
     */
    public void stopMonitoring() throws IOException {
        myMonitoring = false;
        if (myExecutor != null) {
            myExecutor.shutdownNow();
        }
        if (myWatcher != null) {
            myWatcher.close();
        }
    }

    /**
     * This method checks whether the given file name matches any of the specified extensions.
     * @param fileName the name of the file to check
     * @return true if the file ends with one of the specified extensions, otherwise false
     */
    private boolean matchesExtension(String fileName) {
        for (String ext : myExtensions) {
            if (fileName.endsWith(ext)) return true;
        }
        return false;
    }

    /**
     * Extracts the extension from a file name.
     * @param fileName the name of the file
     * @return the file extension including the dot (e.g., ".txt"), or an empty string if no extension exists
     */
    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return "";
    }

    /**
     * Registers a PropertyChangeListener to be notified when file events occur.
     * @param listener the listener to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        myChangeSupport.addPropertyChangeListener(listener);
    }
}
