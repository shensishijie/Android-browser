package com.example.android_browser.bookmarker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
/*MySqliteOpenHelper 工具类 单例模式(1.构造函数私有化 2.对外提供函数)

 */

public class MySqliteOpenHelper extends SQLiteOpenHelper {
    //2.对外提供函数
    private static SQLiteOpenHelper mIntrance;
    public static synchronized SQLiteOpenHelper getmIntrance(Context context){
        if(mIntrance == null)
            mIntrance = new MySqliteOpenHelper(context, "BookMarkerDB.db", null,1);
        return mIntrance;
    }
    //1.构造函数私有化
    private MySqliteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//传给父函数
    }
    //数据库初始化 该函数只会在第一次执行时运行一次
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = " CREATE TABLE FAVOURITES(_id Integer PRIMARY KEY AUTOINCREMENT,WebName TEXT,WebUrl TEXT)\n" ;//自动增长autoincrement
        sqLiteDatabase.execSQL(sql);
    }
    //数据库升级用
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    //参数分别为(数据库，老版本号，新版本号)
    }
}
