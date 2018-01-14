package com.anshmidt.easynote;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Sigurd Sigurdsson on 11.09.2017.
 */

public class GetAllNotesTask {
//public class GetAllNotesTask extends AsyncTask<Void, Void, List<Note>> {
//    private Context context;
////    ResultsListener listener;
//    //public ResultsListener delegate = null;
////    public interface OnTaskCompleted {
////        void onTaskCompleted();
////    }
//
////    private OnTaskCompleted listener;
//
//    public GetAllNotesTask(Context context) {
//        this.context = context;
//    }
//
////    public void setOnResultsListener(ResultsListener listener) {
////        this.listener = listener;
////    }
//
////    public GetAllNotesTask(Context context) {
////        context = context;
////    }
//
//
////    @Override
////    protected void onPreExecute() {
////        super.onPreExecute();
////    }
//
//    @Override
//    protected List<Note> doInBackground(Void... params) {
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
//        //db = databaseHelper.getWritableDatabase();
//
//        return databaseHelper.getAllNotes();
//    }
//
////    @Override
////    protected void onProgressUpdate() {
////        super.onProgressUpdate();
////        Log.d("GetAllNotesTask", "All Notes Are Loaded from database");
////
////    }
//
////    @Override
////    protected void onPostExecute(List<Note> notesList) {
//////        listener.onResultsSucceeded(notesList);
//////        if (listener != null) {
//////            listener.onTaskCompleted();
//////        }
////        //super.onPostExecute(notesList);
////        //delegate.processFinish(notesList);
////        //Toast.makeText(context, "All Notes Are Loaded from database", Toast.LENGTH_SHORT).show();
////        //Log.d("GetAllNotesTask", "All Notes Are Loaded from database");
////    }
}
