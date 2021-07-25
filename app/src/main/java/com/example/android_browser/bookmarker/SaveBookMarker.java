package com.example.android_browser.bookmarker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android_browser.history.MyDatabaseHelper;

public class SaveBookMarker {
    private static final SaveBookMarker INSTANCE = new SaveBookMarker();

    private SaveBookMarker(){}

    public static final SaveBookMarker getInstance(){

        return SaveBookMarker.INSTANCE;
    }
    public void add_DB(Context context, String title, String url) {
        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(context);
        SQLiteDatabase marker_db = helper.getWritableDatabase();
        //手动输入数据
        if (marker_db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("WebName", title);
            values.put("WebUrl", url);
            marker_db.insert("FAVOURITES", null, values);
        }
        marker_db.close();
    }

}
