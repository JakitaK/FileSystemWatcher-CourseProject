/**
 * FileEvent.java
 * Part of the File Watcher Project.
 * This class represents a single file event detected by the File System Watcher,
 * including details such as the file name, path, extension, event type, and timestamp.
 * It provides getters for each field and a custom toString() implementation for display.
 *
 * @author Ibadat Sandhu, Jakita Kaur, Balkirat Singh
 * @version Spring Quarter
 */


package model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single file event, containing details like file name, path, extension,
 * event type, and the time of the event.
 */
public class FileEvent  {

    /** The name of the file. */
    private final String myFileName;

    /** The full path to the file. */
    private final String myFilePath;

    /** The file extension (e.g., .txt, .java). */
    private final String myFileExtension;

    /** The type of event (e.g., ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE). */
    private final String myEventType;

    /** The date and time when the event occurred. */
    private final LocalDateTime myEventTime;


    /**
     * Constructs a new FileEvent object with the specified details.
     *
     * @param theFileName      the name of the file
     * @param theFilePath      the full path of the file
     * @param theFileExtension the file extension
     * @param theEventType     the type of event (ENTRY_CREATE, etc.)
     * @param theEventTime     the timestamp of the event
     */

    public FileEvent (final String theFileName, final String theFilePath, final String theFileExtension, final String theEventType, final LocalDateTime theEventTime ) {
        this.myFileName = theFileName;
        this.myFilePath = theFilePath;
        this.myFileExtension = theFileExtension;
        this.myEventType = theEventType;
        this.myEventTime = theEventTime;
    }
    /**
     * Returns the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return myFileName;
    }

    /**
     * Returns the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return myFilePath;
    }

    /**
     * Returns the event type.
     *
     * @return the event type
     */
    public String getEventType() {
        return myEventType;
    }

    /**
     * Returns the event time.
     *
     * @return the event time
     */
    public LocalDateTime getEventTime() {
        return myEventTime;
    }

    /**
     * Returns the file extension.
     *
     * @return the file extension
     */
    public String getFileExtension() {
        return myFileExtension;
    }

    /**
     * Returns a string representation of this FileEvent, including all details.
     *
     * @return a formatted string with event details
     */
    @Override
    public String toString() {
        final DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ("File Events: \n" +
                "File Name: " + getFileName() + "\n" +
                "File Extension: " + getFileExtension() + "\n" +
                "File Path: " + getFilePath() + "\n" +
                "Event Type: " + getEventType() + "\n" +
                "Event Time: " + getEventTime().format(myformatter));
    }
}
