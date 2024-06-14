package Controller;

import java.io.PrintWriter;

public class KeyEventHandler {
    private PrintWriter out;

    public KeyEventHandler(PrintWriter out) {
        this.out = out;
    }

    public void handleKeyEvent(String eventType, String keyText) {
        out.println("KEY:" + eventType + ": " + keyText);
    }
}
