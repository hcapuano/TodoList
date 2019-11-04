package com.example.suptodolist;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo_list.db";
    private static final int DATABASE_VERSION = 1;

    public static final String ACCOUNT_TABLE = "account";
    public static final String ACCOUNT_KEY = "id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String ACCOUNT_USERNAME = "username";
    public static final String ACCOUNT_PASSWORD = "password";
    public static final String ACCOUNT_FIRSTNAME = "firstname";
    public static final String ACCOUNT_LASTNAME = "lastname";
    public static final String ACCOUNT_EMAIL = "email";

    public static final String TODO_LIST_TABLE = "todo_list";
    public static final String TODO_LIST_KEY = "id";
    public static final String TODO_LIST_ID = "list_id";
    public static final String TODO_LIST_LAST_UPDATE = "last_update";
    public static final String TODO_LIST_USER_CREATOR = "user_creator";
    public static final String TODO_LIST_USER_INVITED = "user_invited";
    public static final String TODO_LIST_TODO = "todo";
    public static final String TODO_LIST_ACCOUNT_ID = "account_id";

    public static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE " + ACCOUNT_TABLE + " (" +
                    ACCOUNT_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ACCOUNT_ID + " TEXT, " +
                    ACCOUNT_USERNAME + " TEXT, " +
                    ACCOUNT_PASSWORD + " TEXT, " +
                    ACCOUNT_FIRSTNAME + " TEXT, " +
                    ACCOUNT_LASTNAME + " TEXT, " +
                    ACCOUNT_EMAIL + " TEXT" +");";

    public static final String TODO_LIST_TABLE_CREATE =
            "CREATE TABLE " + TODO_LIST_TABLE + " (" +
                    TODO_LIST_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TODO_LIST_ID + " TEXT, " +
                    TODO_LIST_LAST_UPDATE + " TEXT, " +
                    TODO_LIST_USER_CREATOR + " TEXT, " +
                    TODO_LIST_USER_INVITED + " TEXT, " +
                    TODO_LIST_TODO + " TEXT," +
                    TODO_LIST_ACCOUNT_ID + " TEXT" +");";

    public static final String ACCOUNT_TABLE_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE + ";";
    public static final String TODO_LIST_TABLE_DROP = "DROP TABLE IF EXISTS " + TODO_LIST_TABLE + ";";

    public DatabaseHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(ACCOUNT_TABLE_CREATE);
        dataBase.execSQL(TODO_LIST_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        dataBase.execSQL(ACCOUNT_TABLE_DROP);
        dataBase.execSQL(TODO_LIST_TABLE_DROP);
        onCreate(dataBase);
    }
}
