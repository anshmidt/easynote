package com.anshmidt.easynote;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Sigurd Sigurdsson on 11.09.2017.
 */

public class DeleteNoteTask  {
//public class DeleteNoteTask extends AsyncTask<Integer, Void, Void> {
//    private Context context;
//
//    public DeleteNoteTask(Context context) {
//        this.context = context;
//    }
//
//
////    @Override
////    protected void onPreExecute() {
////        super.onPreExecute();
////    }
//
//    @Override
//    protected Void doInBackground(Integer... params) {
//        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
//        //db = databaseHelper.getWritableDatabase();
//
//        databaseHelper.deleteNote(params[0]);
//        return null;
//    }
//
////    @Override
////    protected void onProgressUpdate() {
////        super.onProgressUpdate();
////        Log.d("GetAllNotesTask", "All Notes Are Loaded from database");
////
////    }
//
//    @Override
//    protected void onPostExecute(Void result) {
////        listener.onResultsSucceeded(notesList);
////        if (listener != null) {
////            listener.onTaskCompleted();
////        }
//        super.onPostExecute(result);
//        //delegate.processFinish(notesList);
//        Toast.makeText(context, "Note deleted from database", Toast.LENGTH_SHORT).show();
//        //Log.d("GetAllNotesTask", "All Notes Are Loaded from database");
//    }
}
