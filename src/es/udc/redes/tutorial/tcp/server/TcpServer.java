package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.IOException;

//Oscar Olveira Miniño
//54224120X

/**
 * Multithread TCP echo server.
 */

public class TcpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.TcpServer <port>");
            System.exit(-1);
        }
        ServerSocket sServidor = null;
        try {
            // Create a server socket
            int port = Integer.parseInt(argv[0]);
            sServidor = new ServerSocket(port);
            // Set a timeout of 300 secs
            sServidor.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                Socket sCliente = sServidor.accept();
                // Create a ServerThread object, with the new connection as parameter
                ServerThread ts = new ServerThread(sCliente);
                // Initiate thread using the start() method
                ts.start();
            }
            // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //Close the socket
            try {
                sServidor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
