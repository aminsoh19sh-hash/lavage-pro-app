package com.mysalonypro.salony;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/** يطبّق اللغة (RTL/LTR) ولون أيقونات شريط الحالة. */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b) {
        T.lang = Store.lang(this);
        super.onCreate(b);
        lightBars(!Store.isDark(this));
    }

    protected void lightBars(boolean light) {
        WindowInsetsControllerCompat c = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (c != null) {
            c.setAppearanceLightStatusBars(light);
            c.setAppearanceLightNavigationBars(light);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getWindow().getDecorView().setLayoutDirection(
                T.isAr() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
    }
}
