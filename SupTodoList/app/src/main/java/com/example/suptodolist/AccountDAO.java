package com.example.suptodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountDAO {

    private SQLiteDatabase database;
    private DatabaseHandler dbHelper;

    private String[] accountColumns = {
            DatabaseHandler.ACCOUNT_ID,
            DatabaseHandler.ACCOUNT_USERNAME,
            DatabaseHandler.ACCOUNT_PASSWORD,
            DatabaseHandler.ACCOUNT_FIRSTNAME,
            DatabaseHandler.ACCOUNT_LASTNAME,
            DatabaseHandler.ACCOUNT_EMAIL
    };

    public AccountDAO(Context context) {
        dbHelper = new DatabaseHandler(context, null);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Account addUser(String[] newAccount){

        ContentValues values = new ContentValues();
        int index = 0;
        for (String columnHandler: accountColumns) {
            values.put(columnHandler, newAccount[index]);
            index++;
        }
        long insertId = database.insert(DatabaseHandler.ACCOUNT_TABLE, null, values);
        Cursor cursor = database.query(DatabaseHandler.ACCOUNT_TABLE,
                accountColumns, DatabaseHandler.ACCOUNT_KEY + " = " + insertId,
                null,null, null, null);
        cursor.moveToFirst();
        Account newUser = cursorToAccount(cursor);
        cursor.close();

        return newUser;
    }

    public Account getAccount(String username, String password){

        ArrayList<Account> accounts = new ArrayList<Account>();
        Cursor cursor = database.query(DatabaseHandler.ACCOUNT_TABLE, accountColumns, "username=? and password=?", new String[] { username, password }, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Account newUser = cursorToAccount(cursor);
            accounts.add(newUser);
            cursor.moveToNext();
        }
        cursor.close();

        if (!accounts.isEmpty()){
            return accounts.get(0);
        }
        return null;
    }

    public ArrayList<Account> getAccountByUsername(String username){

        ArrayList<Account> accounts = new ArrayList<Account>();
        Cursor cursor = database.query(DatabaseHandler.ACCOUNT_TABLE, accountColumns, "username=?", new String[] { username}, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Account newUser = cursorToAccount(cursor);
            accounts.add(newUser);
            cursor.moveToNext();
        }
        cursor.close();
        return accounts;
    }

    public ArrayList<Account> getAllAccounts(){
        ArrayList<Account> accounts = new ArrayList<Account>();
        Cursor cursor = database.query(DatabaseHandler.ACCOUNT_TABLE, accountColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Account newUser = cursorToAccount(cursor);
            accounts.add(newUser);
            cursor.moveToNext();
        }
        cursor.close();

        return accounts;
    }

    private Account cursorToAccount(Cursor cursor) {
        return new Account(cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
        );
    }
}
