package wtf.moonlight.features.modules.impl.visual;

import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;

@ModuleInfo(name = "Test2", category = ModuleCategory.Visual, key = Keyboard.KEY_M)
public class Test2 extends Module {
    @Override
    public void onEnable() {
        mc.displayGuiScreen(INSTANCE.getSkeetGUI());
        INSTANCE.getSkeetGUI().alpha = 0.0;
        INSTANCE.getSkeetGUI().targetAlpha = 255.0;
        INSTANCE.getSkeetGUI().open = true;
        INSTANCE.getSkeetGUI().closed = false;
        toggle();
        super.onEnable();
    }
}