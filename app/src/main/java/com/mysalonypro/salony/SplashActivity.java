package com.mysalonypro.salony;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/** شاشة البداية: قطرة + موجات + شريط تحميل (نفس #sp في الصفحة). */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_splash);
        lightBars(false);

        ((TextView) findViewById(R.id.title)).setText(Ui.brand(this));
        ((TextView) findViewById(R.id.tagline)).setText(T.t("tag"));

        ring(findViewById(R.id.ring1), 0);
        ring(findViewById(R.id.ring2), 700);

        ObjectAnimator fl = ObjectAnimator.ofFloat(findViewById(R.id.logo), View.TRANSLATION_Y, 0f, -Ui.dp(this, 5), 0f);
        fl.setDuration(2000);
        fl.setRepeatCount(ObjectAnimator.INFINITE);
        fl.setInterpolator(new AccelerateDecelerateInterpolator());
        fl.start();

        ObjectAnimator bar = ObjectAnimator.ofFloat(findViewById(R.id.bar), View.TRANSLATION_X, -Ui.dp(this, 48), Ui.dp(this, 144));
        bar.setDuration(1200);
        bar.setRepeatCount(ObjectAnimator.INFINITE);
        bar.setInterpolator(new AccelerateDecelerateInterpolator());
        bar.start();

        new Handler(Looper.getMainLooper()).postDelayed(this::next, 2100);
    }

    private void ring(View v, long delay) {
        ObjectAnimator a = ObjectAnimator.ofPropertyValuesHolder(v,
                PropertyValuesHolder.ofFloat(View.SCALE_X, .4f, 1.5f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, .4f, 1.5f),
                PropertyValuesHolder.ofFloat(View.ALPHA, .8f, 0f));
        a.setDuration(2000);
        a.setStartDelay(delay);
        a.setRepeatCount(ObjectAnimator.INFINITE);
        a.start();
    }

    private void next() {
        if (isFinishing() || isDestroyed()) return;
        findViewById(R.id.splash_root).animate().alpha(0f).setDuration(600).withEndAction(() -> {
            if (isFinishing() || isDestroyed()) return;
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }).start();
    }
}
