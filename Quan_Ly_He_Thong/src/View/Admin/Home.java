package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.imageio.ImageIO;
import java.util.Base64;


public class Home extends JFrame {
    private JTextField ipTextField;
    private JButton connectButton;
    private JButton runningProcessesButton;
    private JButton runningApplicationsButton;
    private JButton keyStrokeButton;
    private JButton shutDownButton;
    private JButton displayButton;
    private JButton printScreenshotButton;
    private JButton getInfoButton;
    private JButton exitButton;
    private JLabel errorLabel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread responseReaderThread;

    public Home() {
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        ipTextField = new JTextField();
        connectButton = new JButton("Connect");
        runningProcessesButton = new JButton("Running Processes");
        runningApplicationsButton = new JButton("Running Applications");
        keyStrokeButton = new JButton("KeyStroke");
        shutDownButton = new JButton("Shut down");
        displayButton = new JButton("Display");
        printScreenshotButton = new JButton("Print screenshot");
        getInfoButton = new JButton("Get info");
        exitButton = new JButton("Exit");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        Dimension buttonSize = new Dimension(140, 30);
        connectButton.setPreferredSize(buttonSize);
        runningProcessesButton.setPreferredSize(buttonSize);
        runningApplicationsButton.setPreferredSize(buttonSize);
        keyStrokeButton.setPreferredSize(buttonSize);
        shutDownButton.setPreferredSize(buttonSize);
        displayButton.setPreferredSize(buttonSize);
        printScreenshotButton.setPreferredSize(buttonSize);
        getInfoButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(new JLabel("Enter IP:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(ipTextField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(connectButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(runningProcessesButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(runningApplicationsButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        add(keyStrokeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(shutDownButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(displayButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        add(printScreenshotButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(getInfoButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(exitButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        add(errorLabel, gbc);

         connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ip = ipTextField.getText();
                if (isValidIPv4(ip)) {
                    errorLabel.setText("Đã kết nối");
                    errorLabel.setForeground(Color.GREEN);
                    try {
                        socket = new Socket(ip, 5254);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out.println("ADMIN");

                        responseReaderThread = new Thread(() -> {
                            try {
                                String serverResponse;
                                while ((serverResponse = in.readLine()) != null) {
                                    System.out.println("Server: " + serverResponse);
                                }
                            } catch (IOException ex) {
                                System.err.println("Error reading from server: " + ex.getMessage());
                            }
                        });
                        responseReaderThread.start();

                        JOptionPane.showMessageDialog(Home.this, "Kết nối thành công!", "Message", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        errorLabel.setText("Kết nối thất bại.");
                    }
                } else {
                    errorLabel.setText("Kết nối thất bại.");
                    errorLabel.setForeground(Color.RED);
                }
            }
        });

         
      getInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (out != null) {
                    out.println("information");
                    new Thread(() -> {
                        try {
                            StringBuilder info = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                if (line.startsWith("INFORMATION:")) {
                                    info.append(line.substring(5)).append("\n");
                                } else if (line.equals("END_INFO")) {
                                    break;
                                }
                            }
                            SwingUtilities.invokeLater(() -> {
                                Info infoFrame = new Info(info.toString());
                                infoFrame.setVisible(true);
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } else {
                    errorLabel.setText("Not connected to server.");
                }
            }
        });

        runningProcessesButton.addActionListener(e -> sendCommand("runningProcesses"));
        
        runningApplicationsButton.addActionListener(e -> sendCommand("runningApplications"));
        keyStrokeButton.addActionListener(e -> sendCommand("keyStroke"));
        shutDownButton.addActionListener(e -> sendCommand("shutDown"));
        displayButton.addActionListener(e -> sendCommand("display"));
        
        
       printScreenshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (out != null) {
                    out.println("SCREENSHOT");
                    new Thread(() -> {
                        try {
                            // Chờ nhận dữ liệu hình ảnh từ server
                            String imageData = in.readLine();
                            if (imageData != null && imageData.startsWith("SCREENSHOT:")) {
                                String base64Image = imageData.substring("SCREENSHOT:".length());
                                BufferedImage image = decodeFromBase64(base64Image);
                                if (image != null) {
                                    SwingUtilities.invokeLater(() -> {
                                        ImageIcon icon = new ImageIcon(image);
                                        JLabel label = new JLabel(icon);
                                        JOptionPane.showMessageDialog(Home.this, label, "Screenshot", JOptionPane.PLAIN_MESSAGE);
                                    });
                                } else {
                                    SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(Home.this, "Failed to decode image.", "Error", JOptionPane.ERROR_MESSAGE);
                                    });
                                }
                            } else {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(Home.this, "No valid image data received.", "Error", JOptionPane.ERROR_MESSAGE);
                                });
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } else {
                    errorLabel.setText("Not connected to server.");
                }
            }
        });
 

        
        exitButton.addActionListener(e -> {
            sendCommand("exit");
            if (responseReaderThread != null && responseReaderThread.isAlive()) {
                responseReaderThread.interrupt();
            }
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.err.println("Error closing socket: " + ex.getMessage());
                }
            }
            System.exit(0);
        });

        setVisible(true);
    }

    private void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        } else {
            errorLabel.setText("Not connected to server.");
        }
    }

    private boolean isValidIPv4(String ip) {
        String ipv4Pattern = "^(\\d{1,3}\\.){3}\\d{1,3}$";
        if (ip.matches(ipv4Pattern)) {
            String[] parts = ip.split("\\.");
            for (String part : parts) {
                int intPart = Integer.parseInt(part);
                if (intPart < 0 || intPart > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
   private BufferedImage decodeFromBase64(String base64Image) {
    BufferedImage image = null;
    try {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        image = ImageIO.read(bis);
        bis.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return image;
}



    public static void main(String[] args) {
        new Home();
    }
}