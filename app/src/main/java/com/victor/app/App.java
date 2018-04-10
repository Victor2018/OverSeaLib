package com.victor.app;

import android.app.Application;

/**
 * Created by victor on 2017/9/12 0012.
 */

public class App extends Application {
    private static App instance;


    public App() {
        instance = this;
    }

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
