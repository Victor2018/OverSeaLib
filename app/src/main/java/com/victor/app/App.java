package com.victor.app;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

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

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(
                        new TwitterAuthConfig("G1NhfGkCLWNcyPtLNfPdV1wIt",
                                "m8JgpCmXMfOCkytOaLQ7ki9gqmCLDLzJbo4pnGgKiqG4PdC8wp"))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

}
