package wtf.moonlight.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.CancellableEvent;

@AllArgsConstructor
@Setter
@Getter
public class PlayerTickEvent extends CancellableEvent {
    public State state;
    public enum State {
        PRE,
        POST
    }
}
