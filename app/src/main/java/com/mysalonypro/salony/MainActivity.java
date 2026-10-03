package com.mysalonypro.salony;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

/** الهيكل: الهيدر + المحتوى (Fragments) + شريط التنقل السفلي + القائمة الجانبية. */
public class MainActivity extends BaseActivity {
    private DrawerLayout drawer;
    private String view = "home";
    private View navHome, navMine, navMore, drHome, drMine, drAbout, drPrivacy, drLang, drTheme;
    private TextView brand, tvLang, drBrand, drTag;
    private ImageView icMenu, icTheme;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        if (b != null) view = b.getString("view", "home");

        drawer = findViewById(R.id.drawer);
        drawer.setScrimColor(0xAA000000);
        brand = findViewById(R.id.brand);
        tvLang = findViewById(R.id.tv_lang);
        icMenu = findViewById(R.id.ic_menu);
        icTheme = findViewById(R.id.ic_theme);
        drBrand = findViewById(R.id.dr_brand);
        drTag = findViewById(R.id.dr_tag);
        navHome = findViewById(R.id.nav_home);
        navMine = findViewById(R.id.nav_mine);
        navMore = findViewById(R.id.nav_more);
        drHome = findViewById(R.id.dr_home);
        drMine = findViewById(R.id.dr_mine);
        drAbout = findViewById(R.id.dr_about);
        drPrivacy = findViewById(R.id.dr_privacy);
        drLang = findViewById(R.id.dr_lang);
        drTheme = findViewById(R.id.dr_theme);

        // عرض القائمة: min(82% من الشاشة، 320dp)
        View dw = findViewById(R.id.dw);
        ViewGroup.LayoutParams dlp = dw.getLayoutParams();
        dlp.width = (int) Math.min(getResources().getDisplayMetrics().widthPixels * 0.82f, Ui.dp(this, 320));
        dw.setLayoutParams(dlp);

        // عرض الشريط السفلي: max 420dp
        final View nav = findViewById(R.id.nav);
        nav.post(() -> {
            int max = Ui.dp(this, 420);
            if (nav.getWidth() > max) {
                ViewGroup.LayoutParams lp = nav.getLayoutParams();
                lp.width = max;
                nav.setLayoutParams(lp);
            }
        });

        findViewById(R.id.btn_menu).setOnClickListener(v -> openDrawer());
        findViewById(R.id.btn_lang).setOnClickListener(v -> toggleLang());
        findViewById(R.id.btn_theme).setOnClickListener(v -> toggleTheme());
        navHome.setOnClickListener(v -> go("home"));
        navMine.setOnClickListener(v -> go("mine"));
        navMore.setOnClickListener(v -> openDrawer());
        drHome.setOnClickListener(v -> go("home"));
        drMine.setOnClickListener(v -> go("mine"));
        drAbout.setOnClickListener(v -> go("about"));
        drPrivacy.setOnClickListener(v -> go("privacy"));
        drLang.setOnClickListener(v -> toggleLang());
        drTheme.setOnClickListener(v -> toggleTheme());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else if (!"home".equals(view)) {
                    go("home");
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        chrome();
        showFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("view", view);
    }

    private void openDrawer() { drawer.openDrawer(GravityCompat.START); }

    /** الانتقال بين: home / mine / about / privacy */
    void go(String v) {
        view = v;
        drawer.closeDrawer(GravityCompat.START);
        chrome();
        showFragment();
    }

    private void showFragment() {
        Fragment f;
        switch (view) {
            case "mine": f = new MineFragment(); break;
            case "about": f = new AboutFragment(); break;
            case "privacy": f = new PrivacyFragment(); break;
            default: f = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, f).commitAllowingStateLoss();
    }

    private void toggleLang() {
        Store.setLang(this, T.isAr() ? "en" : "ar");
        recreate();
    }

    private void toggleTheme() {
        boolean dark = Store.isDark(this);
        Store.setTheme(this, dark ? "light" : "dark");
        AppCompatDelegate.setDefaultNightMode(dark ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    private void nav(View item, int icon, String label, boolean on) {
        ((ImageView) item.findViewById(R.id.nav_icon)).setImageResource(icon);
        ((TextView) item.findViewById(R.id.nav_text)).setText(label);
        item.setSelected(on);
    }

    private void row(View r, int icon, String label, boolean on, String em) {
        ((ImageView) r.findViewById(R.id.row_icon)).setImageResource(icon);
        ((TextView) r.findViewById(R.id.row_text)).setText(label);
        TextView e = r.findViewById(R.id.row_em);
        if (em != null) {
            e.setText(em);
            e.setVisibility(View.VISIBLE);
        }
        r.setSelected(on);
    }

    /** يحدّث الهيدر والتنقل والقائمة (دالة chrome في الصفحة). */
    private void chrome() {
        boolean dark = Store.isDark(this);
        icMenu.setImageResource(R.drawable.ic_menu);
        icTheme.setImageResource(dark ? R.drawable.ic_sun : R.drawable.ic_moon);
        brand.setText(Ui.brand(this));
        tvLang.setText(T.isAr() ? "EN" : "عربي");

        nav(navHome, R.drawable.ic_home, T.t("home"), view.equals("home"));
        nav(navMine, R.drawable.ic_cal, T.t("mine"), view.equals("mine"));
        nav(navMore, R.drawable.ic_menu, T.t("more"), false);

        drBrand.setText(Ui.brand(this));
        drTag.setText(T.t("tag"));
        row(drHome, R.drawable.ic_home, T.t("home"), view.equals("home"), null);
        row(drMine, R.drawable.ic_cal, T.t("mine"), view.equals("mine"), null);
        row(drAbout, R.drawable.ic_info, T.t("about"), view.equals("about"), null);
        row(drPrivacy, R.drawable.ic_shield, T.t("privacy"), view.equals("privacy"), null);
        row(drLang, R.drawable.ic_globe, T.t("lang"), false, T.isAr() ? "English" : "العربية");
        row(drTheme, dark ? R.drawable.ic_sun : R.drawable.ic_moon, dark ? T.t("light") : T.t("dark"), false, null);
    }
}
