package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

public class StatList
{
    protected static Map<String, StatBase> oneShotStats = Maps.newHashMap();
    public static List<StatBase> allStats = Lists.newArrayList();
    public static List<StatBase> generalStats = Lists.newArrayList();
    public static List<StatCrafting> itemStats = Lists.newArrayList();
    public static List<StatCrafting> objectMineStats = Lists.newArrayList();
    public static StatBase leaveGameStat = (new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame"))).initIndependentStat().registerStat();
    public static StatBase minutesPlayedStat = (new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute"), StatBase.timeStatType)).initIndependentStat().registerStat();
    public static StatBase timeSinceDeathStat = (new StatBasic("stat.timeSinceDeath", new ChatComponentTranslation("stat.timeSinceDeath"), StatBase.timeStatType)).initIndependentStat().registerStat();
    public static StatBase distanceWalkedStat = (new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceCrouchedStat = (new StatBasic("stat.crouchOneCm", new ChatComponentTranslation("stat.crouchOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceSprintedStat = (new StatBasic("stat.sprintOneCm", new ChatComponentTranslation("stat.sprintOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceSwumStat = (new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceFallenStat = (new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceClimbedStat = (new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceFlownStat = (new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceDoveStat = (new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceByMinecartStat = (new StatBasic("stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceByBoatStat = (new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceByPigStat = (new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase distanceByHorseStat = (new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm"), StatBase.distanceStatType)).initIndependentStat().registerStat();
    public static StatBase jumpStat = (new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump"))).initIndependentStat().registerStat();
    public static StatBase dropStat = (new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop"))).initIndependentStat().registerStat();
    public static StatBase damageDealtStat = (new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt"), StatBase.field_111202_k)).registerStat();
    public static StatBase damageTakenStat = (new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken"), StatBase.field_111202_k)).registerStat();
    public static StatBase deathsStat = (new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths"))).registerStat();
    public static StatBase mobKillsStat = (new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills"))).registerStat();
    public static StatBase animalsBredStat = (new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred"))).registerStat();
    public static StatBase playerKillsStat = (new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills"))).registerStat();
    public static StatBase fishCaughtStat = (new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught"))).registerStat();
    public static StatBase junkFishedStat = (new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished"))).registerStat();
    public static StatBase treasureFishedStat = (new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished"))).registerStat();
    public static StatBase timesTalkedToVillagerStat = (new StatBasic("stat.talkedToVillager", new ChatComponentTranslation("stat.talkedToVillager"))).registerStat();
    public static StatBase timesTradedWithVillagerStat = (new StatBasic("stat.tradedWithVillager", new ChatComponentTranslation("stat.tradedWithVillager"))).registerStat();
    public static StatBase field_181724_H = (new StatBasic("stat.cakeSlicesEaten", new ChatComponentTranslation("stat.cakeSlicesEaten"))).registerStat();
    public static StatBase field_181725_I = (new StatBasic("stat.cauldronFilled", new ChatComponentTranslation("stat.cauldronFilled"))).registerStat();
    public static StatBase field_181726_J = (new StatBasic("stat.cauldronUsed", new ChatComponentTranslation("stat.cauldronUsed"))).registerStat();
    public static StatBase field_181727_K = (new StatBasic("stat.armorCleaned", new ChatComponentTranslation("stat.armorCleaned"))).registerStat();
    public static StatBase field_181728_L = (new StatBasic("stat.bannerCleaned", new ChatComponentTranslation("stat.bannerCleaned"))).registerStat();
    public static StatBase field_181729_M = (new StatBasic("stat.brewingstandInteraction", new ChatComponentTranslation("stat.brewingstandInteraction"))).registerStat();
    public static StatBase field_181730_N = (new StatBasic("stat.beaconInteraction", new ChatComponentTranslation("stat.beaconInteraction"))).registerStat();
    public static StatBase field_181731_O = (new StatBasic("stat.dropperInspected", new ChatComponentTranslation("stat.dropperInspected"))).registerStat();
    public static StatBase field_181732_P = (new StatBasic("stat.hopperInspected", new ChatComponentTranslation("stat.hopperInspected"))).registerStat();
    public static StatBase field_181733_Q = (new StatBasic("stat.dispenserInspected", new ChatComponentTranslation("stat.dispenserInspected"))).registerStat();
    public static StatBase field_181734_R = (new StatBasic("stat.noteblockPlayed", new ChatComponentTranslation("stat.noteblockPlayed"))).registerStat();
    public static StatBase field_181735_S = (new StatBasic("stat.noteblockTuned", new ChatComponentTranslation("stat.noteblockTuned"))).registerStat();
    public static StatBase field_181736_T = (new StatBasic("stat.flowerPotted", new ChatComponentTranslation("stat.flowerPotted"))).registerStat();
    public static StatBase field_181737_U = (new StatBasic("stat.trappedChestTriggered", new ChatComponentTranslation("stat.trappedChestTriggered"))).registerStat();
    public static StatBase field_181738_V = (new StatBasic("stat.enderchestOpened", new ChatComponentTranslation("stat.enderchestOpened"))).registerStat();
    public static StatBase field_181739_W = (new StatBasic("stat.itemEnchanted", new ChatComponentTranslation("stat.itemEnchanted"))).registerStat();
    public static StatBase field_181740_X = (new StatBasic("stat.recordPlayed", new ChatComponentTranslation("stat.recordPlayed"))).registerStat();
    public static StatBase field_181741_Y = (new StatBasic("stat.furnaceInteraction", new ChatComponentTranslation("stat.furnaceInteraction"))).registerStat();
    public static StatBase field_181742_Z = (new StatBasic("stat.craftingTableInteraction", new ChatComponentTranslation("stat.workbenchInteraction"))).registerStat();
    public static StatBase field_181723_aa = (new StatBasic("stat.chestOpened", new ChatComponentTranslation("stat.chestOpened"))).registerStat();
    public static final StatBase[] mineBlockStatArray = new StatBase[4096];
    public static final StatBase[] objectCraftStats = new StatBase[32000];
    public static final StatBase[] objectUseStats = new StatBase[32000];
    public static final StatBase[] objectBreakStats = new StatBase[32000];

    public static void init()
    {
        initMiningStats();
        initStats();
        initItemDepleteStats();
        initCraftableStats();
        AchievementList.init();
        EntityList.func_151514_a();
    }

    private static void initCraftableStats()
    {
        Set<Item> set = Sets.newHashSet();

        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList())
        {
            if (irecipe.getRecipeOutput() != null)
            {
                set.add(irecipe.getRecipeOutput().getItem());
            }
        }

        for (ItemStack itemstack : FurnaceRecipes.instance().getSmeltingList().values())
        {
            set.add(itemstack.getItem());
        }

        for (Item item : set)
        {
            if (item != null)
            {
                int i = Item.getIdFromItem(item);
                String s = func_180204_a(item);

                if (s != null)
                {
                    objectCraftStats[i] = (new StatCrafting("stat.craftItem.", s, new ChatComponentTranslation("stat.craftItem", (new ItemStack(item)).getChatComponent()), item)).registerStat();
                }
            }
        }

        replaceAllSimilarBlocks(objectCraftStats);
    }

    private static void initMiningStats()
    {
        for (Block block : Block.blockRegistry)
        {
            Item item = Item.getItemFromBlock(block);

            if (item != null)
            {
                int i = Block.getIdFromBlock(block);
                String s = func_180204_a(item);

                if (s != null && block.getEnableStats())
                {
                    mineBlockStatArray[i] = (new StatCrafting("stat.mineBlock.", s, new ChatComponentTranslation("stat.mineBlock", (new ItemStack(block)).getChatComponent()), item)).registerStat();
                    objectMineStats.add((StatCrafting)mineBlockStatArray[i]);
                }
            }
        }

        replaceAllSimilarBlocks(mineBlockStatArray);
    }

    private static void initStats()
    {
        for (Item item : Item.itemRegistry)
        {
            if (item != null)
            {
                int i = Item.getIdFromItem(item);
                String s = func_180204_a(item);

                if (s != null)
                {
                    objectUseStats[i] = (new StatCrafting("stat.useItem.", s, new ChatComponentTranslation("stat.useItem", (new ItemStack(item)).getChatComponent()), item)).registerStat();

                    if (!(item instanceof ItemBlock))
                    {
                        itemStats.add((StatCrafting)objectUseStats[i]);
                    }
                }
            }
        }

        replaceAllSimilarBlocks(objectUseStats);
    }

    private static void initItemDepleteStats()
    {
        for (Item item : Item.itemRegistry)
        {
            if (item != null)
            {
                int i = Item.getIdFromItem(item);
                String s = func_180204_a(item);

                if (s != null && item.isDamageable())
                {
                    objectBreakStats[i] = (new StatCrafting("stat.breakItem.", s, new ChatComponentTranslation("stat.breakItem", (new ItemStack(item)).getChatComponent()), item)).registerStat();
                }
            }
        }

        replaceAllSimilarBlocks(objectBreakStats);
    }

    private static String func_180204_a(Item p_180204_0_)
    {
        ResourceLocation resourcelocation = Item.itemRegistry.getNameForObject(p_180204_0_);
        return resourcelocation != null ? resourcelocation.toString().replace(':', '.') : null;
    }

    private static void replaceAllSimilarBlocks(StatBase[] p_75924_0_)
    {
        mergeStatBases(p_75924_0_, Blocks.water, Blocks.flowing_water);
        mergeStatBases(p_75924_0_, Blocks.lava, Blocks.flowing_lava);
        mergeStatBases(p_75924_0_, Blocks.lit_pumpkin, Blocks.pumpkin);
        mergeStatBases(p_75924_0_, Blocks.lit_furnace, Blocks.furnace);
        mergeStatBases(p_75924_0_, Blocks.lit_redstone_ore, Blocks.redstone_ore);
        mergeStatBases(p_75924_0_, Blocks.powered_repeater, Blocks.unpowered_repeater);
        mergeStatBases(p_75924_0_, Blocks.powered_comparator, Blocks.unpowered_comparator);
        mergeStatBases(p_75924_0_, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
        mergeStatBases(p_75924_0_, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
        mergeStatBases(p_75924_0_, Blocks.double_stone_slab, Blocks.stone_slab);
        mergeStatBases(p_75924_0_, Blocks.double_wooden_slab, Blocks.wooden_slab);
        mergeStatBases(p_75924_0_, Blocks.double_stone_slab2, Blocks.stone_slab2);
        mergeStatBases(p_75924_0_, Blocks.grass, Blocks.dirt);
        mergeStatBases(p_75924_0_, Blocks.farmland, Blocks.dirt);
    }

    private static void mergeStatBases(StatBase[] statBaseIn, Block p_151180_1_, Block p_151180_2_)
    {
        int i = Block.getIdFromBlock(p_151180_1_);
        int j = Block.getIdFromBlock(p_151180_2_);

        if (statBaseIn[i] != null && statBaseIn[j] == null)
        {
            statBaseIn[j] = statBaseIn[i];
        }
        else
        {
            allStats.remove(statBaseIn[i]);
            objectMineStats.remove(statBaseIn[i]);
            generalStats.remove(statBaseIn[i]);
            statBaseIn[i] = statBaseIn[j];
        }
    }

    public static StatBase getStatKillEntity(EntityList.EntityEggInfo eggInfo)
    {
        String s = EntityList.getStringFromID(eggInfo.spawnedID);
        return s == null ? null : (new StatBase("stat.killEntity." + s, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + s + ".name")))).registerStat();
    }

    public static StatBase getStatEntityKilledBy(EntityList.EntityEggInfo eggInfo)
    {
        String s = EntityList.getStringFromID(eggInfo.spawnedID);
        return s == null ? null : (new StatBase("stat.entityKilledBy." + s, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + s + ".name")))).registerStat();
    }

    public static StatBase getOneShotStat(String p_151177_0_)
    {
        return oneShotStats.get(p_151177_0_);
    }
}
