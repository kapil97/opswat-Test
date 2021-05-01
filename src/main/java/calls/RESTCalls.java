package calls;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.FileProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RESTCalls implements RestCallsI{
    private String dataID;
    private String hash;
    private final String testUrl = "https://jsonplaceholder.typicode.com/albums";
    private final String hashUrl = "https://api.metadefender.com/v4/hash/";
    private final String uploadFileUrl = "https://api.metadefender.com/v4/file";
    private final String retriveFileResultsUrl = "https://api.metadefender.com/v4/file/";
    private final HttpClient httpClient;
    private File file;
    private String apikey;

    public RESTCalls(String apikey, File file){
        this.file = file;
        this.apikey = apikey;
        httpClient = HttpClient.newHttpClient();
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public boolean ifHashExists(String hash){
//        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET()
                .header("apiKey",apikey)
                .header("content-type","application/JSON")
                .uri(URI.create(hashUrl))
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
        System.out.println(response.body());
        JSONObject jsonObject = new JSONObject(response.body());
        JSONObject errorObject = jsonObject.getJSONObject("error");

        if(errorObject != null){
            System.out.println("Error: " + errorObject.getInt("code") + " messages: "+ errorObject.getJSONArray("messages"));
            int code = errorObject.getInt("code");
            return (code == 404003);
        }
        return false;
    }
    public void testJsonGETReq(){
//        HttpClient httpClient = HttpClient.newHttpClient();
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
