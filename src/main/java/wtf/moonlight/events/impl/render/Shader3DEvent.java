package wtf.moonlight.events.impl.render;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.Event;

@Getter
@Setter
public class Shader3DEvent implements Event {

    private Shader3DEvent.ShaderType shaderType;

    public Shader3DEvent(Shader3DEvent.ShaderType shaderType) {
        this.shaderType = shaderType;
    }

    public enum ShaderType {
        BLUR, SHADOW, GLOW
    }
}
