package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.impl.player.MoveInputEvent;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        MoveInputEvent event = new MoveInputEvent(moveForward,moveStrafe, jump, sneak);

        Moonlight.INSTANCE.getEventManager().call(event);

        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();

        this.jump = event.isJumping();
        this.sneak = event.isSneaking();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
