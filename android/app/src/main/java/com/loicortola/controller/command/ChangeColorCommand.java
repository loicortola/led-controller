package com.loicortola.controller.command;

import com.loicortola.controller.command.behavior.Colorable;

/**
 * Created by loic on 28/03/2016.
 */
public class ChangeColorCommand implements Command {

    private Colorable c;
    private int t;
    private boolean hover;
    private Colorable.OnColorChangedListener l;

    public ChangeColorCommand(Colorable c, int target, Colorable.OnColorChangedListener l) {
        this.c = c;
        this.t = target;
        this.l = l;
    }

    public ChangeColorCommand(Colorable c, int target, boolean hover, Colorable.OnColorChangedListener l) {
        this.c = c;
        this.t = target;
        this.l = l;
        this.hover = hover;
    }

    @Override
    public void execute() {
        if (hover) {
            c.changeColor(t, l);
        } else {
            c.setColor(t, l);
        }
    }
}
