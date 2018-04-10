package com.victor.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.victor.app.App;
import com.victor.oversea.R;

import java.util.Arrays;

public class FacebookHelper {
  private String TAG = "FacebookHelper";
  private FacebookListener mListener;
  private CallbackManager mCallBackManager;

  public interface FacebookListener {
    void onFbSignInFail(String errorMessage);

    void onFbSignInSuccess(LoginResult loginResult);

    void onFBSignOut();
  }

  public FacebookHelper(@NonNull FacebookListener facebookListener) {
    mListener = facebookListener;
    mCallBackManager = CallbackManager.Factory.create();
    FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
      @Override
      public void onSuccess(LoginResult loginResult) {
//        mListener.onFbSignInSuccess(loginResult.getAccessToken().getToken(),
//            loginResult.getAccessToken().getUserId());
        mListener.onFbSignInSuccess(loginResult);
      }

      @Override
      public void onCancel() {
        mListener.onFbSignInFail(App.get().getString(R.string.user_cancelled_operation));
      }

      @Override
      public void onError(FacebookException e) {
        mListener.onFbSignInFail(e.getMessage());
      }
    };
    LoginManager.getInstance().registerCallback(mCallBackManager, mCallBack);
  }

  @NonNull
  @CheckResult
  public CallbackManager getCallbackManager() {
    return mCallBackManager;
  }

  public void performSignIn(Activity activity) {
    LoginManager.getInstance()
        .logInWithReadPermissions(activity,
            Arrays.asList("public_profile", "user_friends", "email"));
  }

  public void performSignIn(Fragment fragment) {
    LoginManager.getInstance()
        .logInWithReadPermissions(fragment,
            Arrays.asList("public_profile", "user_friends", "email"));
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mCallBackManager.onActivityResult(requestCode, resultCode, data);
  }

  public void performSignOut() {
    LoginManager.getInstance().logOut();
    mListener.onFBSignOut();
  }

  public String getToken (Context context) {
      if (!isGooglePlayServiceAvailable (context)) {
          return null;
      }
      // Get token
      String token = FirebaseInstanceId.getInstance().getToken();
      return token;
  }

  /**
   * 检测google服务是否可用
   * @return
   */
  private boolean isGooglePlayServiceAvailable (Context context) {
    boolean isGooglePlayServiceAvailable = false;
    int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
    if (status == ConnectionResult.SUCCESS) {
      Log.e(TAG, "GooglePlayServicesUtil service is available.....................");
      isGooglePlayServiceAvailable = true;
    } else {
      Log.e(TAG, "GooglePlayServicesUtil service is NOT available.....................");
    }
    return isGooglePlayServiceAvailable;
  }

  public void onDestory () {
    if (mListener != null) {
        mListener = null;
    }
    if (mCallBackManager != null) {
      mCallBackManager = null;
    }
  }
}
