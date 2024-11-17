package wtf.moonlight.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wtf.moonlight.events.impl.CancellableEvent;

@Getter
@AllArgsConstructor
public class SendMessageEvent extends CancellableEvent {
    private final String message;
}
