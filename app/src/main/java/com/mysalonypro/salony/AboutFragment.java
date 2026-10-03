package com.mysalonypro.salony;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_about, c, false);
        ((TextView) v.findViewById(R.id.ab_brand)).setText(Ui.brand(requireContext()));
        ((TextView) v.findViewById(R.id.ab_h3)).setText(T.t("about"));
        ((TextView) v.findViewById(R.id.ab_p)).setText(T.t("aboutP"));
        LinearLayout ul = v.findViewById(R.id.ab_list);
        for (String x : T.aboutL()) {
            TextView t = Ui.tv(requireContext(), "•  " + x, 14.4f, requireContext().getColor(R.color.mut), false);
            t.setPadding(Ui.dp(requireContext(), 4), Ui.dp(requireContext(), 2), 0, Ui.dp(requireContext(), 2));
            ul.addView(t);
        }
        Ui.footer(v.findViewById(R.id.footer), (MainActivity) requireActivity());
        return v;
    }
}
