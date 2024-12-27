/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [RandomGuy & opZywl]
 */
package wtf.moonlight.features.modules.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MoveInputEvent;
import wtf.moonlight.events.impl.player.SafeWalkEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.player.PlayerUtils;

@ModuleInfo(name = "SafeWalk", category = ModuleCategory.Movement)
public class SafeWalk extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Safe", "Sneak"}, "Safe", this);
    private final BoolValue heldBlocks = new BoolValue("Held Blocks Check", true, this);
    private final BoolValue pitchCheck = new BoolValue("Pitch Check", true, this);
    private final SliderValue minPitch = new SliderValue("Min Pitch", 55, 50, 90, 1, this, pitchCheck::get);
    public final SliderValue maxPitch = new SliderValue("Max Pitch", 75, 50, 90, 1, this, pitchCheck::get);

    @EventTarget
    public void onSafeWalk(SafeWalkEvent event) {
        if (canSafeWalk() && mode.is("Safe"))
            event.setCancelled(true);
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event) {
        if (canSafeWalk() && mode.is("Sneak") && PlayerUtils.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir)
            event.setSneaking(true);
    }

    public boolean canSafeWalk() {
        return mc.thePlayer.onGround && (heldBlocks.get() && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock || !heldBlocks.get()) && (pitchCheck.get() && MathUtils.inBetween(minPitch.getMin(), maxPitch.getMax(), mc.thePlayer.rotationPitch) || !pitchCheck.get());
    }
}
