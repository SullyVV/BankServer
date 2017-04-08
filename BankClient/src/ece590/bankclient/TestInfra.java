package ece590.bankclient;

import java.io.*;

public class TestInfra {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            System.err.println("Usage: java TestInfra <host_name> <port_number>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNumber = Integer.valueOf(args[1]);
        System.out.println("Host Server is: " + hostName);
        System.out.println("Port Number is: " + portNumber);
        System.out.println("Start test infrastructure...");
        int numClient = 100;
        Thread[] clientThread = new Thread[numClient];
        for (int i = 0; i < numClient; i++) {
            clientThread[i] = new Thread(new Client(i, hostName, portNumber));
        }
        // start all children
        long startTime = System.currentTimeMillis();
        for (Thread currThread : clientThread) {
            currThread.start();
        }
        // wait for completion of all children
        for (Thread currThread : clientThread) {
            currThread.join();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("It takes: " + (endTime - startTime) + " milliseconds for this test");
        System.out.println("Test Infrastructure completes");
    }
}
