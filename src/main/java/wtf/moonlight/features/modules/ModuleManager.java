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
package wtf.moonlight.features.modules;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.misc.KeyPressEvent;
import wtf.moonlight.features.modules.impl.combat.*;
import wtf.moonlight.features.modules.impl.exploit.*;
import wtf.moonlight.features.modules.impl.misc.*;
import wtf.moonlight.features.modules.impl.movement.*;
import wtf.moonlight.features.modules.impl.player.*;
import wtf.moonlight.features.modules.impl.visual.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all modules within the MoonLight client.
 * Responsible for initializing, registering, and handling modules.
 */
@Getter
public class ModuleManager {

    private final List<Module> modules = new CopyOnWriteArrayList<>();

    /**
     * Initializes the ModuleManager by adding all available modules,
     * sorting them by name, and registering event listeners.
     */
    public ModuleManager() {
        addModules(
                // Combat
                Annoy.class,
                AntiBot.class,
                AutoGap.class,
                AutoPot.class,
                AutoProjectile.class,
                AutoWeapon.class,
                BackTrack.class,
                BowAimBot.class,
                Critical.class,
                KeepSprint.class,
                KillAura.class,
                Reach.class,
                TargetStrafe.class,
                TickBase.class,
                Velocity.class,

                // Legit
                AutoClicker.class,
                AutoRod.class,
                MoreKB.class,
                LagRange.class,
                BlockHit.class,
                KeepRange.class,

                // Exploit
                Blink.class,
                ClientSpoofer.class,
                Disabler.class,
                FakeLag.class,
                NoRotate.class,
                Timer.class,
                AntiHunger.class,

                // Misc
                AutoAuthenticate.class,
                AutoPlay.class,
                HackerDetector.class,
                ItemAlerts.class,
                KillSults.class,

                // Movement
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
                Scaffold.class,

                // Player
                AntiFireball.class,
                AutoPearl.class,
                AutoTool.class,
                FastPlace.class,
                InvManager.class,
                NoFall.class,
                Stealer.class,
                BedNuker.class,

                // Visual
                Atmosphere.class,
                Animations.class,
                AspectRatio.class,
                AttackEffect.class,
                BedPlates.class,
                BlockOverlay.class,
                Camera.class,
                Chams.class,
                ChestESP.class,
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
                TargetESP.class
        );

        // Sort modules alphabetically by name for better organization
        modules.sort(Comparator.comparing(Module::getName));

        // Register the ModuleManager to listen for events
        Moonlight.INSTANCE.getEventManager().register(this);
      //  Moonlight.LOGGER.INFO("ModuleManager initialized with {} modules.", modules.size());
    }

    /**
     * Adds multiple modules to the manager by instantiating their classes.
     *
     * @param moduleClasses Varargs of module classes to add.
     */
    @SafeVarargs
    public final void addModules(Class<? extends Module>... moduleClasses) {
        for (Class<? extends Module> moduleClass : moduleClasses) {
            try {
                Module module = moduleClass.getDeclaredConstructor().newInstance();
                modules.add(module);
                //  Moonlight.LOGGER.INFO("Added module: {}", module.getName());
            } catch (Exception e) {
                //  Moonlight.LOGGER.INFO("Failed to instantiate module: {}", moduleClass.getSimpleName(), e);
            }
        }
    }

    /**
     * Retrieves a module instance based on its class type.
     *
     * @param moduleClass The class of the module to retrieve.
     * @param <T>         The type of the module.
     * @return An instance of the requested module or null if not found.
     */
    public <T extends Module> T getModule(Class<T> moduleClass) {
        Optional<Module> module = modules.stream()
                .filter(m -> m.getClass().equals(moduleClass))
                .findFirst();

        return module.map(moduleClass::cast).orElse(null);
    }

    /**
     * Retrieves a module instance based on its name.
     *
     * @param name The name of the module to retrieve.
     * @return The module instance if found, otherwise null.
     */
    public Module getModule(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all modules that belong to a specific category.
     *
     * @param category The category to filter modules by.
     * @return A list of modules within the specified category.
     */
    public List<Module> getModulesByCategory(ModuleCategory category) {
        List<Module> categorizedModules = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                categorizedModules.add(module);
            }
        }
        return categorizedModules;
    }

    /**
     * Event handler for key press events.
     * Toggles the corresponding module if its keybind matches the pressed key.
     *
     * @param event The key press event.
     */
    @EventTarget
    public void onKey(KeyPressEvent event) {
        modules.stream()
                .filter(module -> module.getKeyBind() == event.getKey())
                .forEach(Module::toggle);
    }

    /**
     * Retrieves all modules managed by this manager.
     *
     * @return An unmodifiable list of all modules.
     */
    public List<Module> getAllModules() {
        return List.copyOf(modules);
    }
}
