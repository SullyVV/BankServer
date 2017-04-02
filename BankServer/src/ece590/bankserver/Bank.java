package ece590.bankserver;

import common.*;
import org.w3c.dom.Document;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bank {
    private static Document xmlDocument;
    private static ConcurrentHashMap<Integer, Integer> actMap = new ConcurrentHashMap<>();   // use a synchronized hashmap for multi-thread convinience
    private static CopyOnWriteArrayList<TransferOps> transArray = new CopyOnWriteArrayList<>();
    /*
    no need to consider concurrency for operation array, because in multithread, each thread is responsible for one request
     */
    private static ArrayList<OpsType> opsArray = new ArrayList<>();


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
            long len = dis.readLong();
            //System.out.println("Size of the input msg is: " + len);
            String outMsg;
            if (len > 0) {
                byte[] inMsg = new byte[(int) len];
                dis.readFully(inMsg, 0, (int) len);
                String txt = new String(inMsg);
                XmlUtil xmlHandler = new XmlUtil();
                xmlDocument = xmlHandler.parseXml(txt);
                if (xmlDocument == null) {
                    // return error msg ---> request non-parsable
                    outMsg = "Non-parsable request";
                } else {
                    // proces xml object
                    outMsg = "This is the response";
//                    if (!xmlHandler.initOpsArray(xmlDocument, opsArray, transArray)) {
//                        System.out.println("Invalid Operations in the transaction");
//                    }
                    xmlHandler.initOpsArray(xmlDocument, opsArray, transArray);
                    System.out.print("checkpoint");
                }
            } else {
                // return error msg
                outMsg = "empty request";
            }
            byte[] bytes = outMsg.getBytes();
            dos.writeInt(bytes.length);
            dos.write(bytes);
            System.out.println("End of Communication");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

