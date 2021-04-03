package de.chaosolymp.chaosessentials.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangApiRequest {

    public static String requestName(String uuid) throws IOException, ParseException {
        URL url = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(3000);
        connection.setConnectTimeout(3000);
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed: HTTP error code: " + connection.getResponseCode());
        }
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonArray.class);
        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(jsonArray.get(0).toString());
        return jsonObject.get("name").toString();
    }
}
