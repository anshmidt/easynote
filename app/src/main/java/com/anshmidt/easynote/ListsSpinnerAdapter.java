package com.anshmidt.easynote;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 17.02.2018.
 */

public class ListsSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    public List<String> arrayList;
    public static String ADD_NEW_LIST_LABEL;

    public ListsSpinnerAdapter(Context context, int textViewResourceId,
                               List<String> arrayList) {
        super(context, textViewResourceId, addCreateListItem(arrayList, context));
        this.context = context;
        this.arrayList = arrayList;


    }


    private static List<String> addCreateListItem(List<String> arrayList, Context context) {
        ADD_NEW_LIST_LABEL = context.getString(R.string.add_new_list_label);
        arrayList.add(ADD_NEW_LIST_LABEL);
        return arrayList;
    }

    @Override
    public View getDropDownView(int position, final View convertView,
                                ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;
        if (arrayList.get(position).equals(ADD_NEW_LIST_LABEL)) {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryDark));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "Add list clicked", Toast.LENGTH_LONG).show();
                    RenameListDialogFragment dialog = new RenameListDialogFragment();
//                    Bundle numberOfAlarmsBundle = new Bundle();
//                    numberOfAlarmsBundle.putString("number_of_alarms", sharPrefHelper.getNumberOfAlarmsStr());
//                    dialog.setArguments(numberOfAlarmsBundle);
                    FragmentManager manager = ((Activity) context).getFragmentManager();
                    dialog.show(manager, "renameListDialog");

                }
            });
        } else {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            textView.setTypeface(null, Typeface.BOLD);
        }
        return view;

    }

    @Override
    public boolean isEnabled(int position) {
        if (arrayList.get(position).equals(ADD_NEW_LIST_LABEL)) {
            return false;
        } else {
            return true;
        }

    }

//    public void setDropDownViewResource(int resource) {
//        arrayAdapter.setDropDownViewResource(resource);
//    }



//    public View getCustomView(int position, View convertView, ViewGroup parent) {
//
//
////        LayoutInflater inflater = getLayoutInflater();
////        View row=inflater.inflate(R.layout.row, parent, false);
////        TextView label=(TextView)row.findViewById(R.id.weekofday);
////        label.setText(DayOfWeek[position]);
////
////        ImageView icon=(ImageView)row.findViewById(R.id.icon);
////
////        if (DayOfWeek[position]=="Sunday"){
////            icon.setImageResource(R.drawable.icon);
////        }
////        else{
////            icon.setImageResource(R.drawable.icongray);
////        }
////
//        //return row;
//        return null;
//    }


}
