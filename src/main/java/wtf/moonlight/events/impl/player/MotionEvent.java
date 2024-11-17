package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public final class MotionEvent extends CancellableEvent {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public State state;

    public MotionEvent(State state) {
        this.state = state;
    }

    public enum State {
        PRE,
        POST
    }

    public boolean isPre() {
        return state.equals(State.PRE);
    }

    public boolean isPost() {
        return state.equals(State.POST);
    }
}