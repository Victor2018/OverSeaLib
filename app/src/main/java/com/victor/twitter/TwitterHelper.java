package com.victor.twitter;

import android.app.Activity;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.victor.app.App;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author Victor
 * @Date Create on 2018/4/12 14:37.
 * @Describe
 */

public class TwitterHelper {
    private TwitterLoginButton mTwitterLoginButton;
    private OnTwitterLoginListener mOnTwitterLoginListener;
    public interface OnTwitterLoginListener {
        void OnTwitterLogin (boolean twitterLoginSuccess,String error);
    }

    public TwitterHelper (TwitterLoginButton btn,OnTwitterLoginListener listener) {
        mTwitterLoginButton = btn;
        mOnTwitterLoginListener = listener;
        init();
    }

    private void init () {
        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterAuthToken authToken = result.data.getAuthToken();

                String token = authToken.token;
//                String appId = getResources().getString(R.string.twitter_app_id);
//                String tokenSecret = authToken.secret;
                if (mOnTwitterLoginListener != null) {
                    mOnTwitterLoginListener.OnTwitterLogin(true,"Twitter log successfully token = " + token);
                }
            }

            @Override public void failure(TwitterException exception) {
                // Do something on failure
                if (mOnTwitterLoginListener != null) {
                    mOnTwitterLoginListener.OnTwitterLogin(false,"Twitter log failed error" + exception.getMessage());
                }
            }
        });
    }

    public void share (Activity activity) {
        if (activity == null) return;
        //这里分享一个链接，更多分享配置参考官方介绍：https://dev.twitter.com/twitterkit/android/compose-tweets
        try {
            TweetComposer.Builder builder = new TweetComposer.Builder(activity)
                    .url(new URL("https://www.google.com/"));
            builder.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void onDestory () {
        if (mOnTwitterLoginListener != null) {
            mOnTwitterLoginListener = null;
        }
    }


}
