package wtf.moonlight.events.impl.render;

import lombok.AllArgsConstructor;
import wtf.moonlight.events.impl.Event;

@AllArgsConstructor
public class ChatGUIEvent implements Event {
    public int mouseX, mouseY;
}
