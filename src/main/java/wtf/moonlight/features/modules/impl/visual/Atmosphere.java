package wtf.moonlight.features.modules.impl.visual;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.packet.PacketEvent;
import wtf.moonlight.events.impl.player.UpdateEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.features.values.impl.ColorValue;
import wtf.moonlight.features.values.impl.ModeValue;
import wtf.moonlight.features.values.impl.SliderValue;

import java.awt.*;

@ModuleInfo(name = "Atmosphere", category = ModuleCategory.Visual)
public class Atmosphere extends Module {
    private final BoolValue time = new BoolValue("Time Editor",true,this);
    private final SliderValue timeValue = new SliderValue("Time", 18000, 0, 24000, 1000, this,time::get);
    private final BoolValue weather = new BoolValue("Weather Editor",true,this);
    private final ModeValue weatherValue = new ModeValue("Weather",new String[]{"Clean", "Rain", "Thunder"},"Clean",this,weather::get);
    public final BoolValue worldColor = new BoolValue("World Color", true, this);
    public final ColorValue worldColorRGB = new ColorValue("World Color RGB", Color.WHITE, this, worldColor::get);
    public final BoolValue worldFog = new BoolValue("World Fog", false, this);
    public final ColorValue worldFogRGB = new ColorValue("World Fog RGB", Color.WHITE, this, worldFog::get);

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if(time.get())
            mc.theWorld.setWorldTime((long) timeValue.get());
        if (weather.get()) {
            switch (weatherValue.get()) {
                case "Rain":
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.setThunderStrength(0);
                    break;
                case "Thunder":
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.setThunderStrength(1);
                    break;
                default:
                    mc.theWorld.setRainStrength(0);
                    mc.theWorld.setThunderStrength(0);
            }
        }
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (time.get() && event.getPacket() instanceof S03PacketTimeUpdate)
            event.setCancelled(true);
    }
}
