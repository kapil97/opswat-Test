

##OPSWAT ASSESSMENT

###Name: Kamleshwar Ragava
###Email: rskamleshwar@gmail.com
####Project Requirements and Dependencies:

1. [JAVA (jdk 11 or above)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
2. [Maven](https://maven.apache.org/download.cgi)
3. [org.json](https://mvnrepository.com/artifact/org.json/json/20140107)
4. [java-dotenv](https://github.com/cdimascio/java-dotenv)

#### Steps to build and run the project:
1. Set api key in the .env file in project root directory.
2. Change directory to the project root directory and type the following commands in the terminal / command line:
```
mvn clean
mvn compile
mvn package
java -jar target/opswat-1.0-SNAPSHOT.jar <file_path>
```


