package wtf.moonlight.features.modules.impl.visual;

import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "Test", category = ModuleCategory.Visual, key = Keyboard.KEY_M)
public class Test extends Module {
    @Override
    public void onEnable() {
        mc.displayGuiScreen(INSTANCE.getMoonGUI());
        toggle();
        super.onEnable();
    }
}
