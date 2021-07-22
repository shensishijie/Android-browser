package com.example.android_browser.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SaveBookmarkService {
    private static final SaveBookmarkService INSTANCE = new SaveBookmarkService();

    private SaveBookmarkService() {}

    public static SaveBookmarkService getInstance(){
        return SaveBookmarkService.INSTANCE;
    }

    public void add_DB(Context context, String title, String url, String date) {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(context);
        SQLiteDatabase history_db = helper.getWritableDatabase();
        //手动输入数据
        if (history_db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("url", url);
            values.put("date", date);
            history_db.insert("historyDB", null, values);
        }
        history_db.close();
    }
}
