package driver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import utils.FileProcessor;
import utils.FileProcessorI;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Driver {
    public static void main(String[] args) {
        if(args.length!=2){
            System.err.println("Invalid number of Arguments");
            return;
        }
        String inputFile = args[0];
        String apiKey = args[1];


        System.out.println("Api Key: "+ apiKey);
        FileProcessorI fileProcessor = new FileProcessor(inputFile);

        String md5Hash = fileProcessor.getChecksum("MD5");
        System.out.println("Hash: " + md5Hash);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET()
                .header("content-type","application/JSON")
                .uri(URI.create("https://jsonplaceholder.typicode.com/albums"))
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

//        JSONTokener jsonTokener = new JSONTokener(response.body());
//        JSONArray jsonArray = new JSONArray(jsonTokener);
//        for(int i=0; i<jsonArray.length(); i++){
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            System.out.println(jsonObject.get("id")+ " : " + jsonObject.get("title"));
//        }

    }
}
