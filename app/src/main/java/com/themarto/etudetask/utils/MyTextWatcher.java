package com.themarto.etudetask.utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class MyTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
        // nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){
        // nothing
    }

    @Override
    public abstract void afterTextChanged(Editable s);
}
