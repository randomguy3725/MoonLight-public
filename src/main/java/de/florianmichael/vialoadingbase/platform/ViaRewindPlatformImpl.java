/*
 * This file is part of ViaLoadingBase - https://github.com/FlorianMichael/ViaLoadingBase
 * Copyright (C) 2020-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.vialoadingbase.platform;

import com.viaversion.viarewind.api.ViaRewindPlatform;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.item.ItemSword;
import wtf.moonlight.utils.InstanceAccess;

import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatformImpl implements ViaRewindPlatform, InstanceAccess {

    public ViaRewindPlatformImpl(final File directory) {
        this.init(new File(directory, "viarewind.yml"));
    }

    @Override
    public Logger getLogger() {
        return ViaLoadingBase.LOGGER;
    }

    @Override
    public boolean isSword() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return false;
        } else {
            return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
        }
    }
}
