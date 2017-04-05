package ece590.bankserver;


import common.database.DatabaseManager;

import java.io.*;

public class Bank {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Bank <port number>");
            System.exit(1);
        }
        int portNumber = Integer.valueOf(args[0]);
        // initialize database at first time, if already have data, comment the following two lines
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.initActMap();
        databaseManager.initTransfers();
        /*
        Single Thread approach
         */
//        SingleThreadServer server = new SingleThreadServer(portNumber);
        /*
        Multi thread approach: one thread per request
         */
//        MultiThreadServer server = new MultiThreadServer(portNumber);
        /*
        Multi thread approach: thread pool
         */
        int threadNum = 10;
        ThreadPoolServer server = new ThreadPoolServer(portNumber, threadNum);



        server.runServer();
    }
}

