package com.shoppa.shoppa.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Column of store table
    public static final String TABLE_STORE = "store";
    public static final String COLUMN_STORE_ID = "store_id";
    public static final String COLUMN_STORE_NAME = "store_name";
    public static final String COLUMN_STORE_LNG = "store_lng";
    public static final String COLUMN_STORE_LAT = "store_lat";
    private static final String SQL_CREATE_TABLE_STORE = "CREATE TABLE " + TABLE_STORE + "("
            + COLUMN_STORE_ID + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_STORE_NAME + " TEXT NOT NULL, "
            + COLUMN_STORE_LNG + " REAL NOT NULL, "
            + COLUMN_STORE_LAT + " REAL NOT NULL "
            + ");";

    //Column of item table
    public static final String TABLE_ITEM = "item";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_DESC = "item_desc";
    public static final String COLUMN_ITEM_PRICE = "item_price";
    private static final String SQL_CREATE_TABLE_ITEM = "CREATE TABLE " + TABLE_ITEM + "("
            + COLUMN_ITEM_ID + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_ITEM_NAME + " TEXT NOT NULL, "
            + COLUMN_ITEM_DESC + " TEXT NOT NULL, "
            + COLUMN_ITEM_PRICE + " REAL NOT NULL, "
            + COLUMN_STORE_ID + " TEXT NOT NULL"
            + ");";

    //Column of user table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_GENDER = "user_gender";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_PROFILE = "user_profile";
    public static final String COLUMN_USER_POCKET_MONEY = "user_pocket_money";
    private static final String SQL_CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_USER_NAME + " TEXT NOT NULL, "
            + COLUMN_USER_GENDER + " TEXT, "
            + COLUMN_USER_EMAIL + " TEXT NOT NULL, "
            + COLUMN_USER_PROFILE + " TEXT, "
            + COLUMN_USER_POCKET_MONEY + " REAL NOT NULL"
            + ");";

    //Column of cart table
    public static final String TABLE_CART = "cart";
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_CART_TOTAL = "cart_total";
    private static final String SQL_CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART + "("
            + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CART_TOTAL + " REAL NOT NULL "
            + ");";

    //Column of cart detail table
    public static final String TABLE_CART_DETAIL = "cart_detail";
    public static final String COLUMN_CART_DETAIL_ID = "cart_detail_id";
    public static final String COLUMN_CART_DETAIL_QUANTITY = "cart_detail_qty";
    private static final String SQL_CREATE_TABLE_CART_DETAIL = "CREATE TABLE " + TABLE_CART_DETAIL + "("
            + COLUMN_CART_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CART_DETAIL_QUANTITY + " INTEGER NOT NULL, "
            + COLUMN_ITEM_ID + " TEXT NOT NULL "
            + ");";

    private static final String DATABASE_NAME = "shoppa.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STORE);
        db.execSQL(SQL_CREATE_TABLE_ITEM);
        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_CART);
        db.execSQL(SQL_CREATE_TABLE_CART_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop the tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_DETAIL);
        //Recreate the tables
        onCreate(db);
    }
}
