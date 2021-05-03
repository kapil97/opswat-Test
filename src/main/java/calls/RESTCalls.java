package calls;

import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Class to perform REST Api calls to the server
 */
public class RESTCalls{
    private String dataID;
    private final String retrieveFileResultsUrl = "https://api.metadefender.com/v4/file/";
    private final String uploadFileUrl = "https://api.metadefender.com/v4/file";
    private final String hashUrl = "https://api.metadefender.com/v4/hash/";
    private final HttpClient httpClient;
    private String filepath;
    private String apikey;                     // api key
    private int retryDuration = 500;            //the time delay to call for the retrieval of scan results until progress is 100

    /**
     * Constructor for RESTCalls class
     * @param apikey api key
     * @param filepath path to the file
     */
    public RESTCalls(String apikey, String filepath){
        this.filepath = filepath;
        this.apikey = apikey;
        httpClient = HttpClient.newHttpClient();
    }

    /**
     * Method to set filepath of the file that needs to be scanned
     * @param filepath path to the file
     */
    public void setFile(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Method to upload file for the scan to the server
     * @return if upload to the server for the scan is successful
     */
    public boolean uploadFile(){
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofFile(Paths.get(filepath)))
                    .header("apikey",apikey)
                    .header("content-type", "application/octet-stream")
                    .uri(URI.create(uploadFileUrl))
                    .build();
            response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException | IOException e) {
            System.err.println("No such file found");
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(response.body());
        if(jsonObject.has("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            int code = errorObject.getInt("code");
            if(code!= 400140) System.err.println("No endpoint found");
            else System.err.println(errorObject.getJSONArray("messages").toString());
            return false;
        }
        else {
            this.dataID = jsonObject.getString("data_id");
            return jsonObject.getString("status").equals("inqueue");
        }
    }

    /**
     * Method to set the delay period for retrieval of scan results
     * @param retryDuration delay period for calling the retrieveScanResults method
     */
    public void setRetryDuration(int retryDuration) {
        this.retryDuration = retryDuration;
    }

    /**
     * Retrieve and print scan results to the console.
     */
    public void retrieveAndPrintScanResults() {
        JSONObject jsonObject = retrieveScanResults();
        JSONObject scanResults = jsonObject.getJSONObject("scan_results");
        if(scanResults.has("error")){
            JSONObject errorObject = scanResults.getJSONObject("error");
            int code = errorObject.getInt("code");
            if(code!= 404004) System.err.println("No endpoint found");
            else System.err.println(errorObject.getJSONArray("messages").toString());
        }
        else{

            while(scanResults.has("progress_percentage") && scanResults.getInt("progress_percentage") != 100){
                try {
                    Thread.sleep(retryDuration);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                scanResults = retrieveScanResults().getJSONObject("scan_results");
            }
            printResults(scanResults);
        }


    }

    /**
     * Method to check if hash is already present in the server
     * @param hash hash to be searched
     * @return if hash exists
     */
    public boolean ifHashExists(String hash){
        HttpRequest request = HttpRequest.newBuilder().GET()
                .header("apikey",apikey)
                .header("content-type","application/JSON")
                .uri(URI.create(hashUrl +hash))
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(response.body());
        if(jsonObject.has("error")){
                JSONObject errorObject = jsonObject.getJSONObject("error");
                int code = errorObject.getInt("code");
                if(code!= 404003) System.err.println("No endpoint found");
                return false;
        }
        else {
            this.dataID = jsonObject.getString("data_id");
            return true;
        }
    }

    /**
     * Private method to retrieve scan results from the server
     * @return response in the form of JSONObject
     */
    private JSONObject retrieveScanResults(){
        HttpRequest request = HttpRequest.newBuilder()
                .header("apikey",apikey)
                .uri(URI.create(retrieveFileResultsUrl+dataID))
                .build();
        HttpResponse<String> response = null;
        try{
            response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        }
        catch ( IOException | InterruptedException exception){
            exception.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(response.body());
        return jsonObject;
    }

    /**
     * Formatting and printing the scan results to the console.
     * @param scanResults JSONObject which needs to be formatted and printed
     */
    private void  printResults(JSONObject scanResults){
        File file = new File(filepath);
        System.out.println("filename: "+file.getName());
        System.out.println("overall_status: "+scanResults.getString("scan_all_result_a"));
        JSONObject scanDetails = scanResults.getJSONObject("scan_details");
        Iterator<String> scanDetailsIterator = scanDetails.keys();
        while(scanDetailsIterator.hasNext()){
            String engineName = scanDetailsIterator.next();
            JSONObject engineObject = scanDetails.getJSONObject(engineName);
            System.out.println("engine: "+engineName);
            System.out.println("threat_found: "+(engineObject.getString("threat_found").equals("")? "Clean" : engineObject.getString("threat_found")));
            System.out.println("scan_result: "+engineObject.getInt("scan_result_i"));
            System.out.println("def_time: "+ engineObject.getString("def_time"));
            System.out.println();
        }
        System.out.println("End");

    }
}
