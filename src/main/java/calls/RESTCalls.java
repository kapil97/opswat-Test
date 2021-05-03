package calls;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RESTCalls implements RestCallsI{
    private String dataID;
    private final String retrieveFileResultsUrl = "https://api.metadefender.com/v4/file/";
    private final String uploadFileUrl = "https://api.metadefender.com/v4/file";
    private final String hashUrl = "https://api.metadefender.com/v4/hash/";
    private final HttpClient httpClient;
    private String filepath;
    private String apikey;
    private List<ScanResult> resultList = new ArrayList<>();

    public RESTCalls(String apikey, String filepath){
        this.filepath = filepath;
        this.apikey = apikey;
        httpClient = HttpClient.newHttpClient();
    }

    public void setFile(String filepath) {
        this.filepath = filepath;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public boolean uploadFile(){
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofFile(Paths.get(filepath)))
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
//            System.out.println("dataID: " +dataID);
            return jsonObject.getString("status").equals("inqueue");
        }
    }

    public void printScanResults() {
        JSONObject jsonObject = retrieveScanResults();
        JSONObject scanResults = jsonObject.getJSONObject("scan_results");
        if(scanResults.has("error")){
            JSONObject errorObject = scanResults.getJSONObject("error");
            int code = errorObject.getInt("code");
            if(code!= 404004) System.err.println("No endpoint found");
            else System.err.println(errorObject.getJSONArray("messages").toString());
        }

        else{
            while(scanResults.has("scan_all_result_a") && scanResults.getString("scan_all_result_a").equals("In queue")){
                scanResults = retrieveScanResults().getJSONObject("scan_results");
            }
            while(scanResults.has("progress_percentage") && scanResults.getInt("progress_percentage") != 100){
                scanResults = retrieveScanResults().getJSONObject("scan_results");
            }
            printResults(scanResults);
        }


    }

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
//                else System.err.println(errorObject.getJSONArray("messages").toString());
                return false;
        }
        else {
            this.dataID = jsonObject.getString("data_id");
            return true;
        }
    }

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
//        System.out.println("FileName: " +jsonObject.getJSONObject("file_info").getString("display_name"));
        return jsonObject;
    }

    private void  printResults(JSONObject scanResults){
        System.out.println("filename: "+filepath);
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
