package com.anshmidt.easynote;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Ilya Anshmidt on 10.02.2018.
 */

public class Priority {
    public String name;
    public int id;

    public Priority(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public Priority(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof Priority)) {
            return false;
        }
        Priority priority2 = (Priority) obj;

        if ((priority2.id == this.id) && (priority2.name.equals(this.name))) {
            return true;
        } else {
            return false;
        }
    }
}
