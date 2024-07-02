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
                        out.println("OS_INFO:");
                        String osName = System.getProperty("os.name");
                        String osVersion = System.getProperty("os.version");
                        long totalMemory = Runtime.getRuntime().totalMemory();
                        
                        SystemInformation info = new SystemInformation(osName, osVersion, totalMemory);
                        out.println("OS_INFO:" + info.toString());
                        break;

                    case "SCREENSHOT":
                        // Thực hiện chụp màn hình và gửi dữ liệu ảnh dạng Base64
                        String base64Image = takeScreenshot();
                        out.println("SCREENSHOT:" + base64Image);
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
