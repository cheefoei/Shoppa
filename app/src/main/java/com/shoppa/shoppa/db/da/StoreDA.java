//package com.shoppa.shoppa.db.da;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import com.shoppa.shoppa.db.DatabaseHelper;
//import com.shoppa.shoppa.db.entity.Store;
//
//public class StoreDA {
//
//    private SQLiteDatabase mDatabase;
//    private DatabaseHelper mDatabaseHelper;
//    private String[] mAllColumns = {
//            DatabaseHelper.COLUMN_STORE_ID,
//            DatabaseHelper.COLUMN_STORE_NAME,
//            DatabaseHelper.COLUMN_STORE_LNG,
//            DatabaseHelper.COLUMN_STORE_LAT
//    };
//
//    public StoreDA(Context mContext) {
//
//        mDatabaseHelper = new DatabaseHelper(mContext);
//        //Open database
//        try {
//            mDatabase = mDatabaseHelper.getWritableDatabase();
//        } catch (SQLException e) {
//            Log.e(e.getClass().getName(), e.getMessage());
//        }
//    }
//
//    public Store insertStore(String Id, String name, double longitude, double latitude) {
//
//        ContentValues values = new ContentValues();
//        values.put(DatabaseHelper.COLUMN_STORE_ID, Id);
//        values.put(DatabaseHelper.COLUMN_STORE_NAME, name);
//        values.put(DatabaseHelper.COLUMN_STORE_LNG, longitude);
//        values.put(DatabaseHelper.COLUMN_STORE_LAT, latitude);
//
//        mDatabase.insert(DatabaseHelper.TABLE_STORE, null, values);
//
//        return getStore();
//    }
//
//    public Store getStore() {
//
//        Store store = null;
//        Cursor cursor = mDatabase.query(
//                DatabaseHelper.TABLE_STORE,             // The table to query
//                mAllColumns,                            // The columns to return
//                null,                                   // The columns for the WHERE clause
//                null,                                   // The values for the WHERE clause
//                null,                                   // don't group the rows
//                null,                                   // don't filter by row groups
//                null                                    // The sort order
//        );
//
//        if (cursor != null && cursor.moveToFirst()) {
//
//            store = new Store();
//            store.setId(cursor.getString(0));
//            store.setName(cursor.getString(1));
//            store.setLongitude(cursor.getDouble(2));
//            store.setLatitude(cursor.getDouble(3));
//        }
//        assert cursor != null;
//        cursor.close();
//
//        return store;
//    }
//
//    public void close() {
//        mDatabaseHelper.close();
//    }
//}
