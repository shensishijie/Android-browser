package com.example.android_browser.webview;

import android.util.Log;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class WebViewModel extends ViewModel implements Observer<CurrentData> {
    public WebViewModel() {
        //CurrentDataLiveData.getInstance().observe();
    }

    void collect() {

        Log.d("WebViewModel", CurrentDataLiveData.getInstance().getValue().getCurrentTitle());
    }

    @Override
    public void onChanged(CurrentData currentData) {
    }
}
