package es.udc.redes.webserver;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("FORMATO: ServidorWeb <CONFIG_PATH>");
            System.exit(-1);
        }
        ServerSocket sServidor = null;
        try {
            int port = Integer.parseInt(argv[0]);
            sServidor = new ServerSocket(port);
            sServidor.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                Socket sCliente = sServidor.accept();
                // Create a ServerThread object, with the new connection as parameter
                ServerThread ts = new ServerThread(sCliente);
                // Initiate thread using the start() method
                ts.start();
            }
        } catch (IOException e) {
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