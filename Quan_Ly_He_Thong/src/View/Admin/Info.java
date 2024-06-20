package View.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Info extends JFrame {
    private JTextArea infoTextArea;
    private JTextArea defaultInfoTextArea;

    public Info(String info) {
        setTitle("Information");
        setSize(800, 300);  // Đặt kích thước rộng hơn để phù hợp với hai cột
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo JTextArea cho thông tin từ server
        infoTextArea = new JTextArea();
        infoTextArea.setText(info);
        infoTextArea.setEditable(false);
        
        // Tạo JTextArea cho thông tin mặc định về CPU, RAM, DISK, ETHERNET
        defaultInfoTextArea = new JTextArea();
        defaultInfoTextArea.setText(getDefaultInfo());
        defaultInfoTextArea.setEditable(false);

        // Tạo JSplitPane để chia giao diện thành hai cột
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(infoTextArea), new JScrollPane(defaultInfoTextArea));
        splitPane.setDividerLocation(400); // Đặt vị trí chia cột

        // Tạo JButton "Exit"
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng cửa sổ JFrame hiện tại
            }
        });

        // Thêm JSplitPane và JButton vào JFrame
        add(splitPane, BorderLayout.CENTER);
        add(exitButton, BorderLayout.SOUTH);
    }

    // Phương thức để lấy thông tin mặc định về CPU, RAM, DISK, ETHERNET
    private String getDefaultInfo() {
        return "CPU: \n" +
               "RAM: \n" +
               "DISK: \n" +
               "ETHERNET: \n";
    }

    // Hàm main để kiểm tra
    public static void main(String[] args) {
        // Tạo một đối tượng Info và hiển thị nó
        Info infoFrame = new Info("Thông tin chi tiết về chương trình...");
        infoFrame.setVisible(true);
    }
}
