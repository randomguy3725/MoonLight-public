package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.TickEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;

@ModuleInfo(name = "AutoClicker", category = ModuleCategory.Combat)
public class AutoClicker extends Module {
    private final SliderValue minAps = new SliderValue("Min Aps", 10, 1, 20, this);
    private final SliderValue maxAps = new SliderValue("Max Aps", 12, 1, 20, this);
    private final BoolValue breakBlocks = new BoolValue("Break Blocks", true, this);
    private final TimerUtils clickTimer = new TimerUtils();

    @EventTarget
    public void onTick(TickEvent event) {
        if (breakBlocks.get() && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            return;

        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (clickTimer.hasTimeElapsed(1000 / MathUtils.nextInt((int) minAps.get(), (int) maxAps.get()))) {
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                clickTimer.reset();
            }
        }
    }
}
