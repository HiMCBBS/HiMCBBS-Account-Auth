package com.himcbbs.play.serverclient.himcbbsauth.network;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

public class NetworkManager {
    private static final MediaType FORM = MediaType.get("application/x-www-form-urlencoded");
    public static final String API_TEST_HOST = "cn-js-01-bgp-c7k5s6d9.wxmc.top";
    public static final int API_TEST_PORT = 3625;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private static NetworkManager INSTANCE;

    private NetworkManager() {
        httpClient = new OkHttpClient();
        gson = new Gson();
    }

    public <T> T getObjectByResponse(Response response, TypeToken<?> typeToken) throws IOException, JsonParseException {
        T res;
        if(response.body()==null) {
            res = null;
        } else {
            res = gson.fromJson(response.body().string(), typeToken.getType());
        }
        return res;
    }

    public Response POST(String pathSegments, Map<String, String> formBody) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        for(Map.Entry<String, String> entry:formBody.entrySet()) {
            builder.add(entry.getKey(),entry.getValue());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .url(getApiUrl(pathSegments))
                .post(requestBody)
                .build();
        return httpClient.newCall(request).execute();
    }

    private HttpUrl getApiUrl(String pathSegments) {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(API_TEST_HOST)
                .port(API_TEST_PORT)
                .addPathSegments(pathSegments)
                .build();
    }

    public static NetworkManager getInstance() {
        if(INSTANCE==null) {
            INSTANCE = new NetworkManager();
        }
        return INSTANCE;
    }
}