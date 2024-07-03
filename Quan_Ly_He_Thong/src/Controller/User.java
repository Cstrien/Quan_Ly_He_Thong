package Controller;

import Model.SystemInformation;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import javax.imageio.ImageIO;

public class User {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Địa chỉ server
        int serverPort = 5254; // Cổng server

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Gửi thông báo kết nối user
            out.println("USER");

            while (true) {
                String serverCommand = in.readLine();
                if (serverCommand == null) {
                    break;
                }

                System.out.println("Command from server: " + serverCommand);

                switch (serverCommand) {
                  case "OS_INFO":
                    String osName = System.getProperty("os.name");
                    String osVersion = System.getProperty("os.version");
                    String osArch = System.getProperty("os.arch");
                    long totalMemory = Runtime.getRuntime().totalMemory();

                    String osInfo = String.format("OS Name: %s\nOS Version: %s\nOS Architecture: %s\nTotal Memory: %d", osName, osVersion, osArch, totalMemory);
                    out.println("OS_INFO:" + osInfo);
                    break;

                    case "SCREENSHOT":
                        // Thực hiện chụp màn hình và gửi dữ liệu ảnh dạng Base64
                        String base64Image = takeScreenshot();
                        out.println("SCREENSHOT:" + base64Image);
                        break;
                    
                    case "SHUTDOWN":
                        try {
                            String os = System.getProperty("os.name").toLowerCase();
                            if (os.contains("win")) {
                                // Hiển thị thông báo trên Windows
                                Runtime.getRuntime().exec("cmd /c msg * /TIME:5 System will shut down in 5 seconds");
                                Thread.sleep(1000); // Đợi một chút trước khi chạy lệnh shutdown
                                Runtime.getRuntime().exec("shutdown -s -t 5");
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
                        break;


                    // Xử lý các lệnh khác tại đây nếu cần
                    default:
                        System.out.println("Unknown command: " + serverCommand);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String takeScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenFullImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
