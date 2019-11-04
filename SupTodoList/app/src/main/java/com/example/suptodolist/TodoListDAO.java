package com.example.suptodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TodoListDAO {

    private SQLiteDatabase database;
    private DatabaseHandler dbHelper;

    private String[] todoListColumns = {
            DatabaseHandler.TODO_LIST_ACCOUNT_ID,
            DatabaseHandler.TODO_LIST_LAST_UPDATE,
            DatabaseHandler.TODO_LIST_USER_CREATOR,
            DatabaseHandler.TODO_LIST_USER_INVITED,
            DatabaseHandler.TODO_LIST_TODO,
            DatabaseHandler.TODO_LIST_ID
    };

    public TodoListDAO(Context context) {
        dbHelper = new DatabaseHandler(context, null);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addList(ListObject listToAdd, String idAccount){
        ContentValues values = new ContentValues();
        int index = 0;
        String[] list = new String[6];
        list[0] =  idAccount;
        list[1] =  listToAdd.getLastUpdate();
        list[2] =  listToAdd.getUserCreator();
        list[3] =  listToAdd.getUserInvited();
        list[4] =  listToAdd.getTodo();
        list[5] = listToAdd.getListID();
        for (String columnHandler: todoListColumns) {
            values.put(columnHandler, list[index]);
            index++;
        }
        long insertId = database.insert(DatabaseHandler.TODO_LIST_TABLE, null, values);
        Cursor cursor = database.query(DatabaseHandler.TODO_LIST_TABLE,
                todoListColumns, DatabaseHandler.TODO_LIST_KEY + " = " + insertId,
                null,null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public TodoList getAllListsByAccount(String idAccount){
        ArrayList<ListObject> todolists = new ArrayList<ListObject>();
        Cursor cursor = database.query(DatabaseHandler.TODO_LIST_TABLE, todoListColumns, todoListColumns[0]+"=? ", new String[] { idAccount}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ListObject list = cursorToList(cursor);
            todolists.add(list);
            cursor.moveToNext();
        }
        cursor.close();

        return new TodoList(todolists);
    }

    public ArrayList<ListObject> getAllListsByAccountAsArray(String idAccount){
        ArrayList<ListObject> todolists = new ArrayList<ListObject>();
        Cursor cursor = database.query(DatabaseHandler.TODO_LIST_TABLE, todoListColumns, "account_id = ? ", new String[] { idAccount }, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ListObject list = cursorToList(cursor);
            todolists.add(list);
            cursor.moveToNext();
        }
        cursor.close();

        return todolists;
    }

    public ArrayList<ListObject> getListsById(String idList){
        ArrayList<ListObject> todolists = new ArrayList<ListObject>();
        Cursor cursor = database.query(DatabaseHandler.TODO_LIST_TABLE, todoListColumns, todoListColumns[5]+"=? ", new String[] { idList }, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ListObject list = cursorToList(cursor);
            todolists.add(list);
            cursor.moveToNext();
        }
        cursor.close();

        return todolists;
    }

    private ListObject cursorToList(Cursor cursor) {
        return new ListObject(cursor.getString(5),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );
    }

}
