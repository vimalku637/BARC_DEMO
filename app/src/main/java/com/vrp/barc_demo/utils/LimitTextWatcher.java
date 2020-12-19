package com.vrp.barc_demo.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class LimitTextWatcher implements TextWatcher {

    public interface IF_callback{
        void callback(int left);
    }

    public IF_callback if_callback;

    EditText editText;
    int maxLength;

    int cursorPositionLast;
    String textLast;
    boolean bypass;

    public LimitTextWatcher(EditText editText, int maxLength, IF_callback if_callback) {

        this.editText = editText;
        this.maxLength = maxLength;
        this.if_callback = if_callback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        if (bypass) {

            bypass = false;

        } else {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(s);
            textLast = stringBuilder.toString();

            this.cursorPositionLast = editText.getSelectionStart();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() > maxLength) {

            int left = maxLength - s.toString().length();

            bypass = true;
            s.clear();

            bypass = true;
            s.append(textLast);

            editText.setSelection(this.cursorPositionLast);

            if (if_callback != null) {
                if_callback.callback(left);
            }
        }

    }
}

