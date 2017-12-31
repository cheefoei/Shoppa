package com.shoppa.shoppa.db.da;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shoppa.shoppa.db.DatabaseHelper;
import com.shoppa.shoppa.db.entity.Cart;

public class CartDA {

    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;
    private String[] mAllColumns = {
            DatabaseHelper.COLUMN_CART_ID,
            DatabaseHelper.COLUMN_CART_TOTAL
    };

    public CartDA(Context mContext) {

        mDatabaseHelper = new DatabaseHelper(mContext);
        //Open database
        try {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }

    public long insertCart(double total) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CART_TOTAL, total);

        return mDatabase.insert(DatabaseHelper.TABLE_CART, null, values);
    }

    public Cart getCart() {

        Cart cart = null;
        Cursor cursor = mDatabase.query(
                DatabaseHelper.TABLE_CART,              // The table to query
                mAllColumns,                            // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {

            cart = new Cart(cursor.getDouble(1));
        }
        assert cursor != null;
        cursor.close();

        return cart;
    }

    public void removeAll() {
        mDatabase.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART);
    }

    public void close() {
        mDatabaseHelper.close();
    }
}
