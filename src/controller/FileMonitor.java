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

public class FileMonitor {

    private WatchService myWatcher;
    private boolean myMonitoring;
    private ExecutorService myExecutor;
    private final List<String> myExtensions;
    private final PropertyChangeSupport myChangeSupport;

    public FileMonitor(List<String> extensions) {
        this.myExtensions = extensions;
        this.myChangeSupport = new PropertyChangeSupport(this);
    }

    public void startMonitoring(String path) throws IOException {
        myWatcher = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(path);
        dir.register(myWatcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );

        myMonitoring = true;
        myExecutor = Executors.newSingleThreadExecutor();
        myExecutor.submit(() -> {
            while (myMonitoring) {
                WatchKey key;
                try {
                    key = myWatcher.take();
                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path changed = (Path) event.context();
                    String fileName = changed.toString();

                    if (myExtensions.isEmpty() || matchesExtension(fileName)) {
                        String extension = getExtension(fileName);
                        String fullPath = dir.resolve(changed).toAbsolutePath().toString();
                        String type = kind.name();

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
                key.reset();
            }
        });
    }

    public void stopMonitoring() throws IOException {
        myMonitoring = false;
        if (myExecutor != null) {
            myExecutor.shutdownNow();
        }
        if (myWatcher != null) {
            myWatcher.close();
        }
    }

    private boolean matchesExtension(String fileName) {
        for (String ext : myExtensions) {
            if (fileName.endsWith(ext)) return true;
        }
        return false;
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return "";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        myChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        myChangeSupport.removePropertyChangeListener(listener);
    }
}
