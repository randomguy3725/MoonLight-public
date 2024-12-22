/*
 * This file is part of ViaMCP - https://github.com/FlorianMichael/ViaMCP
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

package de.florianmichael.viamcp.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public class AttackOrder {
    private final static Minecraft mc = Minecraft.getMinecraft();

    public static void sendConditionalSwing(MovingObjectPosition mop) {
        if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) mc.thePlayer.swingItem();
    }

    public static void sendFixedAttack(EntityPlayer entityIn, Entity target) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(entityIn, target);
        } else {
            mc.playerController.attackEntity(entityIn, target);
            mc.thePlayer.swingItem();
        }
    }

    public static void sendFixedAttackNoPacketEvent(EntityPlayer entityIn, Entity target) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntityNoPacketEvent(entityIn, target);
        } else {
            mc.playerController.attackEntityNoPacketEvent(entityIn, target);
            mc.thePlayer.swingItem();
        }
    }
}
