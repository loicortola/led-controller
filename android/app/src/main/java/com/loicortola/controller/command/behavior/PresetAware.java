package com.loicortola.controller.command.behavior;

import com.loicortola.controller.model.Preset;

import java.util.List;

/**
 * Created by loic on 28/03/2016.
 */
public interface PresetAware {

    interface OnPresetChangedListener {
        void onPresetChanged(boolean success);
    }
    interface OnPresetLoadedListener {
        void onPresetLoaded(Preset p);
    }
    List<Preset> getPresets();
    void setPreset(Preset p, OnPresetChangedListener l);
    void getPreset(OnPresetLoadedListener l);

}
