package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.Scaffold;
import wtf.moonlight.features.values.impl.SliderValue;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.RotationUtils;

@ModuleInfo(name = "AutoPot", category = ModuleCategory.Combat)
public class AutoPot extends Module {
    private final SliderValue health = new SliderValue("Health", 15, 1, 20, 1, this);
    private final SliderValue delay = new SliderValue("Delay", 500, 50, 5000, 50, this);
    private final TimerUtils timer = new TimerUtils();
    private long nextThrow;

    @EventTarget
    public void onUpdate(UpdateEvent event) {

        if (!mc.thePlayer.onGround || !timer.hasTimeElapsed(nextThrow) || isEnabled(Scaffold.class) || isEnabled(KillAura.class) && getModule(KillAura.class).target != null) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

            if (stack == null) {
                continue;
            }

            final Item item = stack.getItem();

            if (item instanceof ItemPotion potion) {
                final PotionEffect effect = potion.getEffects(stack).get(0);

                if (!ItemPotion.isSplash(stack.getMetadata()) ||
                        !PlayerUtils.goodPotion(effect.getPotionID()) ||
                        (effect.getPotionID() == Potion.regeneration.id ||
                                effect.getPotionID() == Potion.heal.id) &&
                                mc.thePlayer.getHealth() > this.health.get()) {
                    continue;
                }

                if (mc.thePlayer.isPotionActive(effect.getPotionID()) &&
                        mc.thePlayer.activePotionsMap.get(effect.getPotionID()).getDuration() != 0) {
                    continue;
                }

                RotationUtils.setRotation(new float[]{mc.thePlayer.rotationYaw, 87});

                mc.thePlayer.inventory.currentItem = i;

                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));

                this.nextThrow = (long) delay.get();
                timer.reset();
                break;
            }
        }
    }
}
