package wtf.moonlight.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.Event;

@Getter
@Setter
@AllArgsConstructor
public class TimerManipulationEvent implements Event {
    private long time;
}
