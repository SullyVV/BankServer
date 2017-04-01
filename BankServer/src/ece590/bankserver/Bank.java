package ece590.bankserver;

import common.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bank {
    private static Document xmlDocument;
    private static ConcurrentHashMap<Integer, Integer> actMap = new ConcurrentHashMap<>();   // use a synchronized hashmap for multi-thread convinience
    private static CopyOnWriteArrayList<TransInfo> transArray = new CopyOnWriteArrayList<>();
    /*
    no need to consider concurrency for operation array, because in multithread, each thread is responsible for one request
     */
    private static ArrayList<OpsInfo> opsArray = new ArrayList<>();


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
            //System.out.println("Size of the input msg is: " + len);
            String outMsg;
            if (len > 0) {
                byte[] inMsg = new byte[len];
                dis.readFully(inMsg, 0, len);
                String txt = new String(inMsg);
                XmlUtil xmlHandler = new XmlUtil();
                xmlDocument = xmlHandler.parseXml(txt);
                if (xmlDocument == null) {
                    // return error msg ---> request non-parsable
                    outMsg = "Non-parsable request";
                } else {
                    // proces xml object
                    outMsg = "This is the response";
                    xmlHandler.initOpsArray(xmlDocument, opsArray, transArray);
                    System.out.print("check");
//                    for (OpsInfo currOp : opsArray) {
//                        System.out.println();
//                        System.out.println("One Operation");
//                        System.out.println("opsType is: " + currOp.getOpsType());
//                        System.out.println("amount is: " + currOp.getAmt());
//                        System.out.println("balance is: " + currOp.getBal());
//                        System.out.println("account number is: " + currOp.getActNum());
//                        System.out.println("from account number is: " + currOp.getFromActNum());
//                        System.out.println("to account number is: " + currOp.getToActNum());
//                        System.out.println();
//                    }
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

