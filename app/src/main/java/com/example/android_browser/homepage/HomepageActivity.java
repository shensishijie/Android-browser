package com.example.android_browser.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android_browser.R;
import com.example.android_browser.history.HistoryActivity;
import com.example.android_browser.webview.WebViewActivity;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String STATE_CURRENT_URL = "current_url";

    private ImageButton search;
    //输入框
    private EditText url;

    private static Boolean isQuit = false;

    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        url = (EditText) findViewById(R.id.url);
        search = findViewById(R.id.search0);
        search.setOnClickListener(this);

        // 检查是否在离开前保存过当前页面数据
        String currentUrl;
        if (savedInstanceState != null) {
            // 获取离开页面前的数据
            currentUrl = savedInstanceState.getString(STATE_CURRENT_URL);
            url.setText(currentUrl);
        }

        //监听键盘回车搜索
        url.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == KEYCODE_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(search);
                }
                return false;
            }
        });
    }

    //连点两下退出
    @Override
    public void onBackPressed() {
        if (!isQuit) {
            isQuit = true;
            Toast.makeText(getBaseContext(), "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
            TimerTask task = null;
            task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            timer.schedule(task, 2000);
        } else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击搜索打开WebViewActivity
            case R.id.search0 :
                Intent intent = new Intent(HomepageActivity.this, WebViewActivity.class);
                intent.putExtra("url0",url.getText().toString());
                startActivity(intent);
        }
    }

    /**
     * 保存当前页面的数据
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putString(STATE_CURRENT_URL, url.getText().toString());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * 打开历史记录页面
     * @param view
     */
    public void openHistory(View view) {
        Intent intent = new Intent(HomepageActivity.this, HistoryActivity.class);
        startActivity(intent);
    }
}