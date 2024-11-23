package wtf.moonlight.features.modules.impl.visual;

import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "TestGUI", category = ModuleCategory.Visual, key = Keyboard.KEY_M)
public class TestGUI extends Module {
    @Override
    public void onEnable() {
        mc.displayGuiScreen(INSTANCE.getMoonGUI());
        toggle();
        super.onEnable();
    }
}
