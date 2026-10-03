package com.mysalonypro.salony;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** نافذة الحجز / التعديل (bottom sheet) — نفس sheet() في الصفحة. */
public class BookingSheet extends BottomSheetDialogFragment {

    static class State {
        Models.Shop shop;
        List<Models.Service> sv = new ArrayList<>();
        String veh = "سيارة", sid, date, time;
        Models.Appointment edit;
    }

    private static State S;

    static void open(FragmentManager fm, State st) {
        S = st;
        new BookingSheet().show(fm, "book");
    }

    private final List<View> vehViews = new ArrayList<>();
    private final List<View> svViews = new ArrayList<>();
    private final List<View> dayViews = new ArrayList<>();
    private final List<View> timeViews = new ArrayList<>();
    private final List<Models.Service> svOrder = new ArrayList<>();
    private TextView tvTotal;
    private EditText etName, etPhone, etNotes;
    private boolean busy;

    @Override
    public int getTheme() { return R.style.BookSheetDialog; }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle b) {
        final BottomSheetDialog d = (BottomSheetDialog) super.onCreateDialog(b);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.setOnShowListener(x -> {
            FrameLayout f = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (f != null) {
                BottomSheetBehavior<FrameLayout> bh = BottomSheetBehavior.from(f);
                bh.setMaxHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.93f));
                bh.setSkipCollapsed(true);
                bh.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return d;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        if (S == null) {
            dismissAllowingStateLoss();
            return new View(requireContext());
        }
        final Context ctx = requireContext();
        final Models.Appointment a = S.edit;
        if (S.date == null) S.date = Ui.iso(Calendar.getInstance());

        View root = inf.inflate(R.layout.sheet_booking, c, false);
        root.setLayoutDirection(T.isAr() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        ((TextView) root.findViewById(R.id.sheet_title)).setText(a != null ? T.t("editT") : T.t("bookT"));
        ((TextView) root.findViewById(R.id.sheet_sub)).setText(a != null ? a.shopName : S.shop.name);
        ((TextView) root.findViewById(R.id.lb_vehicle)).setText(T.t("vehicle"));
        ((TextView) root.findViewById(R.id.lb_service)).setText(T.t("service"));
        ((TextView) root.findViewById(R.id.lb_day)).setText(T.t("day"));
        ((TextView) root.findViewById(R.id.lb_time)).setText(T.t("time"));
        ((TextView) root.findViewById(R.id.lb_info)).setText(T.t("yourInfo"));
        ((TextView) root.findViewById(R.id.tv_total_label)).setText(T.t("total"));
        root.findViewById(R.id.btn_close).setOnClickListener(x -> dismiss());

        // ----- نوع المركبة -----
        GridLayout gv = root.findViewById(R.id.grid_vehicle);
        for (final String v : T.VEH) {
            View it = inf.inflate(R.layout.item_vehicle, gv, false);
            ((ImageView) it.findViewById(R.id.veh_icon)).setImageResource(T.vehIcon(v));
            ((TextView) it.findViewById(R.id.veh_text)).setText(T.vl(v));
            it.setTag(v);
            it.setOnClickListener(x -> { S.veh = v; refreshVeh(); });
            gv.addView(it, cell(ctx));
            vehViews.add(it);
        }
        refreshVeh();

        // ----- الخدمات (غير ظاهرة عند التعديل) -----
        View boxSvc = root.findViewById(R.id.box_service);
        if (a != null) {
            boxSvc.setVisibility(View.GONE);
        } else {
            fillServices(inf, (LinearLayout) root.findViewById(R.id.services));
        }

        // ----- الأيام -----
        LinearLayout days = root.findViewById(R.id.days);
        List<Calendar> ds = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Calendar d = Calendar.getInstance();
            d.add(Calendar.DAY_OF_MONTH, i);
            ds.add(d);
        }
        if (a != null) {
            boolean has = false;
            for (Calendar d : ds) if (Ui.iso(d).equals(a.date)) has = true;
            if (!has) ds.add(0, Ui.parse(a.date));
        }
        SimpleDateFormat wd = new SimpleDateFormat("EEE", Ui.loc());
        for (Calendar d : ds) {
            final String iso = Ui.iso(d);
            View it = inf.inflate(R.layout.item_day, days, false);
            ((TextView) it.findViewById(R.id.day_name)).setText(wd.format(d.getTime()));
            ((TextView) it.findViewById(R.id.day_num)).setText(String.valueOf(d.get(Calendar.DAY_OF_MONTH)));
            it.setTag(iso);
            it.setSelected(iso.equals(S.date));
            it.setOnClickListener(x -> { S.date = iso; mark(dayViews, iso); });
            days.addView(it);
            dayViews.add(it);
        }

        // ----- الساعات 09:00 → 19:00 -----
        GridLayout gt = root.findViewById(R.id.grid_time);
        List<String> hrs = new ArrayList<>();
        for (int i = 0; i < 11; i++) hrs.add(String.format(java.util.Locale.US, "%02d:00", 9 + i));
        if (a != null && !hrs.contains(a.time)) {
            hrs.add(a.time);
            Collections.sort(hrs);
        }
        for (final String h : hrs) {
            TextView t = new TextView(ctx);
            t.setText(h);
            t.setTextSize(13.6f);
            t.setGravity(Gravity.CENTER);
            t.setPadding(0, Ui.dp(ctx, 8), 0, Ui.dp(ctx, 8));
            t.setBackground(ContextCompat.getDrawable(ctx, R.drawable.bg_choice12));
            t.setTextColor(ctx.getColorStateList(R.color.sel_ink_mut));
            t.setClickable(true);
            t.setFocusable(true);
            t.setTag(h);
            t.setSelected(h.equals(S.time));
            t.setOnClickListener(x -> { S.time = h; mark(timeViews, h); });
            gt.addView(t, cell(ctx));
            timeViews.add(t);
        }

        // ----- بياناتك -----
        String[] prof = Store.profile(ctx);
        etName = root.findViewById(R.id.et_name);
        etPhone = root.findViewById(R.id.et_phone);
        etNotes = root.findViewById(R.id.et_notes);
        etName.setHint(T.t("name"));
        etPhone.setHint(T.t("phone"));
        etNotes.setHint(T.t("notes"));
        etName.setText(a != null ? a.customerName : prof[0]);
        etPhone.setText(a != null ? a.phone : prof[1]);
        etNotes.setText(a != null ? a.notes : "");

        TextView note = root.findViewById(R.id.tv_edit_note);
        if (a != null) {
            note.setText(T.t("editNote"));
            note.setVisibility(View.VISIBLE);
        }

        tvTotal = root.findViewById(R.id.tv_total);
        tvTotal.setText(a != null ? a.price + " " + T.t("mad") : "—");

        TextView ok = root.findViewById(R.id.btn_ok);
        ok.setText(a != null ? T.t("saveEdit") : T.t("confirm"));
        Ui.press(ok);
        ok.setOnClickListener(x -> save());
        return root;
    }

    /** خلية متساوية العرض في GridLayout (4 أعمدة). */
    private GridLayout.LayoutParams cell(Context ctx) {
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED), GridLayout.spec(GridLayout.UNDEFINED, 1f));
        lp.width = 0;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int m = Ui.dp(ctx, 4);
        lp.setMargins(m, m, m, m);
        return lp;
    }

    private void mark(List<View> views, Object tag) {
        for (View v : views) v.setSelected(v.getTag().equals(tag));
    }

    private void refreshVeh() { mark(vehViews, S.veh); }

    private void fillServices(LayoutInflater inf, LinearLayout box) {
        final Context ctx = box.getContext();
        if (S.sv.isEmpty()) {
            TextView nt = Ui.tv(ctx, T.t("nosvc"), 13.1f, ctx.getColor(R.color.mut), false);
            nt.setPadding(0, Ui.dp(ctx, 6), 0, 0);
            box.addView(nt);
            return;
        }
        Map<String, List<Models.Service>> groups = new LinkedHashMap<>();
        for (Models.Service s : S.sv) {
            List<Models.Service> l = groups.get(s.category);
            if (l == null) { l = new ArrayList<>(); groups.put(s.category, l); }
            l.add(s);
        }
        for (Map.Entry<String, List<Models.Service>> e : groups.entrySet()) {
            TextView gl = Ui.tv(ctx, T.cl(e.getKey()), 12.5f, ctx.getColor(R.color.mut), false);
            LinearLayout.LayoutParams glp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            glp.topMargin = Ui.dp(ctx, 11);
            glp.bottomMargin = Ui.dp(ctx, 6);
            box.addView(gl, glp);
            for (final Models.Service s : e.getValue()) {
                View it = inf.inflate(R.layout.item_service, box, false);
                ((TextView) it.findViewById(R.id.sv_name)).setText(s.name);
                ((TextView) it.findViewById(R.id.sv_veh)).setText("الكل".equals(s.vehicle) ? T.t("allv") : T.vl(s.vehicle));
                ((TextView) it.findViewById(R.id.sv_price)).setText(s.price + " " + T.t("mad"));
                it.setTag(s.id);
                it.setOnClickListener(x -> {
                    S.sid = s.id;
                    mark(svViews, s.id);
                    tvTotal.setText(s.price + " " + T.t("mad"));
                    if (!"الكل".equals(s.vehicle)) { S.veh = s.vehicle; refreshVeh(); }
                });
                box.addView(it);
                svViews.add(it);
            }
        }
    }

    private void save() {
        if (busy) return;
        final String n = etName.getText().toString().trim();
        final String ph = etPhone.getText().toString().trim();
        final String nt = etNotes.getText().toString().trim();
        if (n.isEmpty() || ph.isEmpty() || S.time == null || (S.edit == null && S.sid == null)) {
            if (getDialog() != null) Ui.toast(getDialog().getWindow(), T.t("needs"));
            return;
        }
        final MainActivity act = (MainActivity) requireActivity();
        final boolean ed = S.edit != null;
        final String dev = Store.deviceId(act);
        JSONObject body = new JSONObject();
        try {
            body.put("deviceId", dev);
            body.put("name", n);
            body.put("phone", ph);
            body.put("vehicle", S.veh);
            body.put("date", S.date);
            body.put("time", S.time);
            body.put("notes", nt);
            if (!ed) {
                body.put("shopId", S.shop.id);
                body.put("serviceId", S.sid);
            }
        } catch (Exception ignore) { }

        busy = true;
        Api.Cb<Boolean> cb = new Api.Cb<Boolean>() {
            @Override public void ok(Boolean r) {
                Store.saveProfile(act, n, ph);
                dismissAllowingStateLoss();
                S = null;
                Ui.toast(act.getWindow(), T.t(ed ? "saved" : "sent"));
                act.go("mine");
            }

            @Override public void err(String m) {
                busy = false;
                if (getDialog() != null) Ui.toast(getDialog().getWindow(), m);
            }
        };
        if (ed) Api.updateAppt(S.edit.id, body, cb); else Api.createAppt(body, cb);
    }
}
