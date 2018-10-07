package com.anshmidt.easynote;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 30.09.2018.
 */

public class OneLineListEntrySelector extends RelativeLayout {

    private View rootView;
    private TextView valueView;
    private ImageButton upButton, downButton;
    private ArrayList<String> list;
    private int defaultEntryNumber;
    private int currentEntryNumber;

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    public OneLineListEntrySelector(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OneLineListEntrySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public OneLineListEntrySelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        init();
    }

    private void init() {
        rootView = inflate(context, R.layout.oneline_list_entry_selector, this);
        valueView = (TextView) rootView.findViewById(R.id.value_view);

        downButton = (ImageButton) rootView.findViewById(R.id.down_button);
        upButton = (ImageButton) rootView.findViewById(R.id.up_button);

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setDefaultEntryNumber(int defaultEntryNumber) {
        this.defaultEntryNumber = defaultEntryNumber;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public void setValue(int index) {
        String valueToSet = list.get(index);
        valueView.setText(valueToSet);
    }

    public String getValue() {
        return valueView.getText().toString();
    }
}
