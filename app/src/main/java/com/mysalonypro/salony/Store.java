package com.mysalonypro.salony;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONObject;

import java.security.SecureRandom;

/** التخزين المحلي (بديل localStorage). */
final class Store {
    /** نص البحث في الرئيسية (يبقى بين الصفحات). */
    static String query = "";

    private static SharedPreferences sp(Context c) {
        return c.getApplicationContext().getSharedPreferences("lavagepro", Context.MODE_PRIVATE);
    }

    static String lang(Context c) { return sp(c).getString("lang", "ar"); }

    static void setLang(Context c, String l) { sp(c).edit().putString("lang", l).apply(); }

    /** "" = حسب النظام، أو light / dark */
    static String theme(Context c) { return sp(c).getString("theme", ""); }

    static void setTheme(Context c, String t) { sp(c).edit().putString("theme", t).apply(); }

    static void applyTheme(Context c) {
        String t = theme(c);
        AppCompatDelegate.setDefaultNightMode(t.equals("dark") ? AppCompatDelegate.MODE_NIGHT_YES
                : t.equals("light") ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    static boolean isDark(Context c) {
        return (c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    /** رمز الجهاز العشوائي — يتيح رؤية المواعيد بدون حساب. */
    static String deviceId(Context c) {
        SharedPreferences p = sp(c);
        String d = p.getString("dev", null);
        if (d == null) {
            SecureRandom r = new SecureRandom();
            d = Long.toString(System.currentTimeMillis(), 36)
                    + Long.toString(Math.abs(r.nextLong()), 36).substring(0, 10);
            p.edit().putString("dev", d).apply();
        }
        return d;
    }

    /** {name, phone} */
    static String[] profile(Context c) {
        try {
            JSONObject o = new JSONObject(sp(c).getString("prof", "{}"));
            return new String[]{o.optString("name", ""), o.optString("phone", "")};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    static void saveProfile(Context c, String name, String phone) {
        try {
            JSONObject o = new JSONObject();
            o.put("name", name);
            o.put("phone", phone);
            sp(c).edit().putString("prof", o.toString()).apply();
        } catch (Exception ignore) { }
    }
}
