package com.example.suptodolist;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APICall extends AsyncTask<Void, Void, JSONArray> {

    private static final Object JSONArray = new JSONArray();
    private OkHttpClient client = new OkHttpClient();
    private String params;

    public APICall(String params) {
        this.params = params;
    }

    JSONObject run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject json = new JSONObject(makeRequest());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String makeRequest() throws IOException{

        OkHttpClient okHttpClient = new OkHttpClient();
        String result;
        Request request = new Request
                .Builder()
                .url("http://supinfo.steve-colinet.fr/suptodo?"+this.params)
                .build();
        Response response = okHttpClient.
                newCall(request).
                execute();
        result = response.body().string();
        return result;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {

        try {
            String result = makeRequest();
            String firstChar = String.valueOf(makeRequest().charAt(0));
            if (firstChar.equals("[")) {                    //If JsonArray
                return new JSONArray(result);
            }else{                                          //If JsonObject
                JSONArray jsonReturn = new JSONArray();
                JSONObject tempObj = new JSONObject(result);
                jsonReturn.put(tempObj);
                return jsonReturn;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
