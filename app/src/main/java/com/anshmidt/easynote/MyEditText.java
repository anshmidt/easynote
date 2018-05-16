package com.anshmidt.easynote;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;



/**
 * Created by Ilya Anshmidt on 08.03.2018.
 */

public class MyEditText extends AppCompatEditText {
//    private boolean mEnabled; // is this edittext enabled

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        try {
//            if (!mEnabled) return;
//            super.setEnabled(false);
//            super.setEnabled(mEnabled);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void setEnabled(boolean enabled) {
//        this.mEnabled = enabled;
//        super.setEnabled(enabled);
//    }
}
