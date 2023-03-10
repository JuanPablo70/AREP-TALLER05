package edu.eci.arep.app;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static spark.Spark.*;

public class RoundRobin {
    private static String url = "http://192.168.12.21:3500";

    public static void main(String... args){
        port(getPort());

        staticFiles.location("/public");

        get("/logs", (req,res) -> {
            URL urlrr = new URL(url + getNumberPort() + "/logs");
            HttpURLConnection con = (HttpURLConnection) urlrr.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            System.out.println("-------AQUI-------");
            int responseCode = con.getResponseCode();
            StringBuffer response = new StringBuffer();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
            con.disconnect();
            return response.toString();
        });

        post("/logs", (request, response) -> {
            URL urlrr = new URL(url + getNumberPort() + "/logs");
            HttpURLConnection con = (HttpURLConnection) urlrr.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
            String requestBody = request.body();
            byte[] postData = requestBody.getBytes("UTF-8");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(postData);
            os.flush();
            os.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer responseBody = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
            in.close();

            con.disconnect();

            return responseBody.toString();
        });
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

    public static int getNumberPort() {
        return (int)(Math.random()*3+1);
    }


}
