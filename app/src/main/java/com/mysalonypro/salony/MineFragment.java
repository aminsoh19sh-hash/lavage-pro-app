package com.mysalonypro.salony;

import android.animation.ObjectAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/** مواعيدي: قائمة المواعيد + تعديل + حذف. */
public class MineFragment extends Fragment {
    private LinearLayout list;
    private LayoutInflater inf;
    private final List<ObjectAnimator> pulses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        inf = inflater;
        View v = inflater.inflate(R.layout.fragment_mine, c, false);
        ((TextView) v.findViewById(R.id.sec_mine)).setText(T.t("mineT"));
        list = v.findViewById(R.id.list);
        load();
        return v;
    }

    @Override
    public void onDestroyView() {
        stopPulses();
        super.onDestroyView();
    }

    private void stopPulses() {
        for (ObjectAnimator a : pulses) a.cancel();
        pulses.clear();
    }

    private void load() {
        stopPulses();
        list.removeAllViews();
        for (int i = 0; i < 2; i++) {
            View sk = inf.inflate(R.layout.item_skeleton, list, false);
            sk.getLayoutParams().height = Ui.dp(requireContext(), 110);
            list.addView(sk);
            ObjectAnimator a = ObjectAnimator.ofFloat(sk, View.ALPHA, 1f, .45f);
            a.setDuration(650);
            a.setRepeatMode(ObjectAnimator.REVERSE);
            a.setRepeatCount(ObjectAnimator.INFINITE);
            a.start();
            pulses.add(a);
        }
        Api.appointments(Store.deviceId(requireContext()), new Api.Cb<List<Models.Appointment>>() {
            @Override public void ok(List<Models.Appointment> r) {
                if (!isAdded()) return;
                Collections.sort(r, (a, b) -> (b.date + b.time).compareTo(a.date + a.time));
                stopPulses();
                list.removeAllViews();
                if (r.isEmpty()) { emptyState(); return; }
                for (Models.Appointment a : r) list.addView(card(a));
            }

            @Override public void err(String m) {
                if (!isAdded()) return;
                stopPulses();
                list.removeAllViews();
                View e = inf.inflate(R.layout.item_empty, list, false);
                ((TextView) e.findViewById(R.id.empty_text)).setText(T.t("fail"));
                list.addView(e);
            }
        });
    }

    private void emptyState() {
        View e = inf.inflate(R.layout.item_empty, list, false);
        e.findViewById(R.id.empty_box).setVisibility(View.VISIBLE);
        TextView t = e.findViewById(R.id.empty_title);
        t.setVisibility(View.VISIBLE);
        t.setText(T.t("noap"));
        ((TextView) e.findViewById(R.id.empty_text)).setText(T.t("noapS"));
        TextView cta = e.findViewById(R.id.empty_cta);
        cta.setVisibility(View.VISIBLE);
        cta.setText(T.t("browse"));
        Ui.press(cta);
        cta.setOnClickListener(x -> ((MainActivity) requireActivity()).go("home"));
        list.addView(e);
    }

    private View card(final Models.Appointment a) {
        View v = inf.inflate(R.layout.item_appointment, list, false);
        Calendar d = Ui.parse(a.date);
        ((TextView) v.findViewById(R.id.dt_day)).setText(String.valueOf(d.get(Calendar.DAY_OF_MONTH)));
        ((TextView) v.findViewById(R.id.dt_month)).setText(new SimpleDateFormat("MMM", Ui.loc()).format(d.getTime()));
        int base = Ui.statusBase(a.status);
        v.findViewById(R.id.dt).setBackgroundColor(base);

        ((TextView) v.findViewById(R.id.shop_name)).setText(a.shopName);
        TextView st = v.findViewById(R.id.status);
        String key = "st_" + a.status;
        String label = T.t(key);
        st.setText(label.equals(key) ? a.status : label);
        st.setTextColor(Ui.statusText(requireContext(), a.status));
        GradientDrawable pill = new GradientDrawable();
        pill.setCornerRadius(Ui.dp(requireContext(), 99));
        pill.setColor((base & 0x00FFFFFF) | 0x22000000);
        st.setBackground(pill);
        st.setPadding(Ui.dp(requireContext(), 10), Ui.dp(requireContext(), 1), Ui.dp(requireContext(), 10), Ui.dp(requireContext(), 1));

        ((TextView) v.findViewById(R.id.svc_line)).setText(a.serviceName + " · " + T.vl(a.vehicle));
        ((TextView) v.findViewById(R.id.time_line)).setText(a.time);
        ((TextView) v.findViewById(R.id.price)).setText(a.price + " " + T.t("mad"));

        View ed = v.findViewById(R.id.btn_edit);
        if ("done".equals(a.status)) ed.setVisibility(View.GONE);
        ed.setOnClickListener(x -> edit(a));
        v.findViewById(R.id.btn_del).setOnClickListener(x -> confirmDelete(a));
        return v;
    }

    private void edit(Models.Appointment a) {
        BookingSheet.State st = new BookingSheet.State();
        st.edit = a;
        st.veh = a.vehicle;
        st.date = a.date;
        st.time = a.time;
        BookingSheet.open(requireActivity().getSupportFragmentManager(), st);
    }

    private void confirmDelete(final Models.Appointment a) {
        new MaterialAlertDialogBuilder(requireContext())
                .setMessage(T.t("delQ"))
                .setPositiveButton(T.t("yes"), (dlg, w) -> Api.deleteAppt(a.id, Store.deviceId(requireContext()), new Api.Cb<Boolean>() {
                    @Override public void ok(Boolean r) {
                        if (!isAdded()) return;
                        Ui.toast(requireActivity().getWindow(), T.t("deleted"));
                        load();
                    }

                    @Override public void err(String m) {
                        if (isAdded()) Ui.toast(requireActivity().getWindow(), m);
                    }
                }))
                .setNegativeButton(T.t("no"), null)
                .show();
    }
}
