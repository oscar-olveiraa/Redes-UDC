package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

//Oscar Olveira Miniño
//54224120X

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }

        ServerSocket socketServer = null;
        Socket socketClient = null;
        BufferedReader breader = null;
        PrintWriter pwriter = null;

        int port = Integer.parseInt(argv[0]);
        try {
            // Create a server socket
            socketServer = new ServerSocket(port);
            // Set a timeout of 300 secs
            socketServer.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                socketClient = socketServer.accept();
                // Set the input channel
                breader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                // Set the output channel
                pwriter = new PrintWriter(socketClient.getOutputStream(), true);
                // Receive the client message
                String mensaje = breader.readLine();
                System.out.println("SERVER: Received " + mensaje
                        + " from " + socketClient.getInetAddress().toString()
                        + ":" + socketClient.getPort());
                // Send response to the client
                pwriter.println(mensaje);
                System.out.println("SERVER: Sending " + mensaje +
                        " to " + socketClient.getInetAddress().toString() +
                        ":" + socketClient.getPort());
                // Close the streams
                if (breader != null) {
                    breader.close();
                }
                if (pwriter != null) {
                    pwriter.close();
                }
            }
            // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            //e.printStackTrace();
        } finally {
            //Close the socket
            try {
                if (socketServer != null) {
                    socketServer.close();
                }
                if (socketClient != null) {
                    socketClient.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
