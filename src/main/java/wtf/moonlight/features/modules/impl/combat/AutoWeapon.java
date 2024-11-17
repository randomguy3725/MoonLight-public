package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C02PacketUseEntity;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.utils.player.InventoryUtils;

@ModuleInfo(name = "AutoWeapon", category = ModuleCategory.Combat)
public class AutoWeapon extends Module {

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C02PacketUseEntity packet && packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
            int slot = -1;
            double maxDamage = -1.0;

            for (int i = 0; i <= 8; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemSword) {
                    double damage = stack.getAttributeModifiers().get("generic.attackDamage").stream().findFirst().map(AttributeModifier::getAmount).orElse(0.0)
                            + 1.25 * InventoryUtils.getEnchantment(stack, Enchantment.sharpness);
                    if (damage > maxDamage) {
                        maxDamage = damage;
                        slot = i;
                    }
                }
            }

            if (slot == -1 || slot == mc.thePlayer.inventory.currentItem)
                return;

            mc.thePlayer.inventory.currentItem = slot;
            mc.playerController.updateController();
            Entity entity = ((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld);
            event.setCancelled(true);
            sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
    }
}
