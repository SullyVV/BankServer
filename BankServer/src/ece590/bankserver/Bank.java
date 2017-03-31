package ece590.bankserver;

import common.*;
import org.w3c.dom.Document;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class Bank {
    private static Document xmlDocument;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Bank <port number>");
            System.exit(1);
        }
        System.out.println("Bank Server Running...");
        int portNumber = Integer.valueOf(args[0]);
        try (
                    ServerSocket serverSocket = new ServerSocket(portNumber);
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        ) {
            int len = dis.readInt();
            System.out.println("Size of the input msg is: " + len);
            if (len > 0) {
                byte[] inMsg = new byte[len];
                dis.readFully(inMsg, 0, len);
                String txt = new String(inMsg);
                System.out.println("The input msg is: " + txt);
                XmlUtil xmlHandler = new XmlUtil();
                xmlDocument = xmlHandler.parseXml(txt);
            }
            String outMsg = "This is the msg for client";
            byte[] bytes = outMsg.getBytes();
            dos.writeInt(bytes.length);
            dos.write(bytes);
            System.out.println("End of Communication");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
