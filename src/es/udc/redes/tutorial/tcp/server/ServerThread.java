package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * Thread that processes an echo server connection.
 */

public class ServerThread extends Thread {

    Socket socket;

    public ServerThread(Socket s) {
        // Store the socket s
        this.socket = s;
    }

    @Override
    public void run() {

        BufferedReader breader = null;
        PrintWriter pwriter = null;
        try {
            // Set the input channel
            breader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Set the output channel
            pwriter = new PrintWriter(socket.getOutputStream(), true);
            // Receive the message from the client
            String mensaje = breader.readLine();
            // Sent the echo message to the client
            pwriter.println(mensaje);
            // Close the streams
            if (breader != null) {
                breader.close();
            }

            if (pwriter != null) {
                pwriter.close();
            }
            // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Close the socket
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
