package com.loicortola.controller.command.behavior;

/**
 * Created by loic on 28/03/2016.
 */
public interface Colorable {

    interface OnColorChangedListener {
        void onColorSet(boolean success);
    }
    interface OnColorGetListener {
        void onColorGet(Integer c);
    }

    void changeColor(int c, OnColorChangedListener l);
    void setColor(int c, OnColorChangedListener l);
    void getColor(OnColorGetListener l);

}
