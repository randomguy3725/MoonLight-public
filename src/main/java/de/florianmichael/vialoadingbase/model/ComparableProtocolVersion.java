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

package de.florianmichael.vialoadingbase.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;

public class ComparableProtocolVersion extends ProtocolVersion {
    private final int index;

    public ComparableProtocolVersion(final int version, final String name, final int index) {
        super(version, name);
        this.index = index;
    }

    public boolean isOlderThan(final ProtocolVersion other) {
        return this.getIndex() > ViaLoadingBase.fromProtocolVersion(other).getIndex();
    }

    public boolean isOlderThanOrEqualTo(final ProtocolVersion other) {
        return this.getIndex() >= ViaLoadingBase.fromProtocolVersion(other).getIndex();
    }

    public boolean isNewerThan(final ProtocolVersion other) {
        return this.getIndex() < ViaLoadingBase.fromProtocolVersion(other).getIndex();
    }

    public boolean isNewerThanOrEqualTo(final ProtocolVersion other) {
        return this.getIndex() <= ViaLoadingBase.fromProtocolVersion(other).getIndex();
    }

    public boolean isEqualTo(final ProtocolVersion other) {
        return this.getIndex() == ViaLoadingBase.fromProtocolVersion(other).getIndex();
    }

    public int getIndex() {
        return index;
    }
}
