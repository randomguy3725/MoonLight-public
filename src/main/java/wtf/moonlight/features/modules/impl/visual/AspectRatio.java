package wtf.moonlight.features.modules.impl.visual;

import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.SliderValue;

@ModuleInfo(name = "AspectRatio", category = ModuleCategory.Visual)
public class AspectRatio extends Module {
    public final SliderValue aspect = new SliderValue("Aspect",1.0f, 0.1f, 5.0f, 0.1f,this);
}
