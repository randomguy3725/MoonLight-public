package wtf.moonlight.events.impl.render;

import net.minecraft.entity.Entity;
import wtf.moonlight.events.impl.CancellableEvent;

public class RenderNameTagEvent extends CancellableEvent {

    final Entity entity;

    public RenderNameTagEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

}