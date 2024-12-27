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
package wtf.moonlight.gui.altmanager.group;

import lombok.NonNull;

import java.util.function.Function;

public class GroupSupplierLine<T> implements GroupLine<T> {

    @NonNull
    private final Function<T, String> stringFunction;

    protected GroupSupplierLine(@NonNull Function<T, String> stringFunction) {
        this.stringFunction = stringFunction;
    }

    @NonNull
    static <T> GroupSupplierLine<T> of(@NonNull Function<T, String> stringFunction) {
        return new GroupSupplierLine<>(stringFunction);
    }

    @Override
    public String getText(T t) {
        return this.stringFunction.apply(t);
    }

}
