package com.shoppa.shoppa.db.da;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shoppa.shoppa.db.DatabaseHelper;
import com.shoppa.shoppa.db.entity.User;

public class UserDA {

    private SQLiteDatabase mDatabase;
    private DatabaseHelper mDatabaseHelper;
    private String[] mAllColumns = {
            DatabaseHelper.COLUMN_USER_ID,
            DatabaseHelper.COLUMN_USER_NAME,
            DatabaseHelper.COLUMN_USER_GENDER,
            DatabaseHelper.COLUMN_USER_EMAIL,
            DatabaseHelper.COLUMN_USER_PROFILE,
            DatabaseHelper.COLUMN_USER_POCKET_MONEY
    };

    public UserDA(Context mContext) {

        mDatabaseHelper = new DatabaseHelper(mContext);
        //Open database
        try {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }

    public User insertUser(String Id,
                           String name,
                           @Nullable String gender,
                           String email,
                           @Nullable String profile,
                           double pocketMoney) {

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_ID, Id);
        values.put(DatabaseHelper.COLUMN_USER_NAME, name);
        values.put(DatabaseHelper.COLUMN_USER_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_USER_PROFILE, profile);
        values.put(DatabaseHelper.COLUMN_USER_POCKET_MONEY, pocketMoney);

        mDatabase.insert(DatabaseHelper.TABLE_USER, null, values);

        return getUser();
    }

    public User getUser() {

        User user = null;
        Cursor cursor = mDatabase.query(
                DatabaseHelper.TABLE_USER,              // The table to query
                mAllColumns,                            // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {

            user = new User();
            user.setId(cursor.getString(0));
            user.setName(cursor.getString(1));
            user.setGender(cursor.getString(2));
            user.setEmail(cursor.getString(3));
            user.setProfile(cursor.getString(4));
            user.setPocketMoney(cursor.getDouble(5));
        }
        assert cursor != null;
        cursor.close();

        return user;
    }

    public void close() {
        mDatabaseHelper.close();
    }
}
