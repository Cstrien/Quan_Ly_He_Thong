package View.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DisplayInfo extends JFrame {
    private JTable osTable;
    private JTable fsRootTable;
    private JButton getButton;
    private JButton saveButton;
    private JButton clearButton;
    private String serverInfo;

    public DisplayInfo() {
        this("");
    }

    public DisplayInfo(String info) {
        this.serverInfo = info;
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
                System.out.println("Get button pressed. ServerInfo: " + serverInfo);  // Debug print
                if (serverInfo != null && !serverInfo.isEmpty()) {
                    fetchInfoFromServer(serverInfo);
                } else {
                    JOptionPane.showMessageDialog(DisplayInfo.this, "No information available.", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

        System.out.println("Initial ServerInfo: " + serverInfo);  // Debug print
        if (serverInfo != null && !serverInfo.isEmpty()) {
            fetchInfoFromServer(serverInfo);
        }
    }

    private void fetchInfoFromServer(String info) {
        DefaultTableModel osTableModel = (DefaultTableModel) osTable.getModel();
        osTableModel.setRowCount(0);  // Clear the table

        String[] lines = info.split("\n");
        for (String line : lines) {
            if (line.startsWith("OS Name:")) {
                osTableModel.addRow(new Object[]{"OS Name", line.substring("OS Name:".length()).trim()});
            } else if (line.startsWith("OS Version:")) {
                osTableModel.addRow(new Object[]{"OS Version", line.substring("OS Version:".length()).trim()});
            } else if (line.startsWith("OS Architecture:")) {
                osTableModel.addRow(new Object[]{"OS Architecture", line.substring("OS Architecture:".length()).trim()});
            } else if (line.startsWith("Total Memory:")) {
                osTableModel.addRow(new Object[]{"Total Memory", line.substring("Total Memory:".length()).trim()});
            }
        }
    }

    private void saveInfo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }
            fileToSave = new File(filePath);

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                DefaultTableModel osTableModel = (DefaultTableModel) osTable.getModel();

                bw.write("OS Information:\n");
                for (int i = 0; i < osTableModel.getRowCount(); i++) {
                    bw.write(osTableModel.getValueAt(i, 0) + ": " + osTableModel.getValueAt(i, 1) + "\n");
                }

                JOptionPane.showMessageDialog(this, "Info saved to " + fileToSave.getAbsolutePath(), "Save Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving info", "Save Info", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearInfo() {
        ((DefaultTableModel) osTable.getModel()).setRowCount(0);
        ((DefaultTableModel) fsRootTable.getModel()).setRowCount(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String sampleInfo = "OS Name: Windows 10\nOS Version: 10.0\nOS Architecture: amd64\nTotal Memory: 1610612736";
            DisplayInfo infoFrame = new DisplayInfo(sampleInfo);
            infoFrame.setVisible(true);
        });
    }
}
