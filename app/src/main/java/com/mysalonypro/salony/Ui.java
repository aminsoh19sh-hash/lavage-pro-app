package com.mysalonypro.salony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

final class Ui {
    private Ui() { }

    static int dp(Context c, float v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    /** نفس loc() في الصفحة: أرقام لاتينية دائما. */
    static Locale loc() {
        if (T.isAr()) {
            return new Locale.Builder().setLanguage("ar").setRegion("MA")
                    .setUnicodeLocaleKeyword("nu", "latn").build();
        }
        return Locale.UK;
    }

    static String iso(Calendar c) {
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    /** "yyyy-MM-dd" → Calendar (الساعة 12 ظهرا). */
    static Calendar parse(String d) {
        Calendar c = Calendar.getInstance();
        try {
            String[] p = d.split("-");
            c.set(Integer.parseInt(p[0]), Integer.parseInt(p[1].trim()) - 1, Integer.parseInt(p[2].substring(0, 2)), 12, 0, 0);
        } catch (Exception ignore) { }
        return c;
    }

    /** لافاش + برو (بالأصفر) */
    static CharSequence brand(Context c) {
        SpannableStringBuilder sb = new SpannableStringBuilder(T.t("b1") + " ");
        int s = sb.length();
        sb.append(T.t("b2"));
        sb.setSpan(new ForegroundColorSpan(c.getColor(R.color.amber)), s, sb.length(), 0);
        return sb;
    }

    static TextView tv(Context c, CharSequence text, float sp, int color, boolean bold) {
        TextView t = new TextView(c);
        t.setText(text);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(t.getTypeface(), android.graphics.Typeface.BOLD);
        return t;
    }

    /** تأثير الضغط scale(.97) للأزرار. */
    @SuppressWarnings("ClickableViewAccessibility")
    static void press(View v) {
        v.setOnTouchListener((x, e) -> {
            int a = e.getActionMasked();
            if (a == MotionEvent.ACTION_DOWN) {
                x.animate().scaleX(.97f).scaleY(.97f).setDuration(80).start();
            } else if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) {
                x.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
            }
            return false;
        });
    }

    /** Toast أسفل الشاشة بنفس شكل الصفحة (يعمل فوق النافذة السفلية أيضا). */
    static void toast(Window w, String msg) {
        if (w == null) return;
        ViewGroup root = w.getDecorView().findViewById(android.R.id.content);
        if (root == null) return;
        Context c = root.getContext();
        View old = root.findViewWithTag("lp_toast");
        if (old != null) root.removeView(old);
        final TextView t = new TextView(c);
        t.setTag("lp_toast");
        t.setText(msg);
        t.setTextColor(c.getColor(R.color.bg));
        t.setTextSize(14);
        t.setGravity(Gravity.CENTER);
        t.setBackgroundResource(R.drawable.bg_toast);
        t.setPadding(dp(c, 22), dp(c, 11), dp(c, 22), dp(c, 11));
        t.setElevation(dp(c, 24));
        t.setMaxWidth((int) (c.getResources().getDisplayMetrics().widthPixels * 0.9f));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        lp.bottomMargin = dp(c, 100);
        root.addView(t, lp);
        final float off = dp(c, 300);
        t.setTranslationY(off);
        t.animate().translationY(0).setDuration(300).start();
        t.postDelayed(() -> t.animate().translationY(off).setDuration(300).withEndAction(() -> {
            if (t.getParent() instanceof ViewGroup) ((ViewGroup) t.getParent()).removeView(t);
        }).start(), 3000);
    }

    // ----- ألوان الحالة -----
    static int statusBase(String st) {
        switch (st == null ? "" : st) {
            case "confirmed": return 0xFF0D9488;
            case "done": return 0xFF64748B;
            case "cancelled": return 0xFFDC2626;
            default: return 0xFFD97706;
        }
    }

    static int statusText(Context c, String st) {
        switch (st == null ? "" : st) {
            case "confirmed": return c.getColor(R.color.aqua);
            case "done": return c.getColor(R.color.mut);
            case "cancelled": return c.getColor(R.color.danger);
            default: return c.getColor(R.color.pr);
        }
    }

    // ----- ألوان بطاقات المحلات (نفس المصفوفة G) -----
    private static final String[][] G = {
            {"#0f766e", "#0b2a33"}, {"#f59e0b", "#b45309"}, {"#6366f1", "#1e1b4b"},
            {"#10b981", "#064e3b"}, {"#ec4899", "#701a75"}, {"#0ea5e9", "#082f49"}};

    static int[] palette(String name) {
        int sum = 0;
        for (int i = 0; i < name.length(); ) {
            int cp = name.codePointAt(i);
            sum += Character.toChars(cp)[0];
            i += Character.charCount(cp);
        }
        String[] g = G[sum % G.length];
        return new int[]{Color.parseColor(g[0]), Color.parseColor(g[1])};
    }

    static String firstChar(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, s.offsetByCodePoints(0, 1));
    }

    /** روابط «حول التطبيق» و«الخصوصية» وحقوق النشر أسفل الصفحات. */
    static void footer(View f, final MainActivity a) {
        TextView ab = f.findViewById(R.id.fo_about);
        TextView pv = f.findViewById(R.id.fo_privacy);
        ab.setText(T.t("about"));
        pv.setText(T.t("privacy"));
        ab.setPaintFlags(ab.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        pv.setPaintFlags(pv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ab.setOnClickListener(v -> a.go("about"));
        pv.setOnClickListener(v -> a.go("privacy"));
        ((TextView) f.findViewById(R.id.fo_copy)).setText(
                "© 2026 " + T.t("b1") + " " + T.t("b2") + " · " + T.t("oss"));
    }
}
