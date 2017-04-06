package ece590.bankserver;

import common.database.DatabaseManager;
import common.operationType.OpsType;
import common.util.XmlUtil;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SingleThreadServer{
    private ArrayList<OpsType> opsArray;
    private DatabaseManager databaseManager;
    private boolean isStopped = false;
    private int portNum;
    public SingleThreadServer(int portNum){
        this.portNum = portNum;
        this.opsArray = new ArrayList<>();
        this.databaseManager = new DatabaseManager();
    }
    public void runServer() {
        System.out.println("Single_thread Bank Server running....");
        int v = 1;
        Document xmlDocument;
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            while (!isStopped) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {
                    long len = dis.readLong();
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
                            xmlHandler.reset(xmlDocument, databaseManager);
                            xmlHandler.initOpsArray(xmlDocument, opsArray);       // construct operation array from XML Document
                            xmlHandler.processOps(opsArray, databaseManager);       // modify actMap according to operation array
                            databaseManager.closeCnct();
                            Document newXmlDoc = xmlHandler.constructXml(opsArray);       // reconstruct XML document
                            xmlHandler.generateXmlFile(newXmlDoc, "xml" + String.valueOf(v));
                            outMsg = xmlHandler.generateString(newXmlDoc);
                            v++;
                        }
                    } else {
                        outMsg = "empty request";       // return error msg
                    }
                    byte[] bytes = outMsg.getBytes();
                    dos.writeLong(bytes.length);
                    dos.write(bytes);
                    opsArray.clear();   // clear operation array of one transaction
                    Thread.sleep(1); // wait for last msg to be delivered successfully
                    System.out.println("End of one Conversation");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void stop() {
        this.isStopped = true;
    }
}