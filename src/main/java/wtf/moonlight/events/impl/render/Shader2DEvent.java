package wtf.moonlight.events.impl.render;

import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.events.impl.Event;

@Getter
@Setter
public class Shader2DEvent implements Event {

    private ShaderType shaderType;

    public Shader2DEvent(ShaderType shaderType) {
        this.shaderType = shaderType;
    }

    public enum ShaderType {
        BLUR, SHADOW, GLOW
    }
}
