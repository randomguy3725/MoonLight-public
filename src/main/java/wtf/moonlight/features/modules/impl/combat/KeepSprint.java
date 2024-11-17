package wtf.moonlight.features.modules.impl.combat;

import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.AttackEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.values.impl.ModeValue;

@ModuleInfo(name = "KeepSprint", category = ModuleCategory.Combat)
public class KeepSprint extends Module {

    public final ModeValue mode = new ModeValue("Mode", new String[]{"Vanilla"}, "Vanilla", this);
}
