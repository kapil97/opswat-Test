package calls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

public class RESTCalls implements RestCallsI{
    private String dataID;
    private final String retrieveFileResultsUrl = "https://api.metadefender.com/v4/file/";
    private final String uploadFileUrl = "https://api.metadefender.com/v4/file";
    private final String hashUrl = "https://api.metadefender.com/v4/hash/";
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

        } catch (InterruptedException | IOException e) {
            System.err.println("No such file found");
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(response.body());

        if(jsonObject.has("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            int code = errorObject.getInt("code");
            if(code!= 400140) System.err.println("No endpoint found");
            System.err.println(errorObject.getJSONArray("messages").toString());
            return false;
        }
        else {
            this.dataID = jsonObject.getString("data_id");
            System.out.println("dataID: " +dataID);
            return jsonObject.getString("status").equals("inqueue");
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
                System.err.println(errorObject.getJSONArray("messages").toString());
                return false;
        }
        else {
            this.dataID = jsonObject.getString("data_id");
            System.out.println("dataID: " +dataID);
            return true;
        }
    }

    public boolean retrieveScanResults(){
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
        if(jsonObject.has("status") && jsonObject.getString("status").equals("inqueue")){

        }
        else if(jsonObject.has("error")){

        }
        else{

        }
        return false;
    }


    public void testJsonGETReq(){
        //    private String hash;
        String testUrl = "https://jsonplaceholder.typicode.com/albums";
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
//        System.out.println(response.body());

        JSONArray jsonArray = new JSONArray(response.body());
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject.get("id")+ " : " + jsonObject.get("title"));
        }

    }

}
