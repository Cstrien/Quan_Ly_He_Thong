


package Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import com.sun.management.OperatingSystemMXBean;
public class ClientApp extends JFrame {
    private JTextArea statusArea;
    private JLabel connectionStatus;
    private JLabel cpuUsage;
    private JLabel ramUsage;
    private JLabel diskUsage;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public ClientApp() {
        setTitle("Client Monitor");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);

        connectionStatus = new JLabel("Connection Status: Not Connected");
        cpuUsage = new JLabel("CPU Usage: 0%");
        ramUsage = new JLabel("RAM Usage: 0%");
        diskUsage = new JLabel("Disk Usage: 0%");

        JButton shutdownButton = new JButton("Shutdown");
        JButton restartButton = new JButton("Restart");

        shutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performShutdown();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRestart();
            }
        });

        setLayout(new GridLayout(8, 1));
        add(connectionStatus);
        add(cpuUsage);
        add(ramUsage);
        add(diskUsage);
        add(shutdownButton);
        add(restartButton);
        add(scrollPane);

        setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            connectionStatus.setText("Connection Status: Connected");
            statusArea.append("Connected to server\n");

            new CommandListener().start();
            new SystemInfoSender().start();
        } catch (IOException e) {
            connectionStatus.setText("Connection Status: Failed to Connect");
            statusArea.append("Failed to connect to server\n");
        }
    }

    private class CommandListener extends Thread {
        public void run() {
            try {
                String command;
                while ((command = in.readLine()) != null) {
                    statusArea.append("Received command: " + command + "\n");
                    executeCommand(command);
                }
            } catch (IOException e) {
                connectionStatus.setText("Connection Status: Connection Lost");
                statusArea.append("Connection lost\n");
            }
        }
    }

    private void executeCommand(String command) {
        if (command.equals("SHUTDOWN")) {
            performShutdown();
        } else if (command.equals("RESTART")) {
            performRestart();
        }
    }

    private void performShutdown() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to shutdown?", "Shutdown", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                Runtime.getRuntime().exec("shutdown -s -t 0");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void performRestart() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to restart?", "Restart", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                Runtime.getRuntime().exec("shutdown -r -t 0");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class SystemInfoSender extends Thread {
        public void run() {
            try {
                while (true) {
                    String systemInfo = getSystemInfo();
                    out.println(systemInfo);
                    Thread.sleep(5000);
                }
            } catch (InterruptedException e) {
                statusArea.append("System info sender interrupted\n");
            }
        }
    }

    private String getSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double cpuLoad = osBean.getSystemCpuLoad() * 100;
        long totalMemory = osBean.getTotalPhysicalMemorySize();
        long freeMemory = osBean.getFreePhysicalMemorySize();
        double ramUsagePercentage = 100.0 - (freeMemory * 100.0 / totalMemory);

        cpuUsage.setText(String.format("CPU Usage: %.2f%%", cpuLoad));
        ramUsage.setText(String.format("RAM Usage: %.2f%%", ramUsagePercentage));
        diskUsage.setText("Disk Usage: N/A"); // Placeholder, as disk usage is more complex to fetch

        return String.format("CPU: %.2f%%, RAM: %.2f%%", cpuLoad, ramUsagePercentage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientApp::new);
    }
}
