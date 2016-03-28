package com.loicortola.controller.command.behavior;

/**
 * Created by loic on 28/03/2016.
 */
public interface Secured {

    interface OnValidityCheckedListener {
        void onValidityChecked(boolean valid);
    }

    void isValid(String key, OnValidityCheckedListener l);
}
