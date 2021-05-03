package driver;

import calls.RESTCalls;
import io.github.cdimascio.dotenv.Dotenv;
import utils.FileProcessor;


public class Driver {
    public static void main(String[] args) {
        if(args.length!=1){
            System.err.println("Invalid number of Arguments");
            return;
        }
        String inputFile = args[0];
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        FileProcessor fileProcessor = new FileProcessor(inputFile);
        String md5Hash = fileProcessor.getChecksum("MD5");
        RESTCalls restCalls = new RESTCalls(apiKey, inputFile);
        restCalls.setRetryDuration(500);
        boolean hashExists = restCalls.ifHashExists(md5Hash);

        boolean isUploadSuccessful = true;
        if(!hashExists){
            isUploadSuccessful = restCalls.uploadFile();
        }
        if(isUploadSuccessful) restCalls.retrieveAndPrintScanResults();
    }
}
