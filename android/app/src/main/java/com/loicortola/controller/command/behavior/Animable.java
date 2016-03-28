package com.loicortola.controller.command.behavior;

import com.loicortola.controller.model.Animation;

/**
 * Created by loic on 28/03/2016.
 */
public interface Animable {

    interface OnAnimationSetListener {
        void onAnimationSet(boolean success);
    }

    void animate(Animation a, OnAnimationSetListener l);

}
