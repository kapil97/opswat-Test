package calls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.FileProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RESTCalls implements RestCallsI{
    private String dataID;
    private String hash;
    private final String testUrl = "https://jsonplaceholder.typicode.com/albums";
    private final String hashUrl = "https://api.metadefender.com/v4/hash/";
    private final String uploadFileUrl = "https://api.metadefender.com/v4/file";
    private final String retriveFileResultsUrl = "https://api.metadefender.com/v4/file/";
    private final HttpClient httpClient;
    private String filepath;
    private String apikey;

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
//            System.out.println(response.body());

        } catch (InterruptedException | IOException e) {
            System.err.println("No such file found");
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(response.body());
        JSONObject errorObject = null;
        boolean successful = false;
        try {
            errorObject = jsonObject.getJSONObject("error");
            if(errorObject!=null){
                int code = errorObject.getInt("code");
                if(code!= 400140) System.err.println("No endpoint found");
                successful = !(code == 400140);
                System.err.println(errorObject.getJSONArray("messages").toString());
            }
        }
        catch (JSONException exception){
            this.dataID = jsonObject.getString("data_id");
            successful = jsonObject.getString("status").equals("inqueue");
            System.out.println("dataID: " +dataID);
        }
        return successful;
    }

    public boolean ifHashExists(String hash){
        HttpRequest request = HttpRequest.newBuilder().GET()
                .header("apikey",apikey)
                .header("content-type","application/JSON")
                .uri(URI.create(hashUrl+hash))
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject(response.body());
        boolean result = false;
        JSONObject errorObject  = null;

        try {
            errorObject = jsonObject.getJSONObject("error");
//            System.out.println("Successful: ");
//            System.out.println(jsonObject);
            if (errorObject != null) {
//                System.out.println("Error: " + errorObject.getInt("code") + " messages: " + errorObject.getJSONArray("messages"));
                int code = errorObject.getInt("code");
                if(code!= 404003) System.err.println("No endpoint found");
                result = !(code == 404003);
                System.err.println(errorObject.getJSONArray("messages").toString());
            }
        }
        catch (JSONException exception){
            this.dataID = jsonObject.getString("data_id");
//            System.out.println("dataID: " +dataID);
            result = true;
        }
        return result;
    }

    public boolean retrieveScanResults(){
        return false;
    }


    public void testJsonGETReq(){
        HttpRequest request = HttpRequest.newBuilder().GET()
                .header("content-type","application/JSON")
                .uri(URI.create(testUrl))
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        System.out.println(response.body());

        JSONArray jsonArray = new JSONArray(response.body());
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject.get("id")+ " : " + jsonObject.get("title"));
        }

    }

}
