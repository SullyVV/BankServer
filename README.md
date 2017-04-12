Code and Makefile for test infrastructure is in BankClient.<br/>
To compile and run test infra for function test, execute func-test.sh <br/>
You can modify the input xml freely by modify it in Client.java.<br/>
To compile and run test infra for load test, execute load-test.sh<br/>
You can modify number of concurrent clients by modify the num_clients variable in testInfra.java<br/>

Code and Makefile for server side is in BankServer.<br/>
To compile and run single thread server, execute bank-st.sh<br/>
To compile and run multi thread server, execute bank-mt.sh<br/>
You can modify number of threads in the thread pool by changing threads number in Bank.java<br/>
