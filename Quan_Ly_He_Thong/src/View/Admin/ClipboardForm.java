package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClipboardForm extends JFrame {
    private JTextArea clipboardTextArea;
    private volatile boolean running; // Add a flag to control the thread

    public ClipboardForm(PrintWriter out, BufferedReader in) {
        setTitle("Clipboard Data");
        setSize(400, 300);
        setLayout(new BorderLayout());

        clipboardTextArea = new JTextArea();
        clipboardTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(clipboardTextArea);

        add(scrollPane, BorderLayout.CENTER);

        running = true; // Initialize the flag

        new Thread(() -> {
            try {
                String line;
                while (running && (line = in.readLine()) != null) {
                    if (line.startsWith("CLIPBOARD:")) {
                        final String clipboardData = line.substring("CLIPBOARD:".length());
                        EventQueue.invokeLater(() -> clipboardTextArea.append(clipboardData + "\n"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Add a window listener to stop the thread when the window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
            }
        });
    }
}
