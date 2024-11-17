package wtf.moonlight.features.modules.impl.combat;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.WorldEvent;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.MultiBoolValue;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(name = "AntiBot", category = ModuleCategory.Combat)
public class AntiBot extends Module {
    public final MultiBoolValue options = new MultiBoolValue("Options", Arrays.asList(new BoolValue("Tab", false), new BoolValue("Matrix Test", false), new BoolValue("Matrix Armor", false), new BoolValue("HYT Get Name", false)), this);
    private final ModeValue hytGetNameModes = new ModeValue("GetName Mode", new String[]{"Bw4v4","Bw1v1","Bw32","Bw16"},"Bw4v4",this,()->options.isEnabled("HYT Get Name"));
    public final ArrayList<EntityPlayer> bots = new ArrayList<>();
    private final List<String> playerName = new ArrayList<>();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity != mc.thePlayer)
                if (isBot(entity))
                    bots.add(entity);
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        Packet<?> packet = event.getPacket();
        if (options.isEnabled("HYT Get Name") && packet instanceof S02PacketChat s02PacketChat) {
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("获得胜利!") || s02PacketChat.getChatComponent().getUnformattedText().contains("游戏开始 ...")) {
                playerName.clear();
            }
            switch (hytGetNameModes.get()) {
                case "Bw4v4":
                case "Bw1v1":
                case "Bw32": {
                    Matcher matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?) 死了!)))").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if (matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000);
                                    playerName.remove(name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    if (matcher2.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) {
                        String name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000);
                                    playerName.remove(name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;
                }
                case "Bw16": {
                    Matcher matcher = Pattern.compile("击败了 (.*?)!").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if (matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    playerName.remove(name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    if (matcher2.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) {
                        String name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    playerName.remove(name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;

                }
            }
        }

        if(options.isEnabled("Matrix Test")){
            if (packet instanceof S38PacketPlayerListItem) {
                for (S38PacketPlayerListItem.AddPlayerData data : ((S38PacketPlayerListItem) packet).getEntries()) {
                    if (((S38PacketPlayerListItem) packet).getAction().equals(S38PacketPlayerListItem.Action.ADD_PLAYER) &&
                            data.getProfile().getProperties().isEmpty() && ((S38PacketPlayerListItem) packet).getEntries().size() == 1
                            && mc.getNetHandler() != null && mc.getNetHandler().getPlayerInfo(data.getProfile().getName()) != null) {
                        if (!bots.contains(data.getProfile().getId())) {
                            bots.add(mc.theWorld.getPlayerEntityByName(data.getProfile().getName()));
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        bots.clear();
    }

    @Override
    public void onDisable() {
        bots.clear();
    }

    public boolean isBot(EntityPlayer entity) {

        if (options.isEnabled("Tab")) {
            String targetName = RenderUtils.stripColor(entity.getDisplayName().getFormattedText());
            if (targetName != null) {
                for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    String networkName = RenderUtils.stripColor(PlayerUtils.getName(networkPlayerInfo));
                    if (targetName.contains(networkName)) return false;
                }
                return true;
            }
        }

        if (options.isEnabled("Matrix Armor")) {
            ItemStack helmet = entity.getInventory()[3];
            ItemStack chestplate = entity.getInventory()[2];

            if (helmet == null || chestplate == null)
                return true;
            if (helmet.getItem() == null || chestplate.getItem() == null)
                return true;

            int helmetColor = ((ItemArmor) helmet.getItem()).getColor(helmet);
            int chestplateColor = ((ItemArmor) chestplate.getItem()).getColor(chestplate);
            return !(helmetColor > 0 && chestplateColor == helmetColor);
        }

        if(options.isEnabled("HYT Get Name")){
            return playerName.contains(entity.getName());
        }

        return false;
    }
}
