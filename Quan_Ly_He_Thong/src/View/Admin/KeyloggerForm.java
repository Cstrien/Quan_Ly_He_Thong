package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class KeyloggerForm extends JFrame {
    private JTextArea keyloggerTextArea;

    public KeyloggerForm(PrintWriter out, BufferedReader in) {
        setTitle("Keylogger Data");
        setSize(400, 300);
        setLayout(new BorderLayout());

        keyloggerTextArea = new JTextArea();
        keyloggerTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(keyloggerTextArea);

        add(scrollPane, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("KEYLOGS:")) {
                        keyloggerTextArea.append(line.substring("KEYLOGS:".length()) + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
