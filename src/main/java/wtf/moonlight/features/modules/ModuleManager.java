package wtf.moonlight.features.modules;

import lombok.Getter;
import wtf.moonlight.MoonLight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.KeyPressEvent;
import wtf.moonlight.features.command.CommandManager;
import wtf.moonlight.features.command.impl.ModuleCommand;
import wtf.moonlight.features.modules.impl.combat.*;
import wtf.moonlight.features.modules.impl.exploit.*;
import wtf.moonlight.features.modules.impl.exploit.Timer;
import wtf.moonlight.features.modules.impl.misc.*;
import wtf.moonlight.features.modules.impl.movement.*;
import wtf.moonlight.features.modules.impl.player.*;
import wtf.moonlight.features.modules.impl.visual.*;
import wtf.moonlight.features.modules.impl.world.BedNuker;
import wtf.moonlight.features.modules.impl.world.Scaffold;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ModuleManager {

    private final List<Module> modules = new CopyOnWriteArrayList<>();

    public ModuleManager() {
        addModules(

                //combat
                Annoy.class,
                AntiBot.class,
                AutoClicker.class,
                AutoGap.class,
                AutoGap2.class,
                AutoPot.class,
                AutoProjectile.class,
                AutoRod.class,
                AutoWeapon.class,
                BackTrack.class,
                BowAimBot.class,
                Critical.class,
                KeepRange.class,
                KeepSprint.class,
                KillAura.class,
                LagRange.class,
                MoreKB.class,
                Reach.class,
                TargetStrafe.class,
                TickBase.class,
                Velocity.class,

                //exploit,
                Blink.class,
                ClientSpoofer.class,
                Disabler.class,
                FakeLag.class,
                NoRotate.class,
                Timer.class,
                AntiHunger.class,

                //misc
                AutoAuthenticate.class,
                AutoPlay.class,
                HackerDetector.class,
                ItemAlerts.class,
                KillSults.class,

                //movement
                AntiFall.class,
                Freeze.class,
                InvMove.class,
                LongJump.class,
                NoJumpDelay.class,
                NoSlowdown.class,
                Phase.class,
                SafeWalk.class,
                Speed.class,
                Sprint.class,
                Step.class,
                Strafe.class,
                Fly.class,

                //player
                AntiFireball.class,
                AutoPearl.class,
                AutoTool.class,
                FastPlace.class,
                InvManager.class,
                NoFall.class,
                Stealer.class,

                //visual
                Atmosphere.class,
                Animations.class,
                AspectRatio.class,
                AttackEffect.class,
                BedPlates.class,
                Camera.class,
                Chams.class,
                ClickGUI.class,
                DashTrail.class,
                DeadEffect.class,
                ESP.class,
                FinalKills.class,
                FireFlies.class,
                FreeLook.class,
                FullBright.class,
                GifTest.class,
                GlowESP.class,
                Hat.class,
                HitBubbles.class,
                Indicators.class,
                Interface.class,
                JumpCircles.class,
                LineGlyphs.class,
                Rotation.class,
                Shaders.class,
                Trajectories.class,
                TargetESP.class,

                //world
                BedNuker.class,
                Scaffold.class

        );

        modules.sort(Comparator.comparing(Module::getName));

        MoonLight.INSTANCE.getEventManager().register(this);
    }

    @SafeVarargs
    public final void addModules(Class<? extends Module>... moduleClasses) {
        for (Class<? extends Module> moduleClass : moduleClasses) {
            try {
                Module module = moduleClass.getDeclaredConstructor().newInstance();
                modules.add(module);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Class<?>> getAllClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        List<Class<?>> classes = new ArrayList<>();

        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }

    public <module extends Module> module getModule(Class<? extends module> moduleClass) {
        Iterator<Module> var2 = this.modules.iterator();
        Module module;
        do {
            if (!var2.hasNext()) {
                return null;
            }
            module = var2.next();
        } while (module.getClass() != moduleClass);

        return (module) module;
    }

    @SuppressWarnings("unchecked")
    public Module getModule(String name) {
        for (Module feature : getModules()) {
            if (feature.getName().equalsIgnoreCase(name)) {
                return feature;
            }
        }
        return null;
    }

    public ArrayList<Module> getModulesByCategory(ModuleCategory category) {
        ArrayList<Module> ms = new ArrayList<>();
        for (Module m : getModules()) if (m.getCategory() == category) ms.add(m);
        return ms;
    }

    @EventTarget
    public void onKey(KeyPressEvent event) {
        for (Module module : getModules()) {
            if (module.getKeyBind() == event.getKey()) {
                module.toggle();
            }
        }
    }
}