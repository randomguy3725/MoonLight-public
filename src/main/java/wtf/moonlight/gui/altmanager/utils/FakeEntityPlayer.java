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
package wtf.moonlight.gui.altmanager.utils;

import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.resources.DefaultPlayerSkin.TEXTURE_ALEX;
import static net.minecraft.client.resources.DefaultPlayerSkin.TEXTURE_STEVE;

public final class FakeEntityPlayer extends EntityOtherPlayerMP {

    private static final List<ItemStack> ITEM_STACKS = Arrays.asList( // @off
            new ItemStack(Items.bow),
            new ItemStack(Items.iron_sword),
            new ItemStack(Items.wooden_sword),
            new ItemStack(Items.stone_pickaxe),
            new ItemStack(Items.diamond_pickaxe),
            new ItemStack(Items.iron_ingot),
            new ItemStack(Blocks.cobblestone),
            new ItemStack(Blocks.red_flower)
    );

    /* fields */
    private final ResourceLocation locationSkin;
    private final JsonParser jsonParser = new JsonParser();
    private String skinType;

    /* constructors */
    public FakeEntityPlayer(@NotNull GameProfile profile, @Nullable ResourceLocation locationSkin) {
        super(new FakeWorld(), profile);

        try {
            gameProfile.getProperties().asMap().get("textures").stream().findFirst().map(property -> {
                return jsonParser.parse(new String(Base64.getDecoder().decode(property.getValue()))).getAsJsonObject()
                        .getAsJsonObject("textures").getAsJsonObject("SKIN");
            }).ifPresent(jsonObject -> {
                this.skinType = jsonObject.has("metadata") ? jsonObject.getAsJsonObject("metadata").get("model").getAsString() : "default";
            });
        } catch(Throwable ignored) {
        } finally {
            if(skinType == null) this.skinType = "slim";
        }

        if(locationSkin != null) {
            this.locationSkin = locationSkin;
        } else {
            this.locationSkin = skinType.equals("default") ? TEXTURE_STEVE : TEXTURE_ALEX;
        }

        setCurrentItemOrArmor(0, ITEM_STACKS.get(ThreadLocalRandom.current().nextInt(ITEM_STACKS.size())));
    }

    /* methods */
    // region Lombok
    @Override
    public String getSkinType() {
        return skinType;
    }

    @Override
    @Nullable
    public ResourceLocation getLocationSkin() {
        return locationSkin;
    }


}
