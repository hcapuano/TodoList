package com.example.suptodolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    private AccountDAO datasource;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.register);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

        datasource = new AccountDAO(this);
        datasource.open();

        Intent intent = getIntent();
        HashMap<String, String> params = (HashMap<String, String>) intent.getSerializableExtra("params");
        if (params != null){
            if (params.get("username") != null) ((EditText)findViewById(R.id.username)).setText(params.get("username"));
            if (params.get("password") != null) ((EditText)findViewById(R.id.password)).setText(params.get("password"));
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isConnected(){
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    public void register(View view) throws ExecutionException, InterruptedException, JSONException {

        Account account = null;
        boolean next = true;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", ((EditText)findViewById(R.id.username)).getText().toString());
        params.put("password", ((EditText)findViewById(R.id.password)).getText().toString());
        params.put("firstname", ((EditText)findViewById(R.id.firstname)).getText().toString());
        params.put("lastname", ((EditText)findViewById(R.id.lastname)).getText().toString());
        params.put("email", ((EditText)findViewById(R.id.email)).getText().toString());

        String[] newUser = new String[]{
            (((EditText)findViewById(R.id.username)).getText().toString()),
            (((EditText)findViewById(R.id.password)).getText().toString()),
            (((EditText)findViewById(R.id.firstname)).getText().toString()),
            (((EditText)findViewById(R.id.lastname)).getText().toString()),
            (((EditText)findViewById(R.id.email)).getText().toString())
        };
        for(Map.Entry<String, String> editText : params.entrySet()){
            if (editText.getValue().equals("") || editText.getValue() == null){
                next = false;
                break;
            }
        }

        if (isConnected()){
            if (next){
                if(!datasource.getAccountByUsername(newUser[0]).isEmpty()){
                    Toast.makeText(this, "Username already exist", Toast.LENGTH_LONG).show();
                    return;
                };
                JSONArray lists = new APIRequest().make(new String[]{
                        "register",
                        (((EditText)findViewById(R.id.username)).getText().toString()),
                        (((EditText)findViewById(R.id.password)).getText().toString()),
                        (((EditText)findViewById(R.id.firstname)).getText().toString()),
                        (((EditText)findViewById(R.id.lastname)).getText().toString()),
                        (((EditText)findViewById(R.id.email)).getText().toString())
                });
                JSONObject newUserAPI = lists.getJSONObject(0);
                if (newUserAPI.has("id")){
                    account = datasource.addUser(new String[]{
                            newUserAPI.getString("id"),
                            newUserAPI.getString("username"),
                            newUserAPI.getString("password"),
                            newUserAPI.getString("firstname"),
                            newUserAPI.getString("lastname"),
                            newUserAPI.getString("email"),
                    });
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("account", params);
                    startActivity(intent);
                }
            }
            else{
                Toast.makeText(this, "Check informations", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show();
        }
    }
}
