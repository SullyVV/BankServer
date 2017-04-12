Code and Makefile for test infrastructure is in BankClient.
To compile and run test infra for function test, execute func-test.sh
You can modify the input xml freely by modify it in Client.java.
To compile and run test infra for load test, execute load-test.sh
You can modify number of concurrent clients by modify the num_clients variable in testInfra.java

Code and Makefile for server side is in BankServer.
To compile and run single thread server, execute bank-st.sh
To compile and run multi thread server, execute bank-mt.sh
You can modify number of threads in the thread pool by changing threads number in Bank.java
