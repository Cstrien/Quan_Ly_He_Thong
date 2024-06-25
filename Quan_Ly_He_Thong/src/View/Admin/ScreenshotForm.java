package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.Base64;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ScreenshotForm extends JFrame {
    private PrintWriter out;
    private BufferedReader in;
    private JLabel imageLabel;
    private JButton saveButton;
    private JButton captureButton;
    private BufferedImage currentImage; 

    public ScreenshotForm(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
        initComponents();
    }

    private void initComponents() {
        setTitle("Screenshot Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        captureButton = new JButton("Capture Again");

        buttonPanel.add(saveButton);
        buttonPanel.add(captureButton);

        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureScreenshot();
            }
        });

        captureScreenshot();
    }

    public void setImage(BufferedImage image) {
        this.currentImage = image;
        imageLabel.setIcon(new ImageIcon(image));
    }

    private void captureScreenshot() {
        if (out != null) {
            out.println("screenshot");
            new Thread(() -> {
                try {
                    String imageData = in.readLine();
                    if (imageData != null && imageData.startsWith("SCREENSHOT:")) {
                        String base64Image = imageData.substring("SCREENSHOT:".length());
                        BufferedImage image = decodeFromBase64(base64Image);
                        if (image != null) {
                            SwingUtilities.invokeLater(() -> {
                                setImage(image); // Update image in the form
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(ScreenshotForm.this, "Failed to decode image.", "Error", JOptionPane.ERROR_MESSAGE);
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ScreenshotForm.this, "No valid image data received.", "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Not connected to server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveImage() {
        if (currentImage != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Image");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                    fileToSave = new File(fileToSave + ".png");
                }
                try {
                    ImageIO.write(currentImage, "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "Image saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to save image.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No image to save.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        // For testing purposes only
        // Create a dummy PrintWriter and BufferedReader
        PrintWriter dummyOut = new PrintWriter(System.out, true);
        BufferedReader dummyIn = new BufferedReader(new InputStreamReader(System.in));

        // Create and display the form
        ScreenshotForm form = new ScreenshotForm(dummyOut, dummyIn);
        form.setVisible(true);
    }
}
