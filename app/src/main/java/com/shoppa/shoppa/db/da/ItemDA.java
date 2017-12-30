package com.shoppa.shoppa.db.da;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shoppa.shoppa.db.DatabaseHelper;
import com.shoppa.shoppa.db.entity.Item;

public class ItemDA {

    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;
    private String[] mAllColumns = {
            DatabaseHelper.COLUMN_ITEM_ID,
            DatabaseHelper.COLUMN_ITEM_NAME,
            DatabaseHelper.COLUMN_ITEM_DESC,
            DatabaseHelper.COLUMN_ITEM_PRICE,
            DatabaseHelper.COLUMN_STORE_ID
    };

    public ItemDA(Context mContext) {

        mDatabaseHelper = new DatabaseHelper(mContext);
        //Open database
        try {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }

    public Item insertItem(String Id, String name, String desc, double price, String storeId) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ITEM_ID, Id);
        values.put(DatabaseHelper.COLUMN_ITEM_NAME, name);
        values.put(DatabaseHelper.COLUMN_ITEM_DESC, desc);
        values.put(DatabaseHelper.COLUMN_ITEM_PRICE, price);
        values.put(DatabaseHelper.COLUMN_STORE_ID, storeId);

        mDatabase.insert(DatabaseHelper.TABLE_ITEM, null, values);

        return getItem();
    }

    public Item getItem() {

        Item item = null;
        Cursor cursor = mDatabase.query(
                DatabaseHelper.TABLE_ITEM,              // The table to query
                mAllColumns,                            // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {

            item = new Item();
            item.setId(cursor.getString(0));
            item.setName(cursor.getString(1));
            item.setDescription(cursor.getString(2));
            item.setPrice(cursor.getDouble(3));
            item.setStoreId(cursor.getString(4));
        }
        assert cursor != null;
        cursor.close();

        return item;
    }

    public void close() {
        mDatabaseHelper.close();
    }
}
