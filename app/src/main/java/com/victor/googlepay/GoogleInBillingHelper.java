package com.victor.googlepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.victor.util.Loger;

import java.util.HashMap;


/**
 * google 应用内支付模块
 * Created by victor on 2017/7/25 0025.
 */

public class GoogleInBillingHelper implements BillingProcessor.IBillingHandler{
    private String TAG = "GoogleInBillingHelper";
    public static final int PURCHASE      = 0x001;
    private static final int CONSUME      = 0x002;
    private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkWfhjsw0FbcBFmSciboQonoe/eTwCjaaXCds2jbGsNZ+Byyj+/mCeFgvNvXHnKw/SxDAzMBsyxs/j9q2jeGUxzJyxJm0ssdMfIize8d/Pyl5+ugnZZjyZHQOiw633Sr4qOZLZWKJknkTlKzZqegcafvGHPfTxkfHlP/Xxk7trEK2N7+Z7IoJ9JgeXRfVC46/XYVelPz5rQDCmuJYB06X7U2XHC/RsCqk2ZM8eX0HuNnAdl2QbLUjvHpb2WzIAl0IF0/HNZeS5PyhUb77i7aZ2IuJ8VkeSnpfHOdHuKGgm5Ytr+l6vWs5PJvgA9t0nil3BPQX9AGoGTxQgGTli0UjAQIDAQAB";
    private String MERCHANT_ID = "Meetgo";
    private Activity mActivity;
    private BillingProcessor billingProcessor;
    private OnGoogleInBillingListener mOnGoogleInBillingListener;
    private Handler mRequestHandler;
    private HandlerThread mRequestHandlerThread;//处理商品购买、消耗独立子线程
    private boolean isGpAvailable = false;//google play billing 是否可用
    private String purchaseId = "android.test.purchased";//当前购买商品 id

    public GoogleInBillingHelper (Activity activity, OnGoogleInBillingListener listener) {
        mActivity = activity;
        mOnGoogleInBillingListener = listener;
        init();
    }

    /**
     * 初始化
     */
    private void init () {
        Loger.d(TAG, "onProductPurchased()......");
        isGpAvailable = isGooglePlayServiceAvailable();
        billingProcessor = new BillingProcessor(mActivity, base64EncodedPublicKey, MERCHANT_ID, this);
        startBillingTask();
    }

    /**
     * 启动商品购买、消耗独立子线程
     */
    private void startBillingTask () {
        Loger.d(TAG, "startBillingTask()......");
        mRequestHandlerThread = new HandlerThread("BillingTask");
        mRequestHandlerThread.start();
        mRequestHandler = new Handler(mRequestHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                HashMap<Integer,Object> purchaseMap = (HashMap<Integer, Object>) msg.obj;
                switch (msg.what) {
                    case PURCHASE://购买商品
                        String id = (String) purchaseMap.get(PURCHASE);
                        buyPurchase(id);
                        break;
                    case CONSUME://消耗商品
                        TransactionDetails details = (TransactionDetails) purchaseMap.get(CONSUME);
                        consumePurchase(details);
                        break;
                }
            }
        };
    }


    /**
     * 发起购买请求
     * @param Msg
     * @param requestData
     */
    public void sendRequestWithParms (int Msg,Object requestData) {
        Loger.d(TAG, "sendRequestWithParms()......");
        HashMap<Integer, Object> requestMap = new HashMap<Integer, Object>();
        requestMap.put(Msg, requestData);
        Message msg = mRequestHandler.obtainMessage(Msg,requestMap);
        mRequestHandler.sendMessage(msg);
    }

    /**
     * 发起购买请求
     * @param msg
     */
    public void sendRequest (int msg) {
        Loger.d(TAG, "sendRequest()......");
        mRequestHandler.sendEmptyMessage(msg);
    }

    /**
     * 购买商品
     * @param purchaseId
     */
    private void buyPurchase(String purchaseId) {
        Loger.d(TAG, "buyPurchase()......purchaseId = " + purchaseId);
        if(!isGpAvailable) {
            if (mOnGoogleInBillingListener != null) {
                mOnGoogleInBillingListener.onBillingFailed("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
                return;
            }
        }
        if (billingProcessor != null) {
            this.purchaseId = purchaseId;
            billingProcessor.purchase(mActivity,purchaseId);
        }
    }

    /**
     *  消耗商品（如果你的商品是可重复购买，需要在购买成功后将商品消耗掉）
     * @param details
     */
    private void consumePurchase (TransactionDetails details) {
        Loger.d(TAG, "buyPurchase()......details = " + details.toString());
        if(!isGpAvailable) {
            if (mOnGoogleInBillingListener != null) {
                mOnGoogleInBillingListener.onBillingFailed("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
                return;
            }
        }
        if (billingProcessor != null) {
            billingProcessor.consumePurchase(purchaseId);
        }
        if (mOnGoogleInBillingListener != null) {
            mOnGoogleInBillingListener.onBillingComplete(details);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Loger.d(TAG, "onProductPurchased()......");
        sendRequestWithParms(CONSUME,details);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Loger.d(TAG, "onPurchaseHistoryRestored()......");
        if (billingProcessor != null) {
            for(String sku : billingProcessor.listOwnedProducts()) {
                Loger.d(TAG, "onPurchaseHistoryRestored()-Owned Managed Product: " + sku);
            }
            for(String sku : billingProcessor.listOwnedSubscriptions()) {
                Loger.d(TAG, "onPurchaseHistoryRestored()-Owned Subscription: " + sku);
            }
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Loger.e(TAG, "onBillingError()......error = " +error);
        if (mOnGoogleInBillingListener != null) {
            mOnGoogleInBillingListener.onBillingFailed("onBillingError: " + Integer.toString(errorCode));
            return;
        }
    }

    @Override
    public void onBillingInitialized() {
        Loger.d(TAG, "onBillingInitialized()......");
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Loger.d(TAG, "onActivityResult()......requestCode = " + requestCode);
        Loger.d(TAG, "onActivityResult()......resultCode = " + resultCode);
        if (billingProcessor != null) {
           return billingProcessor.handleActivityResult(requestCode, resultCode, data);
        }
        return false;
    }

    public void onDestory () {
        Loger.d(TAG, "onDestory()......");
        if (billingProcessor != null) {
            billingProcessor.release();
            billingProcessor = null;
        }
        if (mRequestHandlerThread != null) {
            mRequestHandlerThread.quit();
            mRequestHandlerThread = null;
        }
    }

    /**
     * 检测google服务是否可用
     * @return
     */
    private boolean isGooglePlayServiceAvailable () {
        boolean isGooglePlayServiceAvailable = false;
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mActivity);
        if (status == ConnectionResult.SUCCESS) {
            Log.d(TAG, "GooglePlayServicesUtil service is available");
            isGooglePlayServiceAvailable = true;
        } else {
            Log.d(TAG, "GooglePlayServicesUtil service is not available");
        }
        return isGooglePlayServiceAvailable;
    }

    public interface OnGoogleInBillingListener {
        void onBillingComplete(TransactionDetails details);
        void onBillingFailed (String error);
    }

}
