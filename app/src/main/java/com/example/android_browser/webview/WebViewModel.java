package com.example.android_browser.webview;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WebViewModel extends ViewModel {
    public WebViewModel() {

    }
    private MutableLiveData<String> currentUrl;
    private MutableLiveData<String> currentTitle;

    public MutableLiveData<String> getCurrentUrl() {
        if (currentUrl == null) {
            currentUrl = new MutableLiveData<String>();
        }
        return currentUrl;
    }

    public MutableLiveData<String> getCurrentTitle() {
        if (currentTitle == null) {
            currentTitle = new MutableLiveData<String>();
        }
        return currentTitle;
    }

}
