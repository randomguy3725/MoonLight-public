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
package wtf.moonlight.features.modules.impl.misc;

import net.minecraft.block.BlockObsidian;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.utils.misc.DebugUtils;
import wtf.moonlight.utils.player.InventoryUtils;

import java.util.Collection;
import java.util.HashSet;

@ModuleInfo(name = "ItemAlerts", category = ModuleCategory.Misc)
public class ItemAlerts extends Module {
    
    public final BoolValue swords = new BoolValue("Swords",true,this);
    private final BoolValue includeStone = new BoolValue("Include Stone", true,this, swords::get);
    public final BoolValue armor = new BoolValue("Armor",true,this);
    public final BoolValue tools = new BoolValue("Tools",true,this);
    public final BoolValue enchant = new BoolValue("Enchant",true,this);
    public final BoolValue items = new BoolValue("Items",true,this);
    public final BoolValue invisibleCheck = new BoolValue("Invisible Check",true,this);
    public final BoolValue potionInvis = new BoolValue("Invisibility Status",true,this);
    private final Collection<EntityPlayer> ironSword = new HashSet<>();

    private final Collection<EntityPlayer> diamondSword = new HashSet<>();

    private final Collection<EntityPlayer> stoneSword = new HashSet<>();
    private final Collection<EntityPlayer> ironPickaxe = new HashSet<>();

    private final Collection<EntityPlayer> diamondPickaxe = new HashSet<>();

    private final Collection<EntityPlayer> goldPickaxe = new HashSet<>();

    private final Collection<EntityPlayer> diamondArmor = new HashSet<>();

    private final Collection<EntityPlayer> chainArmor = new HashSet<>();

    private final Collection<EntityPlayer> ironArmor = new HashSet<>();

    private final Collection<EntityPlayer> invisible = new HashSet<>();
    private final Collection<EntityPlayer> fireball = new HashSet<>();
    private final Collection<EntityPlayer> enderPearl = new HashSet<>();
    private final Collection<EntityPlayer> obsidian = new HashSet<>();
    private final Collection<EntityPlayer> enchantedArmor = new HashSet<>();
    private final Collection<EntityPlayer> enchantedSword = new HashSet<>();
    private boolean wasThePlayerInvis = false;

    @EventTarget
    public void onUpdate(UpdateEvent event){
        for (final EntityPlayer entity : mc.theWorld.playerEntities) {
            if (mc.thePlayer != null || mc.theWorld != null) {
                if (entity.getHeldItem() != null) {
                    final Item heldItem = entity.getHeldItem().getItem();

                    if (swords.get()) {
                        if (heldItem instanceof ItemSword) {
                            final String type = ((ItemSword) heldItem).getToolMaterialName().toLowerCase();

                            if (type.contains("iron")) {
                                if (!ironSword.contains(entity)) {
                                    ironSword.add(entity);
                                    DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has an " + EnumChatFormatting.AQUA + "Iron Sword");

                                }
                            }

                            if (type.contains("emerald")) {
                                if (!diamondSword.contains(entity)) {
                                    diamondSword.add(entity);
                                    DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has a " + EnumChatFormatting.AQUA + "Diamond Sword");
                                }
                            }

                            if (type.contains("stone")) {
                                if (!stoneSword.contains(entity)) {
                                    stoneSword.add(entity);
                                    if (includeStone.get()) {
                                        DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has a " + EnumChatFormatting.AQUA + "Stone Sword");
                                    }
                                }
                            }

                            if (type.contains("wood")) {
                                stoneSword.remove(entity);
                                ironSword.remove(entity);
                                diamondSword.remove(entity);
                            }
                        }
                    }

                    if (tools.get()) {
                        if (heldItem instanceof ItemPickaxe) {
                            final String type = ((ItemPickaxe) heldItem).getToolMaterialName().toLowerCase();

                            if (type.contains("iron")) {
                                if (!ironPickaxe.contains(entity)) {
                                    ironPickaxe.add(entity);
                                    DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has an " + EnumChatFormatting.AQUA + "Iron Pickaxe");

                                }
                            }

                            if (type.contains("emerald")) {
                                if (!diamondPickaxe.contains(entity)) {
                                    diamondPickaxe.add(entity);
                                    DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has a " + EnumChatFormatting.AQUA + "Diamond Pickaxe");
                                }
                            }

                            if (type.contains("gold")) {
                                if (!goldPickaxe.contains(entity)) {
                                    goldPickaxe.add(entity);
                                    if (includeStone.get()) {
                                        DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has a " + EnumChatFormatting.AQUA + "Stone Pickaxe");
                                    }
                                }
                            }

                            if (type.contains("wood")) {
                                goldPickaxe.remove(entity);
                                ironPickaxe.remove(entity);
                                diamondPickaxe.remove(entity);
                            }
                        }
                    }


                    if (enchant.get()) {
                        if (InventoryUtils.getEnchantment(entity.inventoryContainer.getSlot(6).getStack(), Enchantment.protection) != 0) {
                            if (!enchantedArmor.contains(entity)) {
                                enchantedArmor.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has a " + EnumChatFormatting.BLUE + "Reinforced Armor");
                            }
                        } else {
                            enchantedArmor.remove(entity);
                        }
                        if (InventoryUtils.getEnchantment(entity.getHeldItem(), Enchantment.protection) != 0) {
                            if (!enchantedSword.contains(entity)) {
                                enchantedSword.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has a " + EnumChatFormatting.YELLOW + "Sharpened Swords");
                            }
                        } else {
                            enchantedSword.remove(entity);
                        }
                    }

                    if (items.get()) {
                        if (heldItem instanceof ItemFireball) {
                            if (!fireball.contains(entity)) {
                                fireball.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has " + EnumChatFormatting.RED + "Fire Ball");
                            }
                        } else {
                            fireball.remove(entity);
                        }

                        if (heldItem instanceof ItemEnderPearl) {
                            if (!enderPearl.contains(entity)) {
                                enderPearl.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has " + EnumChatFormatting.DARK_PURPLE + "Ender Pearl");
                            }
                        } else {
                            enderPearl.remove(entity);
                        }

                        if (heldItem instanceof ItemBlock itemBlock && itemBlock.getBlock() instanceof BlockObsidian) {
                            if (!obsidian.contains(entity)) {
                                obsidian.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has " + EnumChatFormatting.DARK_PURPLE + "Obsidian");
                            }
                        } else {
                            obsidian.remove(entity);
                        }
                    }
                }

                if (armor.get()) {
                    ItemStack entityCurrentArmor = entity.getCurrentArmor(1);

                    if (entityCurrentArmor != null && entityCurrentArmor.getItem() instanceof ItemArmor) {

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.CHAIN)) {
                            if (!chainArmor.contains(entity)) {
                                chainArmor.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has " + EnumChatFormatting.LIGHT_PURPLE + "Chain Armor");
                            }
                        }

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.IRON)) {
                            if (!ironArmor.contains(entity)) {
                                ironArmor.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has " + EnumChatFormatting.LIGHT_PURPLE + "Iron Armor");
                            }
                        }

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.DIAMOND)) {
                            if (!diamondArmor.contains(entity)) {
                                diamondArmor.add(entity);
                                DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " has " + EnumChatFormatting.LIGHT_PURPLE + "Diamond Armor");
                            }
                        }

                        if (((ItemArmor) entityCurrentArmor.getItem()).getArmorMaterial().equals(ItemArmor.ArmorMaterial.LEATHER)) {
                            diamondArmor.remove(entity);
                            ironArmor.remove(entity);
                            chainArmor.remove(entity);
                        }
                    }
                }

                if (invisibleCheck.get()) {
                    if (entity.getActivePotionEffect(Potion.invisibility) != null) {
                        if (!invisible.contains(entity)) {
                            invisible.add(entity);
                            DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " is now " + EnumChatFormatting.GOLD + "Invisible");
                        }
                    } else if (invisible.contains(entity)) {
                        invisible.remove(entity);
                        DebugUtils.sendMessage("Player " + EnumChatFormatting.RED + entity.getGameProfile().getName() + EnumChatFormatting.WHITE + " is now " + EnumChatFormatting.GOLD + "Visible");
                    }
                }

                if (potionInvis.get()) {
                    if (mc.thePlayer.getActivePotionEffect(Potion.invisibility) != null) {
                        wasThePlayerInvis = true;
                        if (mc.thePlayer.ticksExisted % 200 == 0) {
                            DebugUtils.sendMessage("Your Invisibility" + EnumChatFormatting.RED + " expires " + EnumChatFormatting.RESET + "in " + EnumChatFormatting.RED + mc.thePlayer.getActivePotionEffect(Potion.invisibility).getDuration() / 20 + EnumChatFormatting.RESET + " second(s)");
                        }
                    }
                } else if (wasThePlayerInvis) {
                    DebugUtils.sendMessage("Invisibility" + EnumChatFormatting.RED + " Expired");
                    wasThePlayerInvis = false;
                }

            } else {
                diamondSword.clear();
                ironSword.clear();
                stoneSword.clear();
                diamondArmor.clear();
                ironArmor.clear();
                chainArmor.clear();
                invisible.clear();
                goldPickaxe.clear();
                diamondPickaxe.clear();
                ironPickaxe.clear();
                enderPearl.clear();
                fireball.clear();
                obsidian.clear();
                enchantedArmor.clear();
                enchantedSword.clear();
            }
        }
    }
}
