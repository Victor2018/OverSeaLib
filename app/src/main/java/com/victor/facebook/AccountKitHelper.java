package com.victor.facebook;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.victor.app.App;
import com.victor.oversea.R;
import com.victor.util.Loger;

/**
 * Created by victor on 2017/7/11 0011.
 * account kit phone 登陆
 */

public class AccountKitHelper {
    private String TAG = "AccountKitHelper";
    public static int APP_REQUEST_CODE = 99;
    private AccountKitListener mAccountKitListener;

    public interface AccountKitListener {
        void onAkSignInFail(String errorMessage);
        void onAkSignInSuccess(AccountKitLoginResult loginResult);
    }

    public AccountKitHelper (@NonNull AccountKitListener listener) {
        mAccountKitListener = listener;
    }

    /**
     * 检查当前的会话
     */
    private boolean checkToken () {
        com.facebook.accountkit.AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
            Loger.d(TAG,"checkToken------>token=" + accessToken.getToken());
            getCurrentAccount();
            return true;
        } else {
            //Handle new or logged out user
            Loger.d(TAG,"checkToken------>logged out user");
            return false;
        }
    }

    /**
     * 访问设备上的帐户信息
     */
    private Account getCurrentAccount () {
        final Account[] mAccount = new Account[1];
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                mAccount[0] = account;
                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                if (phoneNumber != null) {
                    String phoneNumberString = phoneNumber.toString();
                    Loger.d(TAG,"getCurrentAccount------>phoneNumberString = " + phoneNumberString);
                }
                if (account != null) {
                    // Get Account Kit ID
                    String accountKitId = account.getId();
                    Loger.d(TAG,"getCurrentAccount------>accountKitId = " + accountKitId);
                    // Get email
                    String email = account.getEmail();
                    Loger.d(TAG,"getCurrentAccount------>email = " + email);
                }
            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
            }
        });
        return mAccount[0];
    }

    /**
     * phone短信登录流程
     */
    public void performSignIn(Activity activity) {
        if (checkToken()){
            performSignOut();
        }
        Loger.d(TAG,"phoneLogin()......");
        final Intent intent = new Intent(activity, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        activity.startActivityForResult(intent, APP_REQUEST_CODE);
    }

    /**
     * 邮箱登录
     */
    public void emailLogin(Activity activity) {
        Loger.d(TAG,"emailLogin()......");
        final Intent intent = new Intent(activity, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.EMAIL,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        activity.startActivityForResult(intent, APP_REQUEST_CODE);
    }

    /**
     * 登出
     */
    public void performSignOut () {
        AccountKit.logOut();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APP_REQUEST_CODE) {
            if (data == null) {
                mAccountKitListener.onAkSignInFail(App.get().getString(R.string.phone_login_canceled));
                return;
            }
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult == null) {
                mAccountKitListener.onAkSignInFail(App.get().getString(R.string.phone_login_canceled));
                return;
            }
            if (loginResult.getError() != null) {
                mAccountKitListener.onAkSignInFail(loginResult.getError().getErrorType().getMessage());
            } else if (loginResult.wasCancelled()) {
                Loger.d(TAG," phone login cancelled!");
                mAccountKitListener.onAkSignInFail(App.get().getString(R.string.phone_login_canceled));
            } else {
//                if (loginResult.getAccessToken() != null) {
//                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
//                } else {
//                    toastMessage = String.format(
//                            "Success:%s...",
//                            loginResult.getAuthorizationCode().substring(0, 10));
//                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.
                // Success! Start your next activity...
                Loger.d(TAG," phone login success!");
                mAccountKitListener.onAkSignInSuccess(loginResult);
            }
        }
    }

    public void onDestory () {
        if (mAccountKitListener != null) {
            mAccountKitListener = null;
        }
    }
}
