package com.anshmidt.easynote.list_names_spinner;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anshmidt.easynote.R;
import com.anshmidt.easynote.SharedPreferencesHelper;
import com.anshmidt.easynote.dialogs.RenameListDialogFragment;

import java.util.List;

/**
 * Created by Ilya Anshmidt on 17.02.2018.
 */

public class ListNamesSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private SharedPreferencesHelper sharPrefHelper;
    public List<String> listsList;
    public static String ADD_NEW_LIST_LABEL;

    public ListNamesSpinnerAdapter(Context context, int textViewResourceId,
                                   List<String> listsList) {
        super(context, textViewResourceId, addCreateListItem(listsList, context));
        this.context = context;
        this.listsList = listsList;
        this.sharPrefHelper = new SharedPreferencesHelper(context);

    }


    private static List<String> addCreateListItem(List<String> arrayList, Context context) {
        ADD_NEW_LIST_LABEL = context.getString(R.string.add_new_list_label);
        if (! arrayList.contains(ADD_NEW_LIST_LABEL)) {
            arrayList.add(ADD_NEW_LIST_LABEL);
        } else {
            if (arrayList.indexOf(ADD_NEW_LIST_LABEL) < arrayList.size() - 1) {  //if this item not last, make it last
                arrayList.remove(ADD_NEW_LIST_LABEL);
                arrayList.add(ADD_NEW_LIST_LABEL);
            }
        }
        return arrayList;
    }

    @Override
    public View getDropDownView(int position, final View convertView,
                                ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        final TextView textView = (TextView) view;
        if (listsList.get(position).equals(ADD_NEW_LIST_LABEL)) {
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryDark));
//            textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    RenameListDialogFragment renameListDialogFragment = new RenameListDialogFragment();
//                    FragmentManager manager = ((Activity) context).getFragmentManager();
//                    renameListDialogFragment.show(manager, renameListDialogFragment.FRAGMENT_TAG);
//                }
//            });
        } else {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            textView.setTypeface(null, Typeface.BOLD);
        }
        return view;

    }

    @Override
    public boolean isEnabled(int position) {
        return true;
//        if (listsList.get(position).equals(ADD_NEW_LIST_LABEL)) {
//            return false;
//        } else {
//            return true;
//        }

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
