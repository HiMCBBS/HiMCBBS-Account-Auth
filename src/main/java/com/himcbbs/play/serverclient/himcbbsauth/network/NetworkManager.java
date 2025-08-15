package com.himcbbs.play.serverclient.himcbbsauth.network;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;

public class NetworkManager {
    private static final MediaType JSON = MediaType.get("application/json");
    private static final String API_TEST = "cn-js-01-bgp-c7k5s6d9.wxmc.top:3625";
    private final OkHttpClient httpClient;
    private final Gson gson;
    private static NetworkManager INSTANCE;

    private NetworkManager() {
        httpClient = new OkHttpClient();
        gson = new Gson();
    }

    public Object getObjectByResponse(Response response, Type type) throws IOException {
        return gson.fromJson(response.body().string(), type);
    }

    public Response POST(String pathSegments, String body) throws IOException {
        RequestBody requestBody = RequestBody.create("{}", JSON);
        if(body!=null) {
            requestBody = RequestBody.create(body, JSON);
        }
        Request request = new Request.Builder()
                .addHeader("Accept", "application/json")
                .url(getApiUrl(pathSegments))
                .post(requestBody)
                .build();
        try(Response response = httpClient.newCall(request).execute()) {
            return response;
        }
    }

    private HttpUrl getApiUrl(String pathSegments) {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(API_TEST)
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