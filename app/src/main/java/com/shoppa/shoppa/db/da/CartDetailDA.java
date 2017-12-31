package com.shoppa.shoppa.db.da;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shoppa.shoppa.db.DatabaseHelper;
import com.shoppa.shoppa.db.entity.CartDetail;

import java.util.ArrayList;
import java.util.List;

public class CartDetailDA {

    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;
    private String[] mAllColumns = {
            DatabaseHelper.COLUMN_CART_DETAIL_ID,
            DatabaseHelper.COLUMN_CART_DETAIL_QUANTITY,
            DatabaseHelper.COLUMN_ITEM_ID,
    };

    public CartDetailDA(Context mContext) {

        mDatabaseHelper = new DatabaseHelper(mContext);
        //Open database
        try {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }

    public long insertCartDetail(int quantity, String itemId) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CART_DETAIL_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_ITEM_ID, itemId);

        return mDatabase.insert(DatabaseHelper.TABLE_CART_DETAIL, null, values);
    }

    public List<CartDetail> getCartDetail() {

        List<CartDetail> cartDetailList = new ArrayList<>();
        Cursor cursor = mDatabase.query(
                DatabaseHelper.TABLE_CART_DETAIL,           // The table to query
                mAllColumns,                                // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                CartDetail cartDetail = new CartDetail(
                        cursor.getInt(1),
                        cursor.getString(2)
                );
                cartDetailList.add(cartDetail);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return cartDetailList;
    }

    public void removeAll() {
        mDatabase.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_DETAIL);
    }

    public void close() {
        mDatabaseHelper.close();
    }
}
