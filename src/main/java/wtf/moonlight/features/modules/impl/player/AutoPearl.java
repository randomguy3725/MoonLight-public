/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.features.modules.impl.player;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import org.joml.Vector2f;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.player.MotionEvent;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.ModuleInfo;
import wtf.moonlight.features.modules.impl.movement.Freeze;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.math.TimerUtils;
import wtf.moonlight.utils.misc.DebugUtils;
import wtf.moonlight.utils.player.FallDistanceComponent;
import wtf.moonlight.utils.player.PlayerUtils;
import wtf.moonlight.utils.player.ProjectileUtils;

@ModuleInfo(name = "AutoPearl", category = ModuleCategory.Player)
public class AutoPearl extends Module {
    private CalculateThread calculateThread;
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
        }
        if (event.isPost() && this.calculating && (this.calculateThread == null || this.calculateThread.completed)) {
            this.calculating = false;
            if (this.calculateThread != null) {
                getModule(Freeze.class).throwPearl(this.calculateThread.solution);
            }
        }
        final boolean overVoid = !mc.thePlayer.onGround && !PlayerUtils.isBlockUnder(30.0, true);
        if (!this.attempted && !mc.thePlayer.onGround && overVoid && FallDistanceComponent.distance > 2.0f) {
            FallDistanceComponent.distance = 0.0f;
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack != null && stack.getItem() instanceof ItemEnderPearl && slot >= 36) {
                    this.bestPearlSlot = slot;
                    mc.thePlayer.inventory.currentItem = this.bestPearlSlot - 36;
                }
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            DebugUtils.sendMessage(String.valueOf(this.bestPearlSlot));
            if (!(mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                return;
            }
            this.calculating = true;
            (this.calculateThread = new CalculateThread(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 0.0, 0.0)).start();
            this.getModule(Freeze.class).setEnabled(true);
        }
    }

    private static class CalculateThread extends Thread {
        private int iteration;
        private boolean completed;
        private double temperature;
        private double energy;
        private Vector2f solution;
        public boolean stop;
        private final ProjectileUtils.EnderPearlPredictor predictor;

        private CalculateThread(final double predictX, final double predictY, final double predictZ, final double minMotionY, final double maxMotionY) {
            this.predictor = new ProjectileUtils.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
            this.completed = false;
        }

        @Override
        public void run() {
            final TimerUtils timer = new TimerUtils();
            timer.reset();
            this.solution = new Vector2f((float) MathUtils.randomizeDouble(-180, 180), (float) MathUtils.randomizeDouble(-90, 90));
            Vector2f current = this.solution;
            this.energy = this.predictor.assessRotation(this.solution);
            double solutionE = this.energy;
            while (this.temperature >= 1.0E-4 && !this.stop) {
                final Vector2f rotation = new Vector2f((float) (current.x + MathUtils.randomizeDouble(-this.temperature * 18.0, this.temperature * 18.0)), (float) (current.y + MathUtils.randomizeDouble(-this.temperature * 9.0, this.temperature * 9.0)));
                if (rotation.y > 90.0f) {
                    rotation.y = 90.0f;
                }
                if (rotation.y < -90.0f) {
                    rotation.y = -90.0f;
                }
                final double assessment = this.predictor.assessRotation(rotation);
                final double deltaE = assessment - this.energy;
                if (deltaE >= 0.0 || MathUtils.randomizeDouble(0, 1) < Math.exp(-deltaE / this.temperature * 100.0)) {
                    this.energy = assessment;
                    current = rotation;
                    if (assessment > solutionE) {
                        solutionE = assessment;
                        this.solution = new Vector2f(rotation.x, rotation.y);
                        DebugUtils.sendMessage("Find a better solution: (" + this.solution.x + ", " + this.solution.y + "), value: " + solutionE);
                    }
                }
                this.temperature *= 0.997;
                ++this.iteration;
            }
            DebugUtils.sendMessage("Simulated annealing completed within " + this.iteration + " iterations");
            DebugUtils.sendMessage("Time used: " + timer.getTime() + " solution energy: " + solutionE);
            this.completed = true;
        }
    }
}