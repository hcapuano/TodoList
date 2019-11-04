package com.example.suptodolist;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class APIRequest {

    private String getUrlParams(String[] paramNeeded, String[] paramToUse){
        String params = "";

        for (int i = 0; i < paramNeeded.length; i++){
            if (i != 0){ params+="&"; }
            params += paramNeeded[i]+"="+paramToUse[i];
        }

        return params;
    }

    public static String[] getParamNeeded(String action)
    {
        HashMap<String, String[]> items = new HashMap<String, String[]>();
        items.put("login", new String []{"action", "username", "password"});
        items.put("logout", new String []{"action", "username", "password"});
        items.put("register", new String []{"action", "username", "password", "firstname", "lastname", "email"});
        items.put("list", new String []{"action", "username", "password"});
        items.put("read", new String []{"action", "username", "password", "id"});
        items.put("update", new String []{"action", "username", "password", "id", "todo"});
        items.put("share", new String []{"action", "username", "password", "user"});

        return items.get(action);
    }

    public JSONArray make(String[] paramToUse) throws ExecutionException, InterruptedException {

        String[] paramNeeded = getParamNeeded(paramToUse[0]);
        String params = getUrlParams(paramNeeded, paramToUse);
        return new APICall(params).execute().get();

    }
}
