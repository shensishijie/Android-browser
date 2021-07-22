package com.example.android_browser.webview;

import androidx.lifecycle.MutableLiveData;

//LiveData,存储当前网页的标题和URL
public class CurrentDataLiveData extends MutableLiveData<CurrentData> {
    //private static CurrentDataLiveData sInstance;
    private CurrentDataLiveData() {
    }

    private static class Holder {
        public static final CurrentDataLiveData Instance = new CurrentDataLiveData();
    }

    public static CurrentDataLiveData getInstance() {
        return Holder.Instance;
    }
}
