package View.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class RunningApplicationsForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private BufferedReader in;
    private PrintWriter out;

    public RunningApplicationsForm(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
        initComponents();
        loadRunningApplications();
    }

    private void initComponents() {
        setTitle("Running Applications");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("PID");
        tableModel.addColumn("Session");
        tableModel.addColumn("Memory Usage");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRunningApplications();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRunningApplications() {
        tableModel.setRowCount(0); // Clear previous data
        if (out != null) {
            out.println("GET_RUNNING_APPLICATIONS");

            new Thread(() -> {
                try {
                    String response;
                    StringBuilder dataBuilder = new StringBuilder();
                    while ((response = in.readLine()) != null) {
                        System.out.println("Server response: " + response); // Debug statement

                        // Check for the start of the response
                        if (response.startsWith("RUNNING_APPLICATIONS:")) {
                            dataBuilder.append(response.substring("RUNNING_APPLICATIONS:".length()));
                        } else {
                            dataBuilder.append(response);
                        }

                        // Split the accumulated data into lines
                        String[] apps = dataBuilder.toString().split("\n");
                        dataBuilder.setLength(0); // Clear the builder

                        for (String app : apps) {
                            String[] appDetails = app.split(",");
                            if (appDetails.length >= 4) {
                                tableModel.addRow(new Object[]{appDetails[0], appDetails[1], appDetails[2], appDetails[3]});
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showErrorMessage("Not connected to server.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Example code to create RunningApplicationsForm
            BufferedReader dummyIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter dummyOut = new PrintWriter(System.out, true);
            RunningApplicationsForm form = new RunningApplicationsForm(dummyIn, dummyOut);
            form.setVisible(true);
        });
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
