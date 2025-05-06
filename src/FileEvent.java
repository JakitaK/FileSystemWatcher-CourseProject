import java.time.LocalDateTime;

public class FileEvent {
    private String myFileName;
    private String myFilePath;
    private String myEventType;
    private LocalDateTime myEventTime;

    public FileEvent(String theFileName, String theFilePath, String theEventType, LocalDateTime theEventTime) {
        myFileName = theFileName;
        myFilePath = theFilePath;
        myEventType = theEventType;
        myEventTime = theEventTime;
    }

    public String getFileName() { return myFileName; }
    public String getFilePath() { return myFilePath; }
    public String getEventType() { return myEventType; }
    public LocalDateTime getEventTime() { return myEventTime; }

}
