package com.loicortola.controller.model;

import java.util.Collection;

/**
 * Created by loic on 28/03/2016.
 */
public class Preset {

    public int drawableResId;
    public int stringResId;
    public Object content;

    public Preset drawableResId(int resId) {
        this.drawableResId = resId;
        return this;
    }

    public Preset stringResId(int resId) {
        this.stringResId = resId;
        return this;
    }

    public Preset content(Object content) {
        this.content = content;
        return this;
    }

    public Preset addTo(Collection<Preset> c) {
        if (c != null) {
            c.add(this);
        }
        return this;
    }

}
