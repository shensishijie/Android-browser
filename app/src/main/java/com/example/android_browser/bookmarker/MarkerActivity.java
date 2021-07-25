package com.example.android_browser.bookmarker;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.example.android_browser.R;
import com.example.android_browser.webview.WebViewActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerActivity extends AppCompatActivity {

    public List<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
    public static String BOOKMARKER_URL_EXTRA= "com.example.android_browser.history.EXTRAS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //变量声明与定义
        FloatingActionButton fab = findViewById(R.id.fab);
        ListView MainList = findViewById(R.id.Markers);



        //列表点击事件以及长按事件
        MainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //点击时访问，在此处加入
                Map Checked= list.get(i);
                String Url = Checked.get("WebUrl").toString();
                Intent mIntent = new Intent(MarkerActivity.this, WebViewActivity.class);
                mIntent.putExtra(BOOKMARKER_URL_EXTRA,Url);
                startActivity(mIntent);
            }
        });
        //长按显示菜单
        MainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map Checked= list.get(i);
                String Url = Checked.get("WebUrl").toString();
                String Name = Checked.get("WebName").toString();
                showListDialog(i,Url);
                return false;
            }
        });

        //按钮点击事件监听器
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertView();
            }
        });
        query();

    }
    /*public void autosave(){
        Intent intent = getIntent();
        String SaName = intent.getStringExtra("WebName");
        String SaUrl = intent.getStringExtra("WebUrl");
        insert(SaName,SaUrl);
        query();
    }*/
    private void showListDialog(int CheckedItem,String Url){
        final String[] items = {"修改此书签","删除此书签","就是点一下而已"};
        final int num = CheckedItem+1;
        AlertDialog.Builder listDialog = new AlertDialog.Builder(MarkerActivity.this);
        //listDialog.setIcon(R.drawable.icon);//图标
        listDialog.setTitle("请选择对书签进行的操作");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0:{
                        //准备修改
                        Toast.makeText(MarkerActivity.this,"准备修改第"+num+"个书签·",Toast.LENGTH_SHORT).show();
                        ChangeView(Url);
                        break;
                    }
                    case 1: {
                        Toast.makeText(MarkerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        delete(Url);
                        break;
                    }
                    case 2:{
                        Toast.makeText(MarkerActivity.this,"=_=·",Toast.LENGTH_SHORT).show();
                    }
                }
                list.clear();
                query();
            }
        });
        listDialog.show();
    }

    //开始初始化数据库 需要在其他处引用该函数来启动
    public void createDB(){
        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(this);
        //
        // (helper.getWritableDatabase/helper.getReadableDatabase)databases文件夹的创建，靠下面这句话
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        readableDatabase.close();
    }
    //查询操作（默认全部查询）
    public void query(){
        list.clear();
        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        if(db.isOpen()){

            Cursor cursor = db.rawQuery("SELECT * FROM FAVOURITES",null);
            while (cursor.moveToNext()){//Curcor查询后指向第一条记录的前一条，需要向后移一位
                //int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String Webname = cursor.getString(cursor.getColumnIndex("WebName"));
                String WebUrl = cursor.getString(cursor.getColumnIndex("WebUrl"));

                HashMap<String,Object> map = new HashMap<String,Object>();
                map.put("WebName",Webname);
                map.put("WebUrl",WebUrl);
                list.add(map);

            }
            SimpleAdapter MySimpleAdapter = new SimpleAdapter(this,list,R.layout.itemlayout,new String[]{"WebName","WebUrl"},new int[]{R.id.textView1,R.id.textView2});
            ListView listView = (ListView)findViewById(R.id.Markers);
            listView.setAdapter(MySimpleAdapter);

            cursor.close();//规范一，关闭游标

        }
        else
        {
            Toast.makeText(MarkerActivity.this,"数据库启动异常，无法执行全部查询",Toast.LENGTH_SHORT).show();
        }
        db.close();//规范二，关闭数据库
    }

    //删除操作
    //Url：要删除项目的
    public void delete (String Url){
        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(this);
        //
        // (helper.getWritableDatabase/helper.getReadableDatabase)databases文件夹的创建，靠下面这句话
        SQLiteDatabase deleteDatabase = helper.getWritableDatabase();
        if(deleteDatabase.isOpen()){
            String Del_1 = "DELETE FROM FAVOURITES WHERE WebUrl = '"+Url+"'";
            deleteDatabase.execSQL(Del_1);
        }
        else
        {
            Toast.makeText(MarkerActivity.this,"数据库启动异常，无法执行删除",Toast.LENGTH_SHORT).show();
        }
        deleteDatabase.close();
    }
    public void ChangeView(String BeUrl){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.insertdialog,(ViewGroup)findViewById(R.id.dialog));

        Toast.makeText(MarkerActivity.this,"NAME:"+BeUrl,Toast.LENGTH_SHORT).show();

        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(this);
        SQLiteDatabase Chdb = helper.getWritableDatabase();

        EditText DiName = layout.findViewById(R.id.WebName);
        EditText DiUrl = layout.findViewById(R.id.WebUrl);

        builder.setTitle("请输入书签信息")
                .setView(layout)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //提交修改之后的数据
                        String WebName = DiName.getText().toString();
                        String WebUrl = DiUrl.getText().toString();

                        Toast.makeText(MarkerActivity.this,"NAME:"+WebName,Toast.LENGTH_SHORT).show();
                        Toast.makeText(MarkerActivity.this,"URL:"+WebUrl,Toast.LENGTH_SHORT).show();

                        update(WebName,WebUrl,BeUrl);
                    }
                }).show();

    }
    public void update(String WebName,String WebUrl,String BeUrl){
        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(this);
        SQLiteDatabase updateDatabase = helper.getWritableDatabase();

        String update = "UPDATE FAVOURITES SET WebName ='"+WebName+"',WebUrl ='"+WebUrl+"'WHERE WebUrl ='"+BeUrl+"'";

        if(updateDatabase.isOpen()){
            updateDatabase.execSQL(update);
            Toast.makeText(MarkerActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MarkerActivity.this,"数据库启动异常，无法执行更新",Toast.LENGTH_SHORT).show();
        }
        updateDatabase.close();
        query();
    }
    public void InsertView(){
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.insertdialog,(ViewGroup)findViewById(R.id.dialog));
        builder.setTitle("请输入书签信息")
                .setView(layout)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText DiName = layout.findViewById(R.id.WebName);
                        EditText DiUrl = layout.findViewById(R.id.WebUrl);

                        String WebName = DiName.getText().toString();
                        String WebUrl = DiUrl.getText().toString();


                        insert(WebName,WebUrl);
                    }
                }).show();
    }
    public void insert(String Title, String Url) {
        SQLiteOpenHelper helper = MySqliteOpenHelper.getmIntrance(this);
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();

        /*if(writableDatabase.isOpen()){
            Toast.makeText(MainActivity2.this, "已打开数据库", Toast.LENGTH_SHORT).show();
        }*/

        String Ins = "SELECT * FROM FAVOURITES";
        Cursor ChaFlag = writableDatabase.rawQuery(Ins, null);
        ChaFlag.moveToFirst();


        if(ChaFlag == null){
            String Ins_1 = "INSERT INTO FAVOURITES(WebName,WebUrl) VALUES ('"+Title+ "','"+Url+"')";
            writableDatabase.execSQL(Ins_1);
            Toast.makeText(MarkerActivity.this, "已添加新的标签", Toast.LENGTH_SHORT).show();
        }
        else{
            while (ChaFlag != null) {
                if (ChaFlag.isAfterLast()) {
                    //这里将插入新的项目
                    String Ins_1 = "INSERT INTO FAVOURITES(WebName,WebUrl) VALUES ('"+Title+ "','"+Url+"')";
                    writableDatabase.execSQL(Ins_1);
                    Toast.makeText(MarkerActivity.this, "已添加新的标签", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (ChaFlag.getString(ChaFlag.getColumnIndex("WebUrl")).equals(Url)) {
                    Toast.makeText(MarkerActivity.this, "已存在相同书签", Toast.LENGTH_SHORT).show();
                    break;
                }
                ChaFlag.moveToNext();
            }
        }



        ChaFlag.close();
        writableDatabase.close();
        query();
    }
}