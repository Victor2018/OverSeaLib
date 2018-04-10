package com.victor.facebook.share;

import android.app.Activity;

public interface IShareListener {

    Activity getActivity();

    void onShareSuccess(int shareType);

    void onShareCancel(int shareType, String error);

    void onShareError(int shareType, String error);
}
