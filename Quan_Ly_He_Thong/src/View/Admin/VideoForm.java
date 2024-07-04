package View.Admin;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public class VideoForm extends JFrame {
    private JLabel imageLabel;
    private JButton stopButton;
    private volatile boolean running;

    public VideoForm() {
        setTitle("Screenshot Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> stopFetchingScreenshots());
        add(stopButton, BorderLayout.SOUTH);

        running = true;
    }

    public void updateScreenshot(String base64Image) {
        if (!running) return;

        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopFetchingScreenshots() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VideoForm viewer = new VideoForm();
            viewer.setVisible(true);
        });
    }
}
