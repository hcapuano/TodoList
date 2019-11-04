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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.suptodolist.R.drawable.icon_add;

public class SelectedListActivity extends AppCompatActivity {

    Account connectedUser;
    ListObject currentTodoList;
    ArrayList<String> list = new ArrayList<String>();
    ListManagment listAdapter;

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
        this.currentTodoList = (ListObject)intent.getSerializableExtra("list");

        //If no element in list, don't display an empty line
        if (!this.currentTodoList.getTodo().equals("")){
            String[] todo = this.currentTodoList.getTodo().split(",");
            this.list.addAll(Arrays.asList(todo));
        }

        this.listAdapter = new ListManagment(this.list, this);
        ListView todoListView = (ListView)findViewById(R.id.mobile_list);
        todoListView.setAdapter(this.listAdapter);

        final TextView title = (TextView)findViewById(R.id.titleTodo);
        final EditText addList = (EditText)findViewById(R.id.addEdit);
        final Button addBtn = (Button)findViewById(R.id.addButton);

        //To have only one .xml for listView and get the good placeholder, icon
        addBtn.setCompoundDrawablesWithIntrinsicBounds(icon_add, 0, 0, 0);
        addBtn.setCompoundDrawablePadding(16);
        addList.setHint("New Element");
        title.setText("Selected List");

        //Handle each delete button in the ListManagment
        // (J'ai préféré supprimer un element ou plusieurs comme ça plutôt que d'ajouter une ligne
        // connected user à mettre à jour, de le récupérer et d'enfin update)
        final Handler updateTimer = new Handler();
        updateTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected() && listAdapter.itemToDelete > -1){
                    String newTodo = !String.join(",", list).isEmpty() ? String.join(",", list) : "";
                    currentTodoList.setTodo(newTodo);
                    try {
                        if (updatelist()){
                            listAdapter.itemToDelete = -1;
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateTimer.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public boolean updatelist() throws ExecutionException, InterruptedException, JSONException {
        if (isConnected()) {
            JSONArray lists = new APIRequest().make(new String[]{
                    "update",
                    this.connectedUser.getUsername(),
                    this.connectedUser.getPassword(),
                    this.currentTodoList.getListID(),
                    this.currentTodoList.getTodo()
            });
            return lists.getJSONObject(0).getString("success").equals("true");
        }
        return false;
    }

    public void displayUpdate() throws JSONException, ExecutionException, InterruptedException {
        if (isConnected()){
            JSONArray lists = new APIRequest().make(new String[]{
                    "read",
                    this.connectedUser.getUsername(),
                    this.connectedUser.getPassword(),
                    this.currentTodoList.getListID()
            });
            //if response return user
            if (lists.getJSONObject(0).has("id")) {
                this.currentTodoList = jsonToListObject(lists.getJSONObject(0));
                if (!this.currentTodoList.getTodo().equals("")){
                    String[] todo = this.currentTodoList.getTodo().split(",");
                    list.clear();
                    list.addAll(Arrays.asList(todo));
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected void hideKeyboard(View view){
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isConnected(){
        NetworkInfo network = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    public void logout(View view) throws JSONException, ExecutionException, InterruptedException {
        if (isConnected()){
            JSONArray lists = new APIRequest().make(new String[]{
                    "logout",
                    this.connectedUser.getUsername(),
                    this.connectedUser.getPassword()
            });
            if (lists.getJSONObject(0).getString("success").equals("true")){
                finish();
                Intent intent = new Intent(SelectedListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        else{
            finish();
            Intent intent = new Intent(SelectedListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem back) {
        switch (back.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("account", this.connectedUser);
        intent.putExtra("list", this.currentTodoList);
        startActivity(intent);
        super.onBackPressed();
    }

    public void createAction(View view) throws InterruptedException, ExecutionException, JSONException {
        EditText addElement =  (EditText)findViewById(R.id.addEdit);
        if (isConnected())
        {
            if(!addElement.getText().toString().isEmpty())
            {
                String newElement = this.currentTodoList.getTodo().equals("") ? addElement.getText().toString() : "," + addElement.getText().toString();
                this.currentTodoList.setTodo(this.currentTodoList.getTodo() + newElement);
                if(updatelist()){
                    displayUpdate();
                    addElement.getText().clear();
                    hideKeyboard(view);
                }
            }
            else
            {
                Toast.makeText(this, "Empty element", Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(this, "Impossible to add element - No Connection", Toast.LENGTH_LONG).show();
        }

        //TODO UPDATE BDD
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
