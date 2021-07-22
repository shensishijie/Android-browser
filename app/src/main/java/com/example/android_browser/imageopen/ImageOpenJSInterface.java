package com.example.android_browser.imageopen;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

public class ImageOpenJSInterface {
    private Context context;
    private String[] imageUrls;

    public ImageOpenJSInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void loadImageUrl(String[] imageUrl){
        imageUrls=imageUrl;

    }

    @JavascriptInterface
    public void openImage(String img) {
        Intent intent = new Intent();
        intent.putExtra("curImageUrl", img);
        intent.putExtra("imageUrls",imageUrls);
        intent.setClass(context, PhotoBrowserActivity.class);
        context.startActivity(intent);
    }
}
