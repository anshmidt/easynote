package com.anshmidt.easynote;

import org.junit.Test;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ImportListTest {
    NotesConverter notesConverter = new NotesConverter();

    @Test
    public void importList1() {
        String textFromClipboard = "first\nsecond\nthird\nfourth";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }

    @Test
    public void importList2() {
        String textFromClipboard = "- First\n-Second\n- Third";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }

    @Test
    public void importList3() {
        String textFromClipboard = "1) Large 2) Middle 3)Small";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }

    @Test
    public void importList4() {
        String textFromClipboard = "first, second item,third item, fourth";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }

    @Test
    public void importList5() {
        String textFromClipboard = "a)first with a, B)Second \n c) Third";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }

    @Test
    public void importList6() {
        String textFromClipboard = "1.First, \n 2. Second 3.Third";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }

    @Test
    public void importList7() {
        String textFromClipboard = "Just simple plain text";
        printListOfNoteText(notesConverter.stringToList(textFromClipboard));
    }



    public static void printListOfNoteText(List<String> listOfNoteText) {
        for (String noteText : listOfNoteText) {
            System.out.println(noteText);
            System.out.println("-------------");
        }
        System.out.println("\n");
    }
}