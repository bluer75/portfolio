Simple calculator evaluating the value of given portfolio.
It processes positions from given file and uses price service to evaluate the value of that position.
Default portfolio currency is EUR and portfolio file is bobs_crypto.txt in current folder.
These values can be overridden with command line parameters:
 -c CURRENCY
 -f FILE_NAME

Prices are retrieved from external service available at https://min-api.cryptocompare.com/documentation
If proxy is required to connect to Internet, this can be specified with following java properties:
-Dhttps.proxyHost=PROXY_HOST
-Dhttps.proxyPort=PROXY_PORT
Logging of request and response can be enabled by specifying option -v.

Program was implemented as Maven project with Java 8 and JUnit 4.
Except JUnit 4, no other external libraries are needed for compilation and execution.

Program can be compiled (including execution of junit tests) with command:
mvn clean install

Program can be executed with command: 
java -cp target\portfolio-0.0.1-SNAPSHOT.jar -Dhttps.proxyHost=webproxy.com -Dhttps.proxyPort=8080 db.calc.PortfolioCalculator -f c:\temp\bobs_crypto.txt -c USD
or using default values:
java -cp target\portfolio-0.0.1-SNAPSHOT.jar db.calc.PortfolioCalculator

Evaluation of each positions and total value of portfolio is printed to standard output.
Parsing positions form input file and processing response from the service were intentionally kept simple.
Specifically, valid position in the file should have following format SYMBOL=QUANTITY, only one position per line is allowed. 
Invalid entries will be ignored and excluded from portfolio evaluation.

Please note that performance, security and thread safety aspects were not evaluated with this implementation.