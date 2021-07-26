package com.inmd.javabot.spotify;


import com.inmd.javabot.Bot;
import com.inmd.javabot.BotConfig;
import okhttp3.*;


import java.util.Base64;


public class getToken {

    public static String id,sc,result, Token;

    public static void main(){

        BotConfig config = new BotConfig(null);
        config.load();
        if(!config.isValid())
            return;

        Bot bot = new Bot(null, config, null);

        id = bot.getConfig().getClientId();
        sc = bot.getConfig().getClientSecret();
        result = id + ":" + sc;
        String encodedString = Base64.getEncoder().encodeToString(result.getBytes());
        //curl -H "Authorization: Basic " -d grant_type=client_credentials https://accounts.spotify.com/api/token
        try {
            String url = "https://accounts.spotify.com/api/token";
            String postBody = "grant_type=client_credentials";

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse(""), postBody);

            Request.Builder builder = new Request.Builder().url(url)
                    .addHeader("Authorization", "Basic " + encodedString)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody);
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    Token =  body.string().replace("{\"access_token\":\"", "").replace("\",\"token_type\":\"Bearer\",\"expires_in\":3600}", "");
                }
            } else
                System.err.println("Error Occurred");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getspotify() {return Token;}
}