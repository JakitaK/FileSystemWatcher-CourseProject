import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtensionManager {
    private final List<String> myDefaultExtensions;
    private final List<String> myCustomExtensions;

    public ExtensionManager() {
        myDefaultExtensions = Arrays.asList("txt", "docx", "pdf", "java", "png", "jpg");
        myCustomExtensions = new ArrayList<>();
    }

    /**
     * Returns a combined list of default and custom extensions.
     */
    public List<String> getExtensions() {
        List<String> combined = new ArrayList<>(myDefaultExtensions);
        combined.addAll(myCustomExtensions);
        return combined;
    }

    /**
     * Adds a custom extension if it isn't already included.
     */
    public void addCustomExtension(String theExtension) {
        if (theExtension != null && !theExtension.isBlank()) {
            String trimmed = theExtension.trim().toLowerCase();
            if (!myDefaultExtensions.contains(trimmed) && !myCustomExtensions.contains(trimmed)) {
                myCustomExtensions.add(trimmed);
            }
        }
    }

    /**
     * Returns only the default extensions.
     */
    public List<String> getDefaultExtensions() {
        return new ArrayList<>(myDefaultExtensions);
    }

    /**
     * Returns only the custom extensions.
     */
    public List<String> getCustomExtensions() {
        return new ArrayList<>(myCustomExtensions);
    }
}
