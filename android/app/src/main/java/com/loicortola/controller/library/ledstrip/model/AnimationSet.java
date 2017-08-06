package com.loicortola.controller.library.ledstrip.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by loicortola on 06/08/2017.
 */

public class AnimationSet {

    public final List<Animation> animations = new LinkedList<>();

    public AnimationSet animation(Animation a) {
        animations.add(a);
        return this;
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public List<String> queryString() {
        List<String> s = new ArrayList<>(animations.size());

        for (Animation a : animations) {
            s.add(new StringBuilder()
                    .append(a.r)
                    .append(",")
                    .append(a.g)
                    .append(",")
                    .append(a.b)
                    .append(",")
                    .append(a.loopTime)
                    .toString());
        }
        return s;
    }
}
