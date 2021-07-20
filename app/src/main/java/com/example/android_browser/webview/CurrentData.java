package com.example.android_browser.webview;

    //把数据用实体类的形式存进LiveData
public class CurrentData {
    private String currentUrl;
    private  String currentTitle;

    public CurrentData(String currentUrl, String currentTitle) {
        this.currentUrl = currentUrl;
        this.currentTitle = currentTitle;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }
}
