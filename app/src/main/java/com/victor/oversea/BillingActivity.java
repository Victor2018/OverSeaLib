package com.victor.oversea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.victor.googlepay.GoogleInBillingHelper;
import com.victor.googlepay.TransactionDetails;
import com.victor.util.Loger;

public class BillingActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleInBillingHelper.OnGoogleInBillingListener {
    private String TAG = "BillingActivity";
    private Button mBtnPurchase;

    private GoogleInBillingHelper mGoogleInBillingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        initialize();
    }
    private void initialize () {
        mBtnPurchase = (Button) findViewById(R.id.btn_purchase);
        mBtnPurchase.setOnClickListener(this);

        mGoogleInBillingHelper = new GoogleInBillingHelper(this,this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_purchase:
                if (mGoogleInBillingHelper != null) {
                    mGoogleInBillingHelper.sendRequestWithParms(GoogleInBillingHelper.PURCHASE,"android.test.purchased");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mGoogleInBillingHelper != null) {
            if (!mGoogleInBillingHelper.onActivityResult(requestCode,resultCode,data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onBillingComplete(TransactionDetails details) {
        Loger.d(TAG,"onBillingComplete-buy success" + details.orderId);
    }

    @Override
    public void onBillingFailed(String error) {
        Loger.e(TAG,"onBillingFailed-error = " + error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleInBillingHelper != null) {
            mGoogleInBillingHelper.onDestory();
            mGoogleInBillingHelper = null;
        }
    }
}
