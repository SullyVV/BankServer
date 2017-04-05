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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by JohnDong on 2017/4/4.
 */
public class ThreadPoolServer {
    private boolean isStopped = false;
    private ExecutorService executorService;
    private int portNum;
    public ThreadPoolServer(int portNumber, int threadNum) {
        this.portNum = portNumber;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }
    public void runServer() {
        System.out.println("Multi_Thread (using thread pool) Bank Server running....");
        int t = 1;
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            while (!isStopped) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    executorService.execute(new WorkerRunnable(clientSocket, t));
                    t++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        executorService.shutdownNow();
        isStopped = true;
    }

}
