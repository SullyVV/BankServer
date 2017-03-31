package ece590.bankclient;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException{
        if (args.length != 2) {
            System.err.println("Usage: java Client <host_name> <port_number>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.valueOf(args[1]);
        System.out.println("Host Server is: " + hostName);
        System.out.println("Port Number is: " + portNumber);
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                ) {
            // test for DataOutputStream
            String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<transaction><create ref=\"c1\">" + "<account>1234</account>" + "<balance>5000</balance>" + "</create></transaction>";
            byte[] message = xmlRequest.getBytes();
            dos.writeInt(message.length);
            dos.write(message);
            int len = dis.readInt();
            if (len > 0) {
                byte[] inMsg = new byte[len];
                dis.readFully(inMsg, 0, len);
                String txt = new String(inMsg);
                System.out.println("input msg is: " + txt);
            }
            System.out.println("End of Communication");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
