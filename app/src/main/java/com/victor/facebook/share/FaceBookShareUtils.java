package com.victor.facebook.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.victor.data.ShareInfo;
import com.victor.oversea.R;
import com.victor.util.Loger;
import com.victor.util.ResUtils;

/**
 * facebook分享工具类
 * Created by victor on 2017/7/20 0020.
 */

public class FaceBookShareUtils {
    private String TAG = "FaceBookShareUtils";
    private Activity mActivity ;
    private ShareDialog shareDialog;
    private static CallbackManager callBackManager;
    public static final int SHARE_REQUEST_CODE = 0x111;
    private ShareLinkContent.Builder shareLinkContentBuilder;
    private IShareListener mShareListener;

    public FaceBookShareUtils(Activity activity, IShareListener listener) {
        this.mActivity = activity ;
        mShareListener = listener;
        init();
    }
    private void init () {
        initCallbackManager();
        shareDialog = new ShareDialog(mActivity);
        //注册分享状态监听回调接口
        shareDialog.registerCallback(callBackManager, facebookCallback, FaceBookShareUtils.SHARE_REQUEST_CODE);
        shareLinkContentBuilder = new ShareLinkContent.Builder();
    }

    public void initCallbackManager() {
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(mActivity);
        }
        if (callBackManager == null) {
            callBackManager = CallbackManager.Factory.create();
        }
    }

    /**
     * 分享
     */
    public void share(ShareInfo shareInfo) {

        shareLinkContentBuilder.setContentTitle(shareInfo.title)
//                .setImageUrl(Uri.parse(shareInfo.url))
//                .setContentDescription(shareInfo.summary)
                .setContentUrl(Uri.parse(shareInfo.url));

        ShareLinkContent shareLinkContent = shareLinkContentBuilder.build();
        if(shareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(mActivity,shareLinkContent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callBackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * facebook分享状态回调
     */
    private FacebookCallback facebookCallback = new FacebookCallback() {

        @Override
        public void onSuccess(Object o) {
            Loger.d(TAG,"onSuccess()......");
            mShareListener.onShareSuccess(IShareType.SHARE_FACEBOOK);
        }

        @Override
        public void onCancel() {
            Loger.d(TAG,"onCancel()......");
            invokeShareFailed(R.string.notify_share_cancel);
        }

        @Override
        public void onError(FacebookException error) {
            Loger.d(TAG,"onError()......");
            invokeShareFailed(R.string.notify_share_failed);
        }
    };

    private void invokeShareFailed(int textRes) {
        String notify = ResUtils.getStringRes(textRes);
        mShareListener.onShareCancel(IShareType.SHARE_FACEBOOK, notify);
    }

    public void onDestory () {
        if (shareLinkContentBuilder != null) {
            shareLinkContentBuilder = null;
        }
        if (callBackManager != null) {
            callBackManager = null;
        }
        if (mShareListener != null) {
            mShareListener = null;
        }

    }
}
