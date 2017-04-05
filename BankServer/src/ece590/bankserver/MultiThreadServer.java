package ece590.bankserver;

import common.database.DatabaseManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class MultiThreadServer {
    private boolean isStopped = false;
    private int portNum;
    public MultiThreadServer(int portNum) {
        this.portNum = portNum;
    }
    public void runServer() {
        System.out.println("Multi_Thread Bank Server running....");
        int t = 1;
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            while (!isStopped) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    new Thread(new WorkerRunnable(clientSocket,t)).start();
                    t++;
                } catch (IOException e) {
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