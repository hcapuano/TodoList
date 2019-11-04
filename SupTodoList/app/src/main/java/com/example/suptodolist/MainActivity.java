package com.example.suptodolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private AccountDAO dataAccount;
    public Account account;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainLogin);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        //Db Account creation
        this.dataAccount = new AccountDAO(this);
        this.dataAccount.open();

        //Get all account from db
        ArrayList<Account> allAccounts = dataAccount.getAllAccounts();

        //Return to Register if no account registered, or get the first account (AS REQUESTED IN THE SUBJECT)
        if (allAccounts.size() == 0){
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }
        //Put in the field the first account authenticate from db
        else {
            account = allAccounts.get(0);
            username.setText(account.getUsername());
            password.setText(account.getPassword());
        }

        //get the username and password from register if user have been added
        Intent intent = getIntent();
        HashMap<String, String> params = (HashMap<String, String>) intent.getSerializableExtra("account");

        if (params != null){
            if (params.get("username") != null) username.setText(params.get("username"));
            if (params.get("password") != null) password.setText(params.get("password"));
            try {
                login(layout);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isConnected(){
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    public void login(View view) throws ExecutionException, InterruptedException, JSONException {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){ //if field not empty
            if (isConnected()){

                //get jsonArray response login
                JSONArray lists = new APIRequest().make(new String[]{
                        "login",
                        username.getText().toString(),
                        password.getText().toString()
                });

                //if response return user
                if (lists.getJSONObject(0).has("id")){

                    JSONObject accountObject = lists.getJSONObject(0);
                    //Get the account if already exist in db
                    this.account = this.dataAccount.getAccount(accountObject.getString("username"), accountObject.getString("password"));

                    if (this.account == null) {
                        //Creation of the new account logged
                        this.account = this.dataAccount.addUser(new String[]{
                                accountObject.getString("id"),
                                accountObject.getString("username"),
                                accountObject.getString("password"),
                                accountObject.getString("firstname"),
                                accountObject.getString("lastname"),
                                accountObject.getString("email")
                        });
                    }

                    finish();
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.putExtra("account", this.account);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Wrong username or password", Toast.LENGTH_LONG).show();
                }
            }else {
                this.account = dataAccount.getAccount(username.getText().toString(), password.getText().toString());
                if (this.account != null){
                    finish();
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.putExtra("account", this.account);
                    startActivity(intent);
                }
            }
        }
    }

    public void goToRegister(View view) {

        if (isConnected()){
            HashMap<String, String> params = new HashMap<String, String>();
            EditText username = findViewById(R.id.username);
            EditText password = findViewById(R.id.password);

            if (!username.getText().toString().equals("")){
                params.put("username", username.getText().toString());
            }
            if (!password.getText().toString().equals("")){
                params.put("password", password.getText().toString());
            }

            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("params", params);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "No Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        dataAccount.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataAccount.close();
        super.onPause();
    }
}
