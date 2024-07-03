package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

public class ShutdownForm extends JFrame {
    private JLabel countdownLabel;
    private JButton startButton;
    private JButton cancelButton;
    private Timer timer;
    private int countdown;

    private PrintWriter out;

    public ShutdownForm(PrintWriter out) {
        this.out = out;
        initComponents();
    }

    private void initComponents() {
        setTitle("Shutdown Timer");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        countdownLabel = new JLabel("Time remaining: 10 seconds", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(countdownLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start Countdown");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCountdown();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelCountdown();
            }
        });
    }

    private void startCountdown() {
        countdown = 10; // Countdown from 10 seconds
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdown--;
                countdownLabel.setText("Time remaining: " + countdown + " seconds");
                if (countdown <= 0) {
                    timer.stop();
                    sendShutdownCommand();
                }
            }
        });
        timer.start();
    }

    private void cancelCountdown() {
        if (timer != null) {
            timer.stop();
        }
        dispose();
    }

    private void sendShutdownCommand() {
        if (out != null) {
            out.println("SHUTDOWN");
        }
        JOptionPane.showMessageDialog(this, "Shutdown command sent.", "Shutdown", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrintWriter dummyOut = new PrintWriter(System.out, true);
            ShutdownForm form = new ShutdownForm(dummyOut);
            form.setVisible(true);
        });
    }
}
