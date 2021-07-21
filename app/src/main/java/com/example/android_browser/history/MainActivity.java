package com.example.history;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test测试";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //清空测试数据
        delete_all();
        //添加测试数据，逆序查询输出
        add_DB("百度一下", "www.baidu.com", "2021-07-17");
        add_DB("github", "www.github.com", "2021-07-18");
        add_DB("哔哩哔哩", "www.bilibili.com", "2021-07-18");
        add_DB("测试网站1", "www.baidu.com", "2021-07-19");
        add_DB("测试网站2", "www.baidu.com", "2021-07-20");
        add_DB("测试网站3", "www.baidu.com", "2021-07-20");
        add_DB("测试网站4", "www.baidu.com", getDate());
        add_DB("测试网站5", "www.baidu.com", getDate());

    }

    public String getDate() {
        //返回String类型的日期
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(date);
    }

    //按钮跳转至历史记录页面
    public void goto_history(View view) {
        Intent intent = new Intent(this, history.class);
        startActivity(intent);
    }

    //将网站信息加入数据库
    public void add_DB(String title, String url, String date) {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
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

    //先清空数据库
    public void delete_all() {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
        SQLiteDatabase history_db = helper.getWritableDatabase();
        Cursor cursor = history_db.rawQuery("select * from historyDB", null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            history_db.delete("historyDB", "_id=?", new String[]{String.valueOf(_id)});
        }
        cursor.close();
        history_db.close();
    }



}