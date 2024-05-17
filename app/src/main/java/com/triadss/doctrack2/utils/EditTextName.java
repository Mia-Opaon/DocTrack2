package com.triadss.doctrack2.utils;

import android.widget.EditText;

public class EditTextName {
    private EditText editText;
    private String name;
    public EditTextName(String name, EditText editText)
    {
        this.name = name;
        this.editText = editText;
    }

    public EditText getEditText() {
        return editText;
    }

    public String getName() {
        return name;
    }
}
