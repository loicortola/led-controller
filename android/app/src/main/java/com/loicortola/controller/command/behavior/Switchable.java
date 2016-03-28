package com.loicortola.controller.command.behavior;

/**
 * Created by loic on 28/03/2016.
 */
public interface Switchable {

    interface OnSwitchListener {
        void onActiveResult(Boolean isActive);
    }

    void isActive(OnSwitchListener l);
    void switchOn(OnSwitchListener l);
    void switchOff(OnSwitchListener l);
}
