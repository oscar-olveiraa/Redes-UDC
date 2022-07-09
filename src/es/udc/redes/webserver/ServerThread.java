package es.udc.redes.webserver;

import java.net.*;
import java.io.*;


public class ServerThread extends Thread {

    Socket socket;

    public ServerThread(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {

        BufferedReader input;
        OutputStream output;
        try {
            // Set the input channel
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Set the output channel
            output = socket.getOutputStream();
            //request that will be passed to the handler class
            String line;
            StringBuilder rq_builder = new StringBuilder();
            do {
                line = input.readLine();
                rq_builder.append(line);
                System.out.println(line);
                rq_builder.append(System.lineSeparator());
            } while (!line.equals(""));
            String request = rq_builder.toString();
            HttpRequest handler = new HttpRequest(request);
            //The HttpRequest handler responds using the output stream
            handler.respond(output);
            // Close the streams
            input.close();
            output.close();
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