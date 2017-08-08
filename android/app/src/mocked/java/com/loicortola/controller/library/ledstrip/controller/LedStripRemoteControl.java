package com.loicortola.controller.library.ledstrip.controller;

import com.loicortola.controller.R;
import com.loicortola.controller.command.behavior.Animable;
import com.loicortola.controller.command.behavior.Colorable;
import com.loicortola.controller.command.behavior.HealthAware;
import com.loicortola.controller.command.behavior.PresetAware;
import com.loicortola.controller.command.behavior.Secured;
import com.loicortola.controller.command.behavior.Switchable;
import com.loicortola.controller.library.ledstrip.model.AnimationSet;
import com.loicortola.controller.library.ledstrip.service.LedStripService;
import com.loicortola.controller.model.Animation;
import com.loicortola.controller.model.Device;
import com.loicortola.controller.model.Preset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by loic on 28/03/2016.
 */
public class LedStripRemoteControl implements Switchable, Colorable, Secured, Animable, HealthAware, PresetAware {

    private static final int GREY = 0x00888888;

    private Device d;
    private LedStripService service;
    private List<Preset> presets;
    private int currentColor;
    private Preset currentPreset;
    private boolean isActive;
    private String apiKey = "1234";

    public LedStripRemoteControl(Device d) {
        this.d = d;
        presets = new ArrayList<>();
        new Preset()
                .stringResId(R.string.preset_ocean)
                .drawableResId(R.drawable.preset_ocean)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 100, 255, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(100, 255, 200, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(40, 128, 100, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(80, 255, 220, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(50, 100, 80, 2000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 255, 180, 2000)))
                .addTo(presets);
        new Preset()
                .stringResId(R.string.preset_sunset)
                .drawableResId(R.drawable.preset_sunset)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(255, 100, 0, 8000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(255, 30, 0, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(100, 10, 0, 6000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(200, 50, 5, 2000)))
                .addTo(presets);
        new Preset()
                .stringResId(R.string.preset_uv)
                .drawableResId(R.drawable.preset_uv)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 0, 255, 8000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(128, 40, 128, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(0, 20, 200, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(128, 0, 255, 4000)))
                .addTo(presets);
        new Preset()
                .stringResId(R.string.preset_forest)
                .drawableResId(R.drawable.preset_forest)
                .content(new AnimationSet()
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(72, 87, 0, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(33, 38, 1, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(172, 190, 42, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(226, 242, 132, 4000))
                        .animation(new com.loicortola.controller.library.ledstrip.model.Animation(238, 181, 111, 4000)))
                .addTo(presets);
    }

    @Override
    public void changeColor(int c, final OnColorChangedListener l) {
        this.currentColor = c;
        l.onColorSet(true);
    }

    @Override
    public void setColor(int c, final OnColorChangedListener l) {
        this.currentColor = c;
        this.currentPreset = null;
        l.onColorSet(true);
    }

    @Override
    public void getColor(final OnColorGetListener l) {
        l.onColorGet(currentColor);
    }

    @Override
    public void isActive(final OnSwitchListener l) {
        l.onActiveResult(isActive);
    }

    @Override
    public void switchOn(final OnSwitchListener l) {
        isActive = true;
        l.onActiveResult(true);
    }

    @Override
    public void switchOff(final OnSwitchListener l) {
        isActive = true;
        l.onActiveResult(true);
    }

    @Override
    public void isValid(String key, final OnValidityCheckedListener l) {
        l.onValidityChecked(apiKey.equals(key));
    }


    @Override
    public void animate(Animation a, final OnAnimationSetListener l) {
        this.currentColor = GREY;
        l.onAnimationSet(true);
    }

    @Override
    public void isHealthy(final OnHealthCheckListener l) {
        l.onHealthCheck(true);
    }

    @Override
    public List<Preset> getPresets() {
        return Collections.unmodifiableList(presets);
    }

    @Override
    public void setPreset(Preset p, final OnPresetChangedListener l) {
        this.currentPreset = p;
        this.currentColor = GREY;
        l.onPresetChanged(true);
    }

    @Override
    public void getPreset(OnPresetLoadedListener l) {
        l.onPresetLoaded(currentPreset);
    }
}
