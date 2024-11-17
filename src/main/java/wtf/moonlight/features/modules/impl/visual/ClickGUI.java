package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.features.values.impl.ModeValue;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", category = ModuleCategory.Visual, key = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {
    public final ModeValue mode = new ModeValue("Mode", new String[]{"Menu", "DropDown","Exhi"}, "DropDown", this);

    public final ColorValue color = new ColorValue("Color", new Color(128, 128, 255), this);
    public final BoolValue rainbow = new BoolValue("Rainbow",true,this,() -> mode.is("Exhi"));

    @Override
    public void onEnable() {
        GuiScreen guiScreen = null;
        switch (mode.get()) {
            case "Menu":
                guiScreen = INSTANCE.getMenuGUI();
                break;
            case "DropDown":
                guiScreen = INSTANCE.getDropdownGUI();
                break;
            case "Moon":
                guiScreen = INSTANCE.getMoonGUI();
                break;
            case "Exhi":
                guiScreen = INSTANCE.getSkeetGUI();
                break;
        }
        mc.displayGuiScreen(guiScreen);
        if(mode.is("Exhi")){
            INSTANCE.getSkeetGUI().alpha = 0.0;
            INSTANCE.getSkeetGUI().targetAlpha = 255.0;
            INSTANCE.getSkeetGUI().open = true;
            INSTANCE.getSkeetGUI().closed = false;
        }
        toggle();
        super.onEnable();
    }
}
