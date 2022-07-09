package es.udc.redes.tutorial.udp.server;

import java.io.IOException;
import java.net.*;

//Oscar Olveira Miniño
//DNI: 54224120X


/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }

        DatagramSocket datasocket = null;
        int port = Integer.parseInt(argv[0]);
        try {
            // Create a server socket
            datasocket = new DatagramSocket(port);
            // Set maximum timeout to 300 secs
            datasocket.setSoTimeout(30000);
            byte[] paquete = new byte[1024];

            while (true) {
                // Prepare datagram for reception
                DatagramPacket rdatapacket = new DatagramPacket(paquete, paquete.length);
                // Receive the message
                datasocket.receive(rdatapacket);
                System.out.println("SERVER: Received "
                        + new String(rdatapacket.getData(), 0, rdatapacket.getLength())
                        + " from " + rdatapacket.getAddress().toString() + ":"
                        + rdatapacket.getPort());
                // Prepare datagram to send response
                DatagramPacket sdatapacket = new DatagramPacket(rdatapacket.getData(), rdatapacket.getLength(), rdatapacket.getAddress(), rdatapacket.getPort());
                // Send response
                datasocket.send(sdatapacket);
                System.out.println("SERVER: Sending "
                        + new String(sdatapacket.getData(), 0, rdatapacket.getLength()) + " to "
                        + sdatapacket.getAddress().toString() + ":"
                        + sdatapacket.getPort());
            }
          
        // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
        // Close the socket
            if (datasocket != null) {
                datasocket.close();
            }
        }
    }
}
