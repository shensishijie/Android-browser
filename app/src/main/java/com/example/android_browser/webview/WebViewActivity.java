package com.example.android_browser.webview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.android_browser.R;
import com.example.android_browser.imageopen.ImageOpenJSInterface;

import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_ENTER;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener, Observer<CurrentData> {

    private WebView webView;
    //搜索栏
    private EditText url;

    private ProgressBar progressBar;

    private ImageView collect;

    private ImageView search;

    private ImageView go_for;

    private ImageView go_next;

    private ImageView home;

    private ImageView refresh;

    private ImageView detail;

    private WebViewModel webViewModel;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webViewModel = new ViewModelProvider(this).get(WebViewModel.class);

        CurrentDataLiveData.getInstance().observe(this, this);

        // Data binding
        webView = findViewById(R.id.web_view);
        url = findViewById(R.id.url);
        progressBar = findViewById((R.id.progress_bar));
        collect = findViewById(R.id.collect);
        search = findViewById(R.id.search);
        go_for = findViewById(R.id.go_for);
        go_next = findViewById(R.id.go_next);
        home = findViewById(R.id.go_home);
        refresh = findViewById(R.id.refresh);
        detail = findViewById(R.id.detail);

        // Setting listener
        search.setOnClickListener(this);
        go_for.setOnClickListener(this);
        go_next.setOnClickListener(this);
        home.setOnClickListener(this);
        refresh.setOnClickListener(this);
        detail.setOnClickListener(this);
        collect.setOnClickListener(this);


        //设置webview部分属性
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new ImageOpenJSInterface(this), "imagelistener");
        webView.setWebViewClient(new WebViewClient());

        //设置 WebView 是否应该启用对“viewport”HTML 元标记的支持还是应该使用宽视口
        webView.getSettings().setUseWideViewPort(true);
        //设置 WebView 是否以概览模式加载页面，即按宽度缩小内容以适应屏幕
        webView.getSettings().setLoadWithOverviewMode(true);
        //设置解码 html 页面时使用的默认文本编码名称
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        // 将图片调整到适合 WebView 的大小
        webView.getSettings().setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webView.getSettings().setLoadWithOverviewMode(true);
        // 支持缩放，默认为true。是下面那个的前提。
        webView.getSettings().setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该 WebView 不可缩放
        webView.getSettings().setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        webView.getSettings().setDisplayZoomControls(false);
        // 缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        webView.getSettings().setLoadsImagesAutomatically(true);

        //设置前进或后退步数
        webView.goBackOrForward(1);

        //加载homepage中输入的网址或者搜索内容
        search(getIntent().getStringExtra("url0"));

        //设置进度条显示
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    url.setText(webView.getTitle());
                    url.setSelection(url.getText().toString().length());
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress); //刷新进度值
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //页面加载结束后存储当前网页信息
                putCurrentData(webView.getUrl(), webView.getTitle());
                //待网页加载完全后设置图片点击的监听方法
                addImageClickListener(view);
            }

            private void addImageClickListener(WebView webView) {
                webView.loadUrl("javascript:(function(){" +
                        "var objs = document.getElementsByTagName(\"img\"); " +
                        "var imageUrls = [];"+
                        "for(var i=0;i<objs.length;i++)  " +
                        "{" +
                        "    imageUrls[i] = objs[i].src;   " +
                        "    objs[i].onclick=function(e)  " +
                        "    {  " +
                        "        var oEvent = e || event;" +
                        "        oEvent.cancelBubble = true;   " +
                        "        oEvent.stopPropagation();" +
                        "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                        "    };  " +
                        "}" +
                        "window.imagelistener.loadImageUrl(imageUrls);  " +
                        "})()");
            }
        });


        //监听键盘回车搜索
        url.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KEYCODE_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onClick(search);
                }
                return false;
            }
        });
    }

    //搜索功能
    private void search(String url_name) {
        String url_name1;
        if ((!url_name.startsWith("https://")) && (!url_name.startsWith("http://"))) {
            url_name1 = "https://" + url_name;
        } else {
            url_name1 = url_name;
        }
        if (Patterns.WEB_URL.matcher(url_name1).matches()) {
            webView.loadUrl(url_name1);
        } else {
            webView.loadUrl("https://www.baidu.com/s?wd=" + url_name);
        }
    }

    //同步系统返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect:
                webViewModel.collect();
                break;
            case R.id.search:
                search(url.getText().toString());
                break;
            case R.id.go_for:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Toast.makeText(WebViewActivity.this, "没有上一页了",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.go_next:
                if (webView.canGoForward()) {
                    webView.goForward();
                } else {
                    Toast.makeText(WebViewActivity.this, "没有下一页了",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.go_home:
                Intent intent = new Intent("android.intent.action.MAIN");
                startActivity(intent);
                break;
            case R.id.refresh:
                webView.loadUrl(webView.getUrl().toString());
                Toast.makeText(WebViewActivity.this, "正在刷新",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.detail:
                Toast.makeText(WebViewActivity.this, "这里应该弹出详细界面",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onChanged(CurrentData currentData) {
        //这里可以用来存储历史记录
        //Log.d("WebViewActivity", currentData.getCurrentTitle() + currentData.getCurrentUrl());
    }

    //传入当前页面信息放入LiveData
    private void putCurrentData(String url, String title) {
        CurrentData currentData = new CurrentData(url, title);
        CurrentDataLiveData.getInstance().setValue(currentData);
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        webView.removeAllViews();
//        webView.destroy();
//    }
}