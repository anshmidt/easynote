package com.anshmidt.easynote;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.anshmidt.easynote.database.NotesDao;

/**
 * Created by Ilya Anshmidt on 18.02.2018.
 */

public class KeyboardHelper {

    Context context;
    InputMethodManager imm;
    private final String LOG_TAG = KeyboardHelper.class.getSimpleName();

    public KeyboardHelper(Context context) {
        this.context = context;
        imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void showKeyboard(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean isFocused) {
                if (isFocused) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });
    }

    public void hideKeyboard(EditText editText) {
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void moveCursorToEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    public void showIfNotOpened(final View contentView, final EditText editText) {
        final double KEYBOARD_TO_SCREEN_HEIGHT_RATIO = 0.15;

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                contentView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * KEYBOARD_TO_SCREEN_HEIGHT_RATIO) {
                    Log.d(LOG_TAG, "Keyboard is opened");
                } else {
                    showKeyboard(editText);
                    Log.d(LOG_TAG, "Keyboard is not opened");
                }
            }
        });
    }
}
