package com.mysalonypro.salony;

import android.animation.ObjectAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** الرئيسية: Hero + بحث + الخطوات + قائمة المحلات. */
public class HomeFragment extends Fragment {
    private LinearLayout box;
    private LayoutInflater inf;
    private final List<Models.Shop> shops = new ArrayList<>();
    private final List<ObjectAnimator> pulses = new ArrayList<>();
    private boolean failed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        inf = inflater;
        View v = inflater.inflate(R.layout.fragment_home, c, false);
        ((TextView) v.findViewById(R.id.hero_t)).setText(T.t("heroT"));
        ((TextView) v.findViewById(R.id.hero_s)).setText(T.t("heroS"));
        ((TextView) v.findViewById(R.id.sec_shops)).setText(T.t("shops"));

        EditText q = v.findViewById(R.id.q);
        q.setHint(T.t("search"));
        q.setText(Store.query);
        q.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence a, int b, int c2, int d) { }
            @Override public void onTextChanged(CharSequence a, int b, int c2, int d) { }
            @Override public void afterTextChanged(Editable e) {
                Store.query = e.toString();
                renderList();
            }
        });

        step(v.findViewById(R.id.step1), R.drawable.ic_pin, T.t("s1"));
        step(v.findViewById(R.id.step2), R.drawable.ic_spark, T.t("s2"));
        step(v.findViewById(R.id.step3), R.drawable.ic_check, T.t("s3"));

        box = v.findViewById(R.id.shops);
        Ui.footer(v.findViewById(R.id.footer), (MainActivity) requireActivity());

        skeleton();
        Api.shops(new Api.Cb<List<Models.Shop>>() {
            @Override public void ok(List<Models.Shop> r) {
                if (!isAdded()) return;
                shops.clear();
                shops.addAll(r);
                failed = false;
                renderList();
            }

            @Override public void err(String m) {
                if (!isAdded()) return;
                failed = true;
                renderList();
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        stopPulses();
        super.onDestroyView();
    }

    private void step(View s, int icon, String text) {
        ((ImageView) s.findViewById(R.id.step_icon)).setImageResource(icon);
        ((TextView) s.findViewById(R.id.step_text)).setText(text);
    }

    private void stopPulses() {
        for (ObjectAnimator a : pulses) a.cancel();
        pulses.clear();
    }

    private void skeleton() {
        box.removeAllViews();
        for (int i = 0; i < 2; i++) {
            View sk = inf.inflate(R.layout.item_skeleton, box, false);
            box.addView(sk);
            ObjectAnimator a = ObjectAnimator.ofFloat(sk, View.ALPHA, 1f, .45f);
            a.setDuration(650);
            a.setRepeatMode(ObjectAnimator.REVERSE);
            a.setRepeatCount(ObjectAnimator.INFINITE);
            a.start();
            pulses.add(a);
        }
    }

    private void empty(String text) {
        View e = inf.inflate(R.layout.item_empty, box, false);
        ((TextView) e.findViewById(R.id.empty_text)).setText(text);
        box.addView(e);
    }

    private void renderList() {
        if (box == null) return;
        stopPulses();
        box.removeAllViews();
        if (failed) { empty(T.t("fail")); return; }
        String q = Store.query.toLowerCase(Locale.ROOT);
        int n = 0;
        for (Models.Shop s : shops) {
            if ((s.name + " " + s.city).toLowerCase(Locale.ROOT).contains(q)) {
                box.addView(card(s));
                n++;
            }
        }
        if (n == 0) empty(T.t("nores"));
    }

    private View card(final Models.Shop s) {
        View v = inf.inflate(R.layout.item_shop, box, false);
        int[] g = Ui.palette(s.name);
        v.findViewById(R.id.cover).setBackground(
                new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{g[0], g[1]}));
        ((TextView) v.findViewById(R.id.city)).setText(s.city);
        TextView logo = v.findViewById(R.id.logo_letter);
        logo.setText(Ui.firstChar(s.name));
        logo.setTextColor(g[0]);
        ((TextView) v.findViewById(R.id.name)).setText(s.name);

        TextView ds = v.findViewById(R.id.desc);
        if (s.description.isEmpty()) ds.setVisibility(View.GONE); else ds.setText(s.description);

        FlexboxLayout meta = v.findViewById(R.id.meta);
        meta(meta, R.drawable.ic_clock, s.hours);
        meta(meta, R.drawable.ic_pin, s.address);
        meta(meta, R.drawable.ic_phone, s.phone);
        if (meta.getChildCount() == 0) meta.setVisibility(View.GONE);

        FlexboxLayout fs = v.findViewById(R.id.features);
        for (String f : s.features) {
            View chip = inf.inflate(R.layout.item_chip, fs, false);
            ((TextView) chip.findViewById(R.id.chip_text)).setText(f);
            fs.addView(chip);
        }
        if (s.features.isEmpty()) fs.setVisibility(View.GONE);

        ((TextView) v.findViewById(R.id.svc_count)).setText(T.svcN(s.services));
        TextView bk = v.findViewById(R.id.btn_book);
        bk.setText(T.t("book"));
        Ui.press(bk);
        bk.setOnClickListener(x -> openBook(s));
        return v;
    }

    private void meta(FlexboxLayout parent, int icon, String text) {
        if (text == null || text.isEmpty()) return;
        View m = inf.inflate(R.layout.item_meta, parent, false);
        ((ImageView) m.findViewById(R.id.meta_icon)).setImageResource(icon);
        ((TextView) m.findViewById(R.id.meta_text)).setText(text);
        parent.addView(m);
    }

    private void openBook(final Models.Shop s) {
        Api.services(s.id, new Api.Cb<List<Models.Service>>() {
            @Override public void ok(List<Models.Service> r) {
                if (!isAdded()) return;
                BookingSheet.State st = new BookingSheet.State();
                st.shop = s;
                st.sv = r;
                BookingSheet.open(requireActivity().getSupportFragmentManager(), st);
            }

            @Override public void err(String m) {
                if (isAdded()) Ui.toast(requireActivity().getWindow(), m);
            }
        });
    }
}
