package com.example.suptodolist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ListActivity extends AppCompatActivity {

    Account connectedUser;
    TodoList todoLists;
    ArrayList<String> list = new ArrayList<String>();
    ListManagment listAdapter;
    private TodoListDAO dataTodo;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //To close keyboard
        RelativeLayout layout = findViewById(R.id.todoLists);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });

        Intent intent = getIntent();
        this.connectedUser = (Account)intent.getSerializableExtra("account");

        //Get all account from db
        this.dataTodo = new TodoListDAO(this);
        this.dataTodo.open();

        if (isConnected()){
            try {
                getTodoLists();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            this.todoLists = dataTodo.getAllListsByAccount(this.connectedUser.getId());
            addToListAdapter();
        }
        //TODO else get db lists

        this.listAdapter = new ListManagment(list, this);
        ListView todoListView = (ListView)findViewById(R.id.mobile_list);
        todoListView.setAdapter(listAdapter);

        final EditText addList = (EditText) findViewById(R.id.addEdit);
        final Button addBtn = (Button) findViewById(R.id.addButton);

        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(view.getContext(), SelectedListActivity.class);
                intent.putExtra("list", todoLists.getTodoList().get(position));
                intent.putExtra("account", connectedUser);
                startActivity(intent);
            }
        });

        //Update list every 30 seconds
        final Handler updateTimer = new Handler();
        updateTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected()){
                    try {
                        getTodoLists();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                updateTimer.postDelayed(this, 30000);
            }
        }, 30000);
    }

    //TODO EVERY 30 seconds, update list

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected()) {
            try {
                this.list = new ArrayList<String>();
                getTodoLists();
                this.listAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void hideKeyboard(View view){
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isConnected(){
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    public void logout(View view) throws ExecutionException, InterruptedException, JSONException {

        if (isConnected()){
            JSONArray lists = new APIRequest().make(new String[]{
                    "logout",
                    this.connectedUser.getUsername(),
                    this.connectedUser.getPassword()
            });
            if (lists.getJSONObject(0).getString("success").equals("true")){
                finish();
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        else{
            finish();
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    public void getTodoLists() throws JSONException, ExecutionException, InterruptedException {

        //TODO update in DB or add
        if (isConnected()){
            JSONArray lists = new APIRequest().make(new String[]{
                    "list",
                    this.connectedUser.getUsername(),
                    this.connectedUser.getPassword()
            });

            ArrayList<ListObject> todosLists = new ArrayList<ListObject>();
            //if response return user
            if (lists.getJSONObject(0).has("id")) {
                for (int index = 0; index < lists.length(); index++){
                    todosLists.add(jsonToListObject(lists.getJSONObject(index)));
                }
                this.todoLists = new TodoList(todosLists);

                ArrayList<ListObject> dbTodoLists = dataTodo.getAllListsByAccountAsArray(this.connectedUser.getId());
                for (ListObject currentList:todosLists) {
                    ArrayList<ListObject> dbList = dataTodo.getListsById(currentList.getListID());
                    if (dbList.size() == 0){
                        dataTodo.addList(currentList, this.connectedUser.getId());
                    }
                }
            }

            list.clear();
            addToListAdapter();
        }
    }

    public void addToListAdapter(){
        for (ListObject currentList : todoLists.getTodoList()) {
            String newElementList;
            String state = !currentList.getUserInvited().equals("null") ? "Public -" : "Private -";
            String userInvited = currentList.getUserInvited().equals("null") ? "" : " "+currentList.getUserInvited();
            newElementList = state + userInvited + "  (" + currentList.getLastUpdate().substring(0, 10) + ")  ";
            if (currentList.getTodo().length() > 2){
                String firstElement = currentList.getTodo().split(",")[0];
                newElementList+= firstElement.length() > 12 ? firstElement.substring(0, 9)+"..." : firstElement;
            }
            list.add(newElementList);
        }
    }

    public void createAction(View view) throws ExecutionException, InterruptedException, JSONException {

        EditText addList = (EditText) findViewById(R.id.addEdit);

        if (this.todoLists.getTodoList().size() < 50) {
            if (isConnected()) {
                JSONArray shareRequest = new APIRequest().make(new String[]{
                        "share",
                        this.connectedUser.getUsername(),
                        this.connectedUser.getPassword(),
                        addList.getText().toString()
                });

                if (shareRequest.getJSONObject(0).has("success") && shareRequest.getJSONObject(0).get("success").equals(true)) {
                    this.list = new ArrayList<String>();
                    getTodoLists();
                    addList.getText().clear();
                    listAdapter.notifyDataSetChanged();
                } else {
                    addList.getText().clear();
                    Toast.makeText(this, "This username doesn't exist", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "No connection at the moment", Toast.LENGTH_LONG).show();
            }
        }
    }

    public ListObject jsonToListObject(JSONObject jsonObject) throws JSONException {
        return new ListObject(
                jsonObject.get("id").toString(),
                jsonObject.get("lastupdate").toString(),
                jsonObject.get("usercreator").toString(),
                jsonObject.get("userinvited").toString(),
                jsonObject.get("todo").toString()
        );
    }

}
