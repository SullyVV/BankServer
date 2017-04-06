package ece590.bankserver;

import common.database.DatabaseManager;
import common.operationType.OpsType;
import common.util.XmlUtil;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by JohnDong on 2017/4/4.
 */
public class WorkerRunnable implements Runnable {
    private Socket clientSocket;
    private ArrayList<OpsType> opsArray;
    private DatabaseManager databaseManager;
    private Document xmlDocument;
    private int t;
    public WorkerRunnable(Socket clientSocket, int t) {
        this.clientSocket = clientSocket;
        this.opsArray = new ArrayList<>();
        this.databaseManager = new DatabaseManager();
        this.t = t;
    }
    @Override
    public void run() {
        System.out.println("Start worker on thread: " + Thread.currentThread());
        try(DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
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
                    xmlHandler.generateXmlFile(newXmlDoc, "xml" + String.valueOf(t));
                    outMsg = xmlHandler.generateString(newXmlDoc);
                }
            } else {
                outMsg = "empty request";       // return error msg
            }
            byte[] bytes = outMsg.getBytes();
            dos.writeLong(bytes.length);
            dos.write(bytes);
            opsArray.clear();   // clear operation array of one transaction
            System.out.println("thread: " + Thread.currentThread() + " completes");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}