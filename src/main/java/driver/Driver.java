package driver;

import calls.RESTCalls;
import calls.RestCallsI;
import utils.FileProcessor;
import utils.FileProcessorI;

public class Driver {
    public static void main(String[] args) {
        if(args.length!=2){
            System.err.println("Invalid number of Arguments");
            return;
        }
        String inputFile = args[0];
        String apiKey = args[1];
        FileProcessorI fileProcessor = new FileProcessor(inputFile);
        String md5Hash = fileProcessor.getChecksum("MD5");

        RestCallsI restCalls = new RESTCalls(apiKey, inputFile);
        boolean hashExists = restCalls.ifHashExists(md5Hash);

        if(hashExists){
            restCalls.printScanResults();
        }
        else {
            boolean isSuccessful = restCalls.uploadFile();
            if(isSuccessful)
                restCalls.printScanResults();
        }

    }
}
