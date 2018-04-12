package com.victor.oversea;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.victor.data.ShareInfo;
import com.victor.facebook.AccountKitHelper;
import com.victor.facebook.FacebookHelper;
import com.victor.facebook.share.FacebookShareModule;
import com.victor.facebook.share.IShareListener;
import com.victor.twitter.TwitterHelper;
import com.victor.util.ToastUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        FacebookHelper.FacebookListener,AccountKitHelper.AccountKitListener,IShareListener,
        TwitterHelper.OnTwitterLoginListener {
    private String TAG = "MainActivity";
    private Button mBtnSubscribe,mBtnLogToken,mBtnGooglePay,mBtnFackbookLog,
            mBtnPhoneLog,mBtnFacebookShare,mBtnTwitterShare;
    private TwitterLoginButton mBtnTwitterLog;

    private FacebookHelper mFacebookHelper;
    private AccountKitHelper mAccountKitHelper;
    private TwitterHelper mTwitterHelper;
    private FacebookShareModule facebookShareModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGooglePlayServiceAvailable();
        initialize();
    }

    private void initialize () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        mBtnSubscribe = (Button)findViewById(R.id.btn_subscribe);
        mBtnLogToken = (Button)findViewById(R.id.btn_log_token);
        mBtnGooglePay = (Button)findViewById(R.id.btn_google_pay);
        mBtnFackbookLog = (Button)findViewById(R.id.btn_facebook_log);
        mBtnPhoneLog = (Button)findViewById(R.id.btn_phone_log);
        mBtnTwitterLog = (TwitterLoginButton) findViewById(R.id.btn_twitter_log);
        mBtnFacebookShare = (Button)findViewById(R.id.btn_facebook_share);
        mBtnTwitterShare = (Button)findViewById(R.id.btn_twitter_share);

        mBtnSubscribe.setOnClickListener(this);
        mBtnLogToken.setOnClickListener(this);
        mBtnGooglePay.setOnClickListener(this);
        mBtnFackbookLog.setOnClickListener(this);
        mBtnPhoneLog.setOnClickListener(this);
        mBtnFacebookShare.setOnClickListener(this);
        mBtnTwitterShare.setOnClickListener(this);

        initFacebookAndAccouontKit();

    }

    private void initFacebookAndAccouontKit () {
        mFacebookHelper = new FacebookHelper(this);
        mAccountKitHelper = new AccountKitHelper(this);
        mTwitterHelper = new TwitterHelper(mBtnTwitterLog,this);
        facebookShareModule = new FacebookShareModule(this);
    }

    /**
     * 检测google服务是否可用
     * @return
     */
    private boolean isGooglePlayServiceAvailable () {
        boolean isGooglePlayServiceAvailable = false;
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            Log.e(TAG, "GooglePlayServicesUtil service is available.....................");
            isGooglePlayServiceAvailable = true;
        } else {
            Toast.makeText(getApplicationContext(),"google service is not available!",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "GooglePlayServicesUtil service is NOT available.....................");
        }
        return isGooglePlayServiceAvailable;
    }

    private void getToken () {
        if (!isGooglePlayServiceAvailable ()) {
            return;
        }
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();
        // Log and toast
        String msg = getString(R.string.msg_token_fmt, token);
        Log.e(TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_subscribe:
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                // [END subscribe_topics]

                // Log and toast
                String msg = getString(R.string.msg_subscribed);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_log_token:
                getToken();
                break;
            case R.id.btn_google_pay:
                startActivity(new Intent(MainActivity.this,BillingActivity.class));
                break;
            case R.id.btn_facebook_log:
                mFacebookHelper.performSignIn(this);
                break;
            case R.id.btn_phone_log:
                mAccountKitHelper.performSignIn(this);
                break;
            case R.id.btn_facebook_share:
                ShareInfo info = new ShareInfo();
                info.cover = "facebook share";
                info.title = "facebook";
                info.url = "www.baidu.com";
                info.summary = "this is come from facebook share";
                facebookShareModule.share(info);
                break;
            case R.id.btn_twitter_share:
                mTwitterHelper.share(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookHelper.onActivityResult(requestCode, resultCode, data);
        mAccountKitHelper.onActivityResult(requestCode, resultCode, data);
        facebookShareModule.onActivityResult(requestCode,resultCode,data);
        mBtnTwitterLog.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFacebookHelper != null) {
            mFacebookHelper.onDestory();
            mFacebookHelper = null;
        }
        if (mAccountKitHelper != null) {
            mAccountKitHelper.onDestory();
            mAccountKitHelper = null;
        }
        if (mTwitterHelper != null) {
            mTwitterHelper.onDestory();
            mTwitterHelper = null;
        }
    }

    @Override
    public void onFbSignInFail(String errorMessage) {
        ToastUtils.showShort(errorMessage);
    }

    @Override
    public void onFbSignInSuccess(LoginResult loginResult) {
        String accessId = loginResult.getAccessToken().getUserId();
        ToastUtils.showShort(accessId + "Log successfully!");
    }

    @Override
    public void onFBSignOut() {

    }

    @Override
    public void onAkSignInFail(String errorMessage) {
        ToastUtils.showShort(errorMessage);
    }

    @Override
    public void onAkSignInSuccess(AccountKitLoginResult loginResult) {
        String accessId = loginResult.getAccessToken().getAccountId();
        ToastUtils.showShort(accessId + "Log successfully!");
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onShareSuccess(int shareType) {
        ToastUtils.showShort("onShareSuccess successfully");
    }

    @Override
    public void onShareCancel(int shareType, String error) {
        ToastUtils.showShort("onShareCancel fail error = " + error);
    }

    @Override
    public void onShareError(int shareType, String error) {
        ToastUtils.showShort("onShareError fail error = " + error);
    }

    @Override
    public void OnTwitterLogin(boolean twitterLoginSuccess, String error) {
        ToastUtils.showShort(error);
    }
}
