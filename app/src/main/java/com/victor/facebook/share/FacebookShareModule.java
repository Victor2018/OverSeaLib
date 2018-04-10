package com.victor.facebook.share;

import android.content.Intent;

import com.victor.data.ShareInfo;

/**
 * facebook 分享模块
 * Created by victor on 2017/7/20 0020.
 */

public class FacebookShareModule {
    private String TAG = "FacebookShareModule";
    private IShareListener mShareListener;
    private FaceBookShareUtils faceBookShareUtils;

    public FacebookShareModule (IShareListener listener) {
        mShareListener = listener;
        faceBookShareUtils = new FaceBookShareUtils(listener.getActivity(),mShareListener);
    }

    public void share(ShareInfo shareInfo) {
        faceBookShareUtils.share(shareInfo);
    }

    public void onDestroy() {
        mShareListener = null;
        faceBookShareUtils.onDestory();
        faceBookShareUtils = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        faceBookShareUtils.onActivityResult(requestCode, resultCode, data);
    }



}
