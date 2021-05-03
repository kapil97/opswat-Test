package driver;

import calls.RESTCalls;
import io.github.cdimascio.dotenv.Dotenv;
import utils.FileProcessor;

/**
 * Driver Class: has main method, execution starts from here
 */

public class Driver {
    /**
     * Main Method
     * @param args filepath
     */
    public static void main(String[] args) {
        if(args.length!=1){
            System.err.println("Invalid number of Arguments");
            return;
        }
        String inputFile = args[0];
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");                             // setting api key
        FileProcessor fileProcessor = new FileProcessor(inputFile);
        String md5Hash = fileProcessor.getChecksum("MD5");           // getting hash of the input file
        RESTCalls restCalls = new RESTCalls(apiKey, inputFile);
        restCalls.setRetryDuration(500);                                  // setting up retry call duration for the progress of the file
        boolean hashExists = restCalls.ifHashExists(md5Hash);             // check if already hash exists in the server
        boolean isUploadSuccessful = true;
        if(!hashExists){
            isUploadSuccessful = restCalls.uploadFile();                  // check if file upload for scan is successful
        }
        if(isUploadSuccessful) restCalls.retrieveAndPrintScanResults();   // retrieve scan results and print it only if file upload was successful.
    }
}
