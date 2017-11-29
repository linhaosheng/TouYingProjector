package com.reeman.projector.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.TextView;

import com.reeman.projector.R;


public class WaitDialogUtil {
    Dialog waitDialog;
    Context context;
    View recdialog;
    String dialogTitle;
    boolean back;


    public WaitDialogUtil(Context context) {
        this.context = context;
        waitDialog = new Dialog(context, R.style.MyDialog);
        recdialog = View.inflate(context, R.layout.dialog_wait, null);
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(recdialog);
        waitDialog.setCanceledOnTouchOutside(false);
        waitDialog.setCancelable(true);
        waitDialog.setOnKeyListener(keyListener);
    }

    Dialog.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!back) {
                    return true;
                }
            }
            return false;
        }
    };


    public void showDialog(String text_dialog) {
        TextView tv = (TextView) recdialog.findViewById(R.id.tv_dialog_wait);
        if (text_dialog.length() < 6) {
            tv.setTextSize(18);
        } else {
            tv.setTextSize(14);
        }
        dismiss();
        tv.setText(text_dialog);
        waitDialog.show();
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
      /*  TextView tv = (TextView) recdialog.findViewById(R.id.tv_dialog_wait);
        tv.setText(dialogTitle);*/
    }

    public void showDialog() {
        TextView tv = (TextView) recdialog.findViewById(R.id.tv_dialog_wait);
        if (dialogTitle.length() < 6) {
            tv.setTextSize(18);
        } else {
            tv.setTextSize(14);
        }
        tv.setText(dialogTitle);
        if (!waitDialog.isShowing()) {
            waitDialog.show();
        }
    }

    public void dismiss() {
        if (waitDialog == null) {
            return;
        }
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (waitDialog.isShowing()) {
            return true;
        }
        return false;
    }

    public void setBack(boolean back) {
        this.back = back;
    }
}
