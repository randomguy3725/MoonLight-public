package wtf.moonlight.utils.animations.impl;

import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;

public class DecelerateAnimation extends Animation {

    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return 1 - ((x - 1) * (x - 1));
    }
}
