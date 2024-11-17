package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MoveInputEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;

@ModuleInfo(name = "InvMove", category = ModuleCategory.Movement)
public class InvMove extends Module {
    private final BoolValue cancelInventory = new BoolValue("NoInv", false, this);
    private final BoolValue cancelChest = new BoolValue("No Chest", false, this);
    private final BoolValue wdChest = new BoolValue("Watchdog Chest", false, this);
    private final KeyBinding[] keyBindings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindJump};

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        for (KeyBinding keyBinding : this.keyBindings) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
        }
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        if (wdChest.get() && mc.currentScreen instanceof GuiChest)
            event.setJumping(false);
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiIngameMenu)) {
            if (cancelInventory.get() && (mc.currentScreen instanceof GuiContainer)) {
                return;
            }
            if (cancelChest.get() && mc.currentScreen instanceof GuiChest)
                return;
            for (KeyBinding keyBinding : this.keyBindings) {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), GameSettings.isKeyDown(keyBinding));
            }

            if (wdChest.get() && mc.currentScreen instanceof GuiChest)
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        }
    }
}
