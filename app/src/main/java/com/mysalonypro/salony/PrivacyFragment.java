package com.mysalonypro.salony;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PrivacyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_privacy, c, false);
        Context ctx = requireContext();
        ((TextView) v.findViewById(R.id.pv_title)).setText(T.t("privT"));
        ((TextView) v.findViewById(R.id.pv_upd)).setText(T.t("privU"));
        LinearLayout box = v.findViewById(R.id.pv_box);
        for (String[] sec : T.priv()) {
            TextView h = Ui.tv(ctx, sec[0], 15.7f, ctx.getColor(R.color.ink), true);
            LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            hp.topMargin = Ui.dp(ctx, 18);
            hp.bottomMargin = Ui.dp(ctx, 4);
            box.addView(h, hp);
            TextView p = Ui.tv(ctx, sec[1], 14.4f, ctx.getColor(R.color.mut), false);
            p.setLineSpacing(0, 1.15f);
            box.addView(p);
        }
        Ui.footer(v.findViewById(R.id.footer), (MainActivity) requireActivity());
        return v;
    }
}
