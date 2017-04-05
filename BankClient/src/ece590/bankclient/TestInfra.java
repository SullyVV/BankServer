package ece590.bankclient;

import java.io.*;

public class TestInfra {

    public static void main(String[] args) throws IOException{
        if (args.length != 2) {
            System.err.println("Usage: java TestInfra <host_name> <port_number>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.valueOf(args[1]);
        System.out.println("Host Server is: " + hostName);
        System.out.println("Port Number is: " + portNumber);
        System.out.println("Start test infrastructure...");
        int numClient = 2;
        Thread[] clientThread = new Thread[numClient];
        for (int i = 0; i < numClient; i++) {
            clientThread[i] = new Thread(new Client(i, hostName, portNumber));
        }
        for (Thread currThread : clientThread) {
            currThread.start();
        }
        System.out.println("Test Infrastructure completes");
    }
}
