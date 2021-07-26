package com.inmd.javabot.spotify;

import com.inmd.javabot.commands.music.spotify;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class parsing {

    public static String json;

    public static void main() throws ParseException {

        getToken token = new getToken();
        getToken.main();

        try {
            String url = "https://api.spotify.com/v1/playlists/" + spotify.getplurl() + "/tracks?market=ES&fields=items(track(album(name)%2Cartists(name)))";

            OkHttpClient client = new OkHttpClient();

            Request.Builder builder = new Request.Builder().url(url).get();
            builder.addHeader("Accept", "application/json");
            builder.addHeader("Content-Type", "application/json");
            builder.addHeader("Authorization", "Bearer " + token.getspotify());
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    json = body.string();
                }
            }
            else
                System.err.println("Error Occurred");

        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    public static String getJson() {return json;}
}
