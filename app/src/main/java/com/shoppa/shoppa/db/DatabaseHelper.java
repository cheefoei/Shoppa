package com.shoppa.shoppa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Column of user table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_FNAME = "user_fname";
    public static final String COLUMN_USER_LNAME = "user_lname";
    public static final String COLUMN_USER_GENDER = "user_gender";
    public static final String COLUMN_USER_EMAIL = "user_email";
    private static final String SQL_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_USER_FNAME + " TEXT NOT NULL, "
            + COLUMN_USER_LNAME + " TEXT NOT NULL, "
            + COLUMN_USER_EMAIL + " TEXT NOT NULL, "
            + COLUMN_USER_GENDER + " TEXT NOT NULL "
            + ");";

    public static final String DATABASE_NAME = "shoppa.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop the tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //Recreate the tables
        onCreate(db);
    }
}
