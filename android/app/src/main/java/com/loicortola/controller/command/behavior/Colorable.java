package com.loicortola.controller.command.behavior;

import android.graphics.Color;

/**
 * Created by loic on 28/03/2016.
 */
public interface Colorable {

    interface OnColorSetListener {
        void onColorSet(boolean success);
    }
    interface OnColorGetListener {
        void onColorGet(Integer c);
    }

    void setColor(int c, OnColorSetListener l);
    void getColor(OnColorGetListener l);

}
