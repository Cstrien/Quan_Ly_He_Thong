package Controller;

import java.io.PrintWriter;
import java.io.IOException;

public class KeyLogger {
    private PrintWriter out;
    private boolean running;

    public KeyLogger(PrintWriter out) {
        this.out = out;
    }

    public void start() {
        running = true;
        new Thread(() -> {
            try {
                while (running) {
                    // Giả lập việc lắng nghe sự kiện bàn phím và gửi tới server
                    // Thêm logic keylogger thực tế tại đây
                    out.println("KEY: Simulated key event");
                    Thread.sleep(1000); // Tạm dừng một chút để giả lập
                }
            } catch (InterruptedException e) {
                System.err.println("Keylogger interrupted: " + e.getMessage());
            }
        }).start();
    }

    public void stop() {
        running = false;
    }
}
