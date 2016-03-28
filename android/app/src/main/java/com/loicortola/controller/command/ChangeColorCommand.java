package com.loicortola.controller.command;

import android.graphics.Color;

import com.loicortola.controller.command.behavior.Colorable;

/**
 * Created by loic on 28/03/2016.
 */
public class ChangeColorCommand implements Command {

    private Colorable c;
    private int t;
    private Colorable.OnColorSetListener l;

    public ChangeColorCommand(Colorable c, int target, Colorable.OnColorSetListener l) {
        this.c = c;
        this.t = target;
        this.l = l;
    }

    @Override
    public void execute() {
        c.setColor(t, l);
    }
}
