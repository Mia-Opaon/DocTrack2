package com.triadss.doctrack2.helper;
import android.view.View;
import android.widget.Button;

public class ButtonManager {
    public static void disableButton(Button button) {
        button.setEnabled(false);
        button.setAlpha(0.5f);
    }

    public static void enableButton(Button button) {
        button.setEnabled(true);
        button.setAlpha(1f);
    }

    public static void toggleButton(Button button) {
        if (button.isEnabled()) {
            disableButton(button);
        } else {
            enableButton(button);
        }
    }

    public static void setOnClickListener(Button button, View.OnClickListener onClickListener) {
        button.setOnClickListener(onClickListener);
    }

    public static void setText(Button button, String text) {
        button.setText(text);
    }

    public static String getText(Button button) {
        return button.getText().toString();
    }

    public static void hideButton(Button button) {
        button.setVisibility(View.GONE);
    }

    public static void showButton(Button button) {
        button.setVisibility(View.VISIBLE);
    }
}
