package ece590.bankserver;


import common.database.DatabaseManager;

import java.io.*;

public class Bank {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Bank <port number> <server type (single or multi)>");
            System.exit(1);
        }
        int portNumber = Integer.valueOf(args[0]);
        // initialize database and index for transfer table at first time, if already have data, comment the following two lines
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.initActMap();
        databaseManager.initTransfers();
        databaseManager.buildIndex();
        if (args[1].equals("single")) {
            SingleThreadServer server = new SingleThreadServer(portNumber);
            server.runServer();
        } else {
            int threadNum = 10;
            ThreadPoolServer server = new ThreadPoolServer(portNumber, threadNum);
            server.runServer();
        }
    }
}

