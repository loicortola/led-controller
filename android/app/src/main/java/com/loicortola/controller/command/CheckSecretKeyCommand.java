package com.loicortola.controller.command;

import com.loicortola.controller.command.behavior.Secured;

/**
 * Created by loic on 28/03/2016.
 */
public class CheckSecretKeyCommand implements Command {

    Secured s;
    String k;
    Secured.OnValidityCheckedListener l;

    public CheckSecretKeyCommand(Secured s, String key, Secured.OnValidityCheckedListener l) {
        this.s = s;
        this.k = key;
        this.l = l;
    }

    @Override
    public void execute() {
        s.isValid(k, l);
    }
}
