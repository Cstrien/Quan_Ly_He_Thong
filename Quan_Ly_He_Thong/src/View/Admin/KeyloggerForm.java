package View.Admin;

import javax.swing.*;
import java.awt.*;

public class KeyloggerForm extends JFrame {
    private static KeyloggerForm currentInstance; // Track the current instance

    private JTextArea keylogTextArea;

    public KeyloggerForm() {
        setTitle("Keylogger Data");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the window

        // Initialize components
        keylogTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(keylogTextArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        currentInstance = this; // Set the current instance
    }

    // Method to update the keylog data in the form
    public void updateKeylogData(String keylogData) {
        SwingUtilities.invokeLater(() -> {
            keylogTextArea.append(keylogData + "\n"); // Append new data

            // Ensure scrolling to the bottom for real-time view
            JScrollPane scrollPane = (JScrollPane) keylogTextArea.getParent().getParent();
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    // Static method to get the current instance
    public static KeyloggerForm getCurrentInstance() {
        return currentInstance;
    }
}
