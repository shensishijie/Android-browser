package com.example.history;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class history extends AppCompatActivity {

    public static boolean if_exist; //判断历史记录表是否有数据
    public ArrayList arrayList = new ArrayList(); //在遍历时记录下当前item的_id，用于匹配当前点击位置，返回url到主页打开


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ListView listview = findViewById(R.id.history_list);

        //数据库查询，显示历史记录
        query();

        //返回历史记录
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (if_exist) {
                    Intent mIntent = new Intent();
                    //告诉主页面此时返回一个url，在主页面中打开该网址
                    mIntent.putExtra("open_history", query_by_id(position));
                    setResult(RESULT_OK, mIntent);
                    finish();
                }
            }
        });



    }

    //数据库逆序查询，显示历史记录
    public void query() {
        //listItem数组存放listview数据，记录为map类型
        List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
        SQLiteDatabase history_db = helper.getReadableDatabase();

        if (history_db.isOpen()) {
            //设置游标
            //sql语句 order by _id desc实现逆向查询（按访问时间排序）
            Cursor cursor = history_db.rawQuery("select * from historyDB order by _id desc", null);
            if (cursor != null && cursor.getCount() > 0) {
                if_exist = true;
                //游标循环遍历
                while (cursor.moveToNext()) {
                    int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String url = cursor.getString(cursor.getColumnIndex("url"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("title", title);
                    map.put("url", url);
                    map.put("date", date);
                    arrayList.add(_id);
                    listItem.add(map);
                }
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item,
                        new String[]{"title", "url", "date"},
                        new int[]{R.id.Title, R.id.url, R.id.date});
                ListView listview = findViewById(R.id.history_list);
                listview.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
            else {
                if_exist = false;
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", "浏览记录为空");
                listItem.add(map);

                //new String  数据来源， new int 数据到哪去
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.list_item,
                        new String[]{"title"},
                        new int[]{R.id.Title});
                ListView listview = findViewById(R.id.history_list);
                listview.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }

            cursor.close();
            history_db.close();

        }
    }

    //返回url地址
    public String query_by_id(int position) {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
        SQLiteDatabase history_db = helper.getReadableDatabase();
        Cursor cursor = history_db.rawQuery("select * from historyDB", null);
        //记录返回id、title、url
        int _id;
        String url = null;
        while (cursor.moveToNext()) {
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
            url = cursor.getString(cursor.getColumnIndex("url"));
            //找到id相等的就返回url，否则返空
            if (arrayList.get(position).equals(_id))
                break;
        }
        //关闭游标和数据库，返回url
        cursor.close();
        history_db.close();
        return url;
    }

    //获取当前日期
    public String getDate() {
        //返回String类型的日期，格式为2021-01-01
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(date);
    }

    //获得距当天前i个小时的日期
    public String getDate_hour(int i) {
        //返回String类型的日期，格式为2021-01-01
        Date date = new Date();
        long Time = date.getTime();
        long newTime = Time - i * 3600 * 1000;
        Date dateNew = new Date(newTime);
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(dateNew);
    }


    //删除今天历史记录
    public void delete_today () {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
        SQLiteDatabase history_db = helper.getReadableDatabase();
        String date = getDate();
        history_db.delete("historyDB", "date=?", new String[]{date});
        history_db.close();

        //重新显示历史记录
        query();
    }

    //删除近三天内历史记录
    public void delete_three_day () {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
        SQLiteDatabase history_db = helper.getReadableDatabase();
        String today = getDate();
        String yesterday = getDate_hour(24);
        String the_day_bef_yesterday = getDate_hour(48);
        history_db.delete("historyDB", "date=? or date=? or date=?", new String[]{today, yesterday, the_day_bef_yesterday});
        history_db.close();

        //重新显示历史记录
        query();
    }


    //删除全部历史记录
    public void delete_all() {
        SQLiteOpenHelper helper = MyDatabaseHelper.getmInstance(this);
        SQLiteDatabase history_db = helper.getWritableDatabase();
        if (history_db.isOpen()) {
            //设置游标
            Cursor cursor = history_db.rawQuery("select * from historyDB", null);
            //遍历删除
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                    history_db.delete("historyDB", "_id=?", new String[]{String.valueOf(_id)});
                }
            }
            //关闭游标和数据库
            cursor.close();
            history_db.close();
        }

        //重新显示历史记录
        query();
    }


    //弹出清除历史记录对话框
    public void clear_dialog (View view) {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        view = View.inflate(this, R.layout.clear_dialog, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.clear_dialog_anim);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        //取消
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                dialog.dismiss();
            }
        });

        //删除今天历史记录
        view.findViewById(R.id.delete_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                delete_today();
                dialog.dismiss();
            }
        });

        //删除近三天内历史记录
        view.findViewById(R.id.delete_three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                delete_three_day();
                dialog.dismiss();
            }
        });

        //删除全部历史记录
        view.findViewById(R.id.delete_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                delete_all();
                dialog.dismiss();
            }
        });

    }


}