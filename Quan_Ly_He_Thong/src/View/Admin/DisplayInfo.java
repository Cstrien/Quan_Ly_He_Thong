package View.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class DisplayInfo extends JFrame {
    private JTable osTable;
    private JTable fsRootTable;
    private JButton getButton;
    private JButton saveButton;
    private JButton clearButton;
    private BufferedReader in;
    private PrintWriter out;

    public DisplayInfo(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
        initializeUI();
        fetchInfoFromServer(); // Initial fetch of server info
    }

    private void initializeUI() {
        setTitle("Information System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Create tables
        osTable = new JTable(new DefaultTableModel(new Object[]{"Item", "Value"}, 0));
        fsRootTable = new JTable(new DefaultTableModel(new Object[]{"Item", "Value"}, 0));
        JScrollPane osScrollPane = new JScrollPane(osTable);
        JScrollPane fsRootScrollPane = new JScrollPane(fsRootTable);

        // Create buttons
        getButton = new JButton("Get");
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");

        // Add components to frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Information System", SwingConstants.CENTER), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("OS", SwingConstants.CENTER), gbc);
        gbc.gridx = 1;
        add(new JLabel("File system root", SwingConstants.CENTER), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(osScrollPane, gbc);

        gbc.gridx = 1;
        add(fsRootScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        buttonPanel.add(getButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, gbc);

        // Add button actions
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchInfoFromServer();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveInfo();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInfo();
            }
        });
    }

   private void fetchInfoFromServer() {
    if (out != null) {
        out.println("OS_INFO");

        new Thread(() -> {
            try {
                String response;
                StringBuilder dataBuilder = new StringBuilder();
                while ((response = in.readLine()) != null) {
                    System.out.println("Server response: " + response);
                    if (response.startsWith("OS_INFO:")) {
                        dataBuilder.append(response.substring("OS_INFO:".length())); // Exclude "OS_INFO:" prefix
                        String info = dataBuilder.toString().trim();
                        SwingUtilities.invokeLater(() -> {
                            DefaultTableModel model = (DefaultTableModel) osTable.getModel();
                            model.setRowCount(0); // Clear previous data

                            // Split info into lines and update the table
                            String[] lines = info.split("\n");
                            for (String line : lines) {
                                String[] parts = line.split(": ");
                                if (parts.length >= 2) {
                                    model.addRow(new Object[]{parts[0], parts[1]});
                                }
                            }
                        });
                        break;
                    } else {
                        dataBuilder.append(response).append("\n");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    } else {
        JOptionPane.showMessageDialog(DisplayInfo.this, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}





    private void saveInfo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Information");
        int userSelection = fileChooser.showSaveDialog(DisplayInfo.this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                DefaultTableModel osTableModel = (DefaultTableModel) osTable.getModel();
                for (int i = 0; i < osTableModel.getRowCount(); i++) {
                    String item = osTableModel.getValueAt(i, 0).toString();
                    String value = osTableModel.getValueAt(i, 1).toString();
                    writer.write(item + ": " + value + "\n");
                }
                JOptionPane.showMessageDialog(DisplayInfo.this, "Information saved successfully!", "Save Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DisplayInfo.this, "Failed to save information!", "Save Info", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void clearInfo() {
        DefaultTableModel osTableModel = (DefaultTableModel) osTable.getModel();
        osTableModel.setRowCount(0); // Clear table rows
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Example connection setup
            BufferedReader dummyIn = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter dummyOut = new PrintWriter(System.out, true);

            DisplayInfo displayInfo = new DisplayInfo(dummyIn, dummyOut);
            displayInfo.setVisible(true);
        });
    }
}
