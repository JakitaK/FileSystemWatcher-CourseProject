package model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class FileEvent {

    final private String myFileName;
    final private String myFilePath;
    final private String myFileExtension;
    final private String myEventType;
    final private LocalDateTime myEventTime;

    public FileEvent (String theFileName, String theFilePath, String theFileExtension, String theEventType, LocalDateTime theEventTime ) {
        this.myFileName = theFileName;
        this.myFilePath = theFilePath;
        this.myFileExtension = theFileExtension;
        this.myEventType = theEventType;
        this.myEventTime = theEventTime;
    }
 
    public String getFileName() {
        return myFileName;
    }

    public String getFilePath() {
        return myFilePath;
    }

    public String getEventType() {
        return myEventType;
    }

    public LocalDateTime getEventTime() {
        return myEventTime;
    }

    public String getFileExtension() {
        return myFileExtension;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ("File Events: \n" +
                "File Name: " + getFileName() + "\n" +
                "File Extension: " + getFileExtension() + "\n" +
                "File Path: " + getFilePath() + "\n" +
                "Event Type: " + getEventType() + "\n" +
                "Event Time: " + getEventTime().format(formatter));
    }
}
