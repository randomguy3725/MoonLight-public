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
package wtf.moonlight.events.impl;
/**
 * An interface for objects that can be cancelled.
 * Implementing classes can indicate whether they are cancelled or not using the provided methods.
 */
public interface Cancellable {

    /**
     * Checks if the object is cancelled.
     *
     * @return {@code true} if the object is cancelled, {@code false} otherwise.
     */
    boolean isCancelled();

    /**
     * Sets the cancellation state of the object.
     *
     * @param state {@code true} to cancel the object, {@code false} to uncancel it.
     */
    void setCancelled(boolean state);
}
