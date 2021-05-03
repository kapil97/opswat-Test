package driver;

import calls.RESTCalls;
import calls.RestCallsI;
import io.github.cdimascio.dotenv.Dotenv;
import utils.FileProcessor;
import utils.FileProcessorI;


public class Driver {
    public static void main(String[] args) {
        if(args.length!=1){
            System.err.println("Invalid number of Arguments");
            return;
        }
        String inputFile = args[0];
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        FileProcessorI fileProcessor = new FileProcessor(inputFile);
        String md5Hash = fileProcessor.getChecksum("MD5");
        RestCallsI restCalls = new RESTCalls(apiKey, inputFile);
        boolean hashExists = restCalls.ifHashExists(md5Hash);
        if(hashExists){
            restCalls.retrieveAndPrintScanResults();
        }
        else {
            boolean isSuccessful = restCalls.uploadFile();
            if(isSuccessful)
                restCalls.retrieveAndPrintScanResults();
        }

    }
}
