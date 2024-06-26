/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View.Admin;
import static com.sun.jna.Platform.isWindows;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
public class Processes extends JFrame {

    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton watchButton, killButton, startButton, clearButton, saveButton;

    public Processes() {
         // Set up the frame
        setTitle("Running Processes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name Process");
        tableModel.addColumn("ID Process");
        tableModel.addColumn("Session Name");

        // Create the table
        processTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(processTable);

        // Create buttons
        watchButton = new JButton("Watch");
        killButton = new JButton("Kill");
        startButton = new JButton("Start");
        clearButton = new JButton("Clear");
        saveButton = new JButton("Save");

         // Add action listeners to buttons
        watchButton.addActionListener(e -> populateTable());
        killButton.addActionListener(this::killProcess);
        startButton.addActionListener(this::startProcess);
        clearButton.addActionListener(e -> tableModel.setRowCount(0));
        saveButton.addActionListener(this::saveProcesses);

        // Layout setup
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(watchButton);
        buttonPanel.add(killButton);
        buttonPanel.add(startButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Populate the table initially
        populateTable();
    }

    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows

        // Fetch and display the running processes
        var processes = ProcessHandle.allProcesses()
            .filter(ProcessHandle::isAlive)
            .collect(Collectors.toList());

        for (ProcessHandle process : processes) {
            String name = process.info().command().orElse("Unknown");
            long pid = process.pid();
            String session = process.info().user().orElse("Unknown");

            tableModel.addRow(new Object[]{name, pid, session});
        }
    }
       private void killProcess(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, "Nhập PID hoặc tên quá trình để dừng:");
        if (input != null && !input.trim().isEmpty()) {
            try {
                String command;
                boolean isWindows = isWindows();
                if (input.matches("\\d+")) { // Kiểm tra nếu input là số (PID)
                    command = isWindows ? "taskkill /PID " + input : "kill " + input;
                } else { // Nếu input là tên quá trình
                    command = isWindows ? "taskkill /IM " + input : "pkill -f " + input;
                }
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();  // Đợi lệnh hoàn thành

                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this, "Quá trình đã dừng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể dừng quá trình. Mã thoát: " + exitCode, "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

                populateTable(); // Làm mới bảng
            } catch (IOException | InterruptedException ex) {
                JOptionPane.showMessageDialog(this, "Không thể dừng quá trình: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
     private void startProcess(ActionEvent e) {
        String command = JOptionPane.showInputDialog(this, "Enter the command to start a process:");
        if (command != null && !command.trim().isEmpty()) {
            try {
                Runtime.getRuntime().exec(command);
                populateTable(); // Refresh the table
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to start process: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
     private void saveProcesses(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu danh sách quá trình");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String name = (String) tableModel.getValueAt(i, 0);
                    long pid = (long) tableModel.getValueAt(i, 1);
                    String session = (String) tableModel.getValueAt(i, 2);
                    writer.write(name + ", " + pid + ", " + session);
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Các quá trình đã được lưu vào " + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Không thể lưu các quá trình: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Processes().setVisible(true);
        });
    }
}

