package com.example.android_browser.homepage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android_browser.R;
import com.example.android_browser.webview.WebViewActivity;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class Homepage extends AppCompatActivity implements View.OnClickListener{

    private Button search;
    private EditText url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        url = (EditText) findViewById(R.id.url);
        search = (Button) findViewById(R.id.search0);
        search.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search0 :
                Intent intent = new Intent(Homepage.this, WebViewActivity.class);
                intent.putExtra("url0",url.getText().toString());
                startActivity(intent);
        }
    }
}