package com.um.mydemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by irene on 2018/4/13.
 */

public class WelcomeActivity extends Activity {
    @BindView(R.id.wel_btn1)
    Button mButton1;
    @BindView(R.id.wel_btn2)
    Button mButton2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_fragment);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.wel_btn1)
    public void startLoginPage(View view) {
        // TODO submit data to server...
    }

    @OnClick(R.id.wel_btn2)
    public void startProfilePage(View view) {
        // TODO submit data to server...
    }
}
