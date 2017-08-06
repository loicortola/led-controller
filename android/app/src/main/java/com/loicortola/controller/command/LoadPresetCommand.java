package com.loicortola.controller.command;

import com.loicortola.controller.command.behavior.Colorable;
import com.loicortola.controller.command.behavior.PresetAware;
import com.loicortola.controller.model.Preset;

/**
 * Created by loic on 28/03/2016.
 */
public class LoadPresetCommand implements Command {

    private PresetAware p;
    private Preset t;
    private boolean hover;
    private PresetAware.OnPresetChangedListener l;

    public LoadPresetCommand(PresetAware p, Preset target, PresetAware.OnPresetChangedListener l) {
        this.p = p;
        this.t = target;
        this.l = l;
    }

    @Override
    public void execute() {
        p.setPreset(t, l);
    }
}
