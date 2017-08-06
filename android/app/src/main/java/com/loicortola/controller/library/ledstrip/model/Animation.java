package com.loicortola.controller.library.ledstrip.model;

/**
 * Created by loicortola on 06/08/2017.
 */

public class Animation {
    public int r;
    public int g;
    public int b;
    public int loopTime;

    public Animation() {}

    public Animation(int r, int g, int b, int loopTime) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.loopTime = loopTime;
    }
}
