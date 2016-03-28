package com.loicortola.controller.command;

import com.loicortola.controller.command.behavior.Animable;
import com.loicortola.controller.model.Animation;

import java.util.concurrent.Future;

/**
 * Created by loic on 28/03/2016.
 */
public class AnimateCommand implements Command {

    private Animable a;
    private Animation t;
    private Animable.OnAnimationSetListener l;

    public AnimateCommand(Animable a, Animation target, Animable.OnAnimationSetListener l) {
        this.a = a;
        this.t = target;
        this.l = l;
    }

    @Override
    public void execute() {
        a.animate(t, l);
    }

}
