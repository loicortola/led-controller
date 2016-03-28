package com.loicortola.controller.command;

import com.loicortola.controller.command.behavior.Switchable;

/**
 * Created by loic on 28/03/2016.
 */
public class SwitchCommand implements Command {

    private Switchable s;
    private Switchable.OnSwitchListener l;

    public SwitchCommand(Switchable s, Switchable.OnSwitchListener l) {
        this.s = s;
        this.l = l;
    }

    @Override
    public void execute() {
        s.isActive(new Switchable.OnSwitchListener() {
            @Override
            public void onActiveResult(Boolean isActive) {
                if (isActive) {
                    s.switchOff(l);
                } else {
                    s.switchOn(l);
                }
            }
        });
    }
}
