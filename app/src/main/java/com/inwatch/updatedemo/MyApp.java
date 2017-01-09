package com.inwatch.updatedemo;

import android.app.Application;
import android.util.Log;

/**
 * Created by cbw on 2016/8/27.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UpdateConfig.init(this);
        Log.d("MyApp", "onCreate: ");
    }
}
