package test.view;

import model.IEmailSender;
import model.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.QueryWindow;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

import static org.junit.jupiter.api.Assertions.*;

public class QueryWindowTest {

    private QueryWindow queryWindow;

    @BeforeEach
    public void setUp() {
        DatabaseManager db = new DatabaseManager(":memory:");
        IEmailSender sender = (recipientEmail, subject, body, attachmentPath) -> {
            // Do nothing
        };

        queryWindow = new QueryWindow(db, sender);

        JFrame testFrame = new JFrame();
        testFrame.setContentPane(queryWindow);
        testFrame.pack();
        testFrame.setVisible(true);
    }

    @Test
    public void testComboBoxHasAllOptions() {
        JComboBox<String> comboBox = (JComboBox<String>) findComponent(queryWindow, JComboBox.class);
        assertNotNull(comboBox);
        assertEquals(7, comboBox.getItemCount());
        assertEquals("Choose query", comboBox.getItemAt(0));
    }

    @Test
    public void testButtonsExist() {
        assertNotNull(findButtonWithText(queryWindow, "Send Email"));
        assertNotNull(findButtonWithText(queryWindow, "Export to CSV"));
        assertNotNull(findButtonWithText(queryWindow, "Return to Main Window"));
        assertNotNull(findButtonWithText(queryWindow, "Reset Database"));
    }

    @Test
    public void testPropertyChangeCoversMethod() {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "dummy", null, null);
        assertDoesNotThrow(() -> queryWindow.propertyChange(evt));
    }

    // Helpers
    private <T extends Component> T findComponent(Container container, Class<T> cls) {
        for (Component comp : container.getComponents()) {
            if (cls.isInstance(comp)) return cls.cast(comp);
            if (comp instanceof Container) {
                T result = findComponent((Container) comp, cls);
                if (result != null) return result;
            }
        }
        return null;
    }

    private JButton findButtonWithText(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(text)) {
                return (JButton) comp;
            } else if (comp instanceof Container) {
                JButton result = findButtonWithText((Container) comp, text);
                if (result != null) return result;
            }
        }
        return null;
    }
}
