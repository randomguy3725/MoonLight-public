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
package wtf.moonlight.gui.altmanager.repository.hypixel;

import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HypixelProfileFactory {

    private HypixelProfileFactory() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static @NotNull HypixelProfile hypixelProfile(@NotNull String rank, int level) {
        return HypixelProfile.of(rank, level, null, null, null, null, null, null, System.currentTimeMillis());
    }

    public static @NotNull HypixelProfile hypixelProfile(@Nullable String rank,
                                                         int level,
                                                         @Nullable String guild,
                                                         @Nullable Integer achievementPoints,
                                                         @Nullable Integer quests,
                                                         @Nullable Integer friends,
                                                         @Nullable Long firstLogin,
                                                         @Nullable Long lastLogin,
                                                         @Nullable Long cached) {
        return HypixelProfile.of(rank, level, guild, achievementPoints, quests, friends, firstLogin, lastLogin, cached);
    }

    @Nullable
    public static HypixelProfile fromNBT(@Nullable NBTTagCompound tagCompound) {
        if (tagCompound == null) return null;

        String rank = tagCompound.getString("rank", "Default");
        int level = tagCompound.getInteger("level", 1);
        String guild = tagCompound.getString("rank", null);
        int achievementPoints = tagCompound.getInteger("ap", 0);
        int quests = tagCompound.getInteger("quests", 0);
        int friends = tagCompound.getInteger("friends", 0);
        Long firstLogin = tagCompound.getLong("first_login", null);
        Long lastLogin = tagCompound.getLong("last_login", null);
        Long cached = tagCompound.getLong("cached", null);

        return HypixelProfile.of(rank, level, guild, achievementPoints, quests, friends, firstLogin, lastLogin, cached);
    }

}
