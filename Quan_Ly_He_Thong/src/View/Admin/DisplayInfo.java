package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;

public class DisplayInfo extends JFrame {
    private JTextArea infoTextArea;
    private PrintWriter out;

    public DisplayInfo() {
        setTitle("Display Info");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        infoTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton getInfoButton = new JButton("Get Info");
        getInfoButton.addActionListener(this::fetchInfoFromServer);
        add(getInfoButton, BorderLayout.SOUTH);
    }

    public void setPrintWriter(PrintWriter out) {
        this.out = out;
    }

    private void fetchInfoFromServer(ActionEvent e) {
        if (out != null) {
            out.println("GET_OS_INFO");
        } else {
            System.out.println("Not connected to server.");
        }
    }

    public void updateOSTable(String info) {
        infoTextArea.setText(info);
    }
}
