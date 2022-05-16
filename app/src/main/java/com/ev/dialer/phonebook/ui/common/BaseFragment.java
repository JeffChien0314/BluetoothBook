package com.ev.dialer.phonebook.ui.common;

import android.app.Dialog;

import com.ev.dialer.phonebook.R;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private Dialog dialog;

    protected void hideLoading() {
        if (getContext()!= null /*&& !getActivity().isFinishing()*/) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }

    protected void showLoading() {
         if (dialog == null || !dialog.isShowing()) {
            dialog = new Dialog(getContext(), R.style.TransparentDialog);
            dialog.setContentView(R.layout.layout_data_loading_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            if (getContext()!= null /*&& !getActivity().isFinishing()*/) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        }
    }

}
