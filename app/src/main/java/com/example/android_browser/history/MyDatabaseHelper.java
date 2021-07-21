package com.example.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//建立自用数据库扩展sqlite
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static SQLiteOpenHelper mInstance;

    //对外接口
    public static synchronized SQLiteOpenHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyDatabaseHelper(context, "history.db", null, 1);
        }
        return mInstance;
    }

    //构造函数私有化：
    // 第一个参数:Context上下文，
    // 第二个参数:数据库名，
    // 第三个参数:cursor允许我们在查询数据的时候返回一个自定义的光标位置，一般传入的都是null，
    // 第四个参数:表示目前库的版本号（用于对库进行升级）
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //数据库初始化
    @Override
    public void onCreate(SQLiteDatabase db) {
        //调用SQLiteDatabase中的execSQL（）执行建表语句。
        String sql = "create table historyDB(_id integer primary key autoincrement, title text , url text, date string)";
        db.execSQL(sql);
        //创建成功
    }

    //数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists historyDB");
        onCreate(db);
    }

}