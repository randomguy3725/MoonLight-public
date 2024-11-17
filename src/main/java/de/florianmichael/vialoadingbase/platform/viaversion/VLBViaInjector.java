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

package de.florianmichael.vialoadingbase.platform.viaversion;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.florianmichael.vialoadingbase.netty.VLBPipeline;

public class VLBViaInjector implements ViaInjector {

    @Override
    public void inject() {
    }

    @Override
    public void uninject() {
    }

    @Override
    public String getDecoderName() {
        return VLBPipeline.VIA_DECODER_HANDLER_NAME;
    }

    @Override
    public String getEncoderName() {
        return VLBPipeline.VIA_ENCODER_HANDLER_NAME;
    }

    @Override
    public IntSortedSet getServerProtocolVersions() {
        final IntSortedSet versions = new IntLinkedOpenHashSet();
        for (ProtocolVersion value : ProtocolVersion.getProtocols()) {
            if (value.getVersion() >= ProtocolVersion.v1_7_1.getVersion()) {
                versions.add(value.getVersion());
            }
        }

        return versions;
    }

    @Override
    public int getServerProtocolVersion() {
        return this.getServerProtocolVersions().firstInt();
    }

    @Override
    public JsonObject getDump() {
        return new JsonObject();
    }
}
