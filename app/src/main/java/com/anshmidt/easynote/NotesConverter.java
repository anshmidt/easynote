package com.anshmidt.easynote;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ilya Anshmidt on 07.03.2018.
 */

public class NotesConverter {

    public String notesOfOneListToString(ArrayList<Note> notes) {
        StringBuilder resultStringBuilder = new StringBuilder();
        String listName = notes.get(0).list.name;
        resultStringBuilder.append(listName + ": \n");
        int lineNumber = 1;

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            resultStringBuilder.append(lineNumber);
            resultStringBuilder.append(") ");
            resultStringBuilder.append(note.text);
            resultStringBuilder.append("\n");
            lineNumber++;
        }
        return resultStringBuilder.toString();
    }

    public List<Note> stringToListOfNotes(String textWithDelimiters, Context context, NotesList notesList) {
        List<String> listOfStrings = stringToList(textWithDelimiters);
        List<Note> listOfNotes = new ArrayList<>();
        for (String noteText : listOfStrings) {
            Note note = new Note(noteText, context);
            note.list = notesList;
            listOfNotes.add(note);
        }
        return listOfNotes;
    }

    public List<String> stringToList(String textWithDelimiters) {
        //possible delimiters:
        // -
        // 1)
        // 1.
        // b)
        // \n
        // ,   (if no other delimiters found)

        if ( (textWithDelimiters.contains("1)")) && (textWithDelimiters.contains("2)"))) {
            //delimiters type "7)"
            textWithDelimiters = textWithDelimiters.replace("\n", "");
            textWithDelimiters = textWithDelimiters.replace(",", "");
            String regex = "\\d\\)";
            List<String> lines = Arrays.asList(textWithDelimiters.split(regex));
            return refineLines(lines);
        }

        if ( (textWithDelimiters.toLowerCase().contains("a)")) && (textWithDelimiters.toLowerCase().contains("b"))) {
            //delimiters type "c)"
            textWithDelimiters = textWithDelimiters.replace("\n", "");
            textWithDelimiters = textWithDelimiters.replace(",", "");
            String regex = "[a-zA-Z]\\)";
            List<String> lines = Arrays.asList(textWithDelimiters.split(regex));
            return refineLines(lines);
        }

        if ( (textWithDelimiters.contains("1.")) && (textWithDelimiters.contains("2."))) {
            //delimiters type "1."
            textWithDelimiters = textWithDelimiters.replace("\n", "");
            textWithDelimiters = textWithDelimiters.replace(",", "");
            String regex = "\\d\\.";
            List<String> lines = Arrays.asList(textWithDelimiters.split(regex));
            return refineLines(lines);
        }

        if ( (textWithDelimiters.startsWith("-")) && (textWithDelimiters.contains("\n-"))) {
            //delimiters type "-" with \n
            textWithDelimiters = textWithDelimiters.replace(",", "");
            textWithDelimiters = textWithDelimiters.substring(1);
            String regex = "\n-";
            List<String> lines = Arrays.asList(textWithDelimiters.split(regex));
            return refineLines(lines);
        }

        if (textWithDelimiters.contains("\n")) {
            //delimiters type "\n"
            List<String> lines = Arrays.asList(textWithDelimiters.split("\n"));
            return refineLines(lines);
        } else {
            //probably delimiters are ","?
            if (textWithDelimiters.contains(",")) {
                List<String> lines = Arrays.asList(textWithDelimiters.split(","));
                return refineLines(lines);
            } else {
                ArrayList<String> oneItemArray = new ArrayList<>();
                oneItemArray.add(textWithDelimiters);
                return oneItemArray;
            }
        }
    }

    private List<String> refineLines(List<String> lines) { //remove not needed spaces, etc.
        ArrayList<String> refinedLines = new ArrayList<>();

        for (String line : lines) {
            String refinedLine = line.trim();
            if (refinedLine.length() > 0) {
                refinedLines.add(refinedLine);
            }
        }
        return refinedLines;
    }
}
