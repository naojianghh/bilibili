package ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.naojianghh.bilibili3.R;

import base.BaseActivity;

public class SplashActivity extends BaseActivity {
    private static final int SPLASH_DELAY = 1500;
    @Override
    protected void initViews() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }
}
