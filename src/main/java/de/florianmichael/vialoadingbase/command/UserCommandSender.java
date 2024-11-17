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

package de.florianmichael.vialoadingbase.command;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.UUID;

public class UserCommandSender implements ViaCommandSender {
    private final UserConnection user;

    public UserCommandSender(UserConnection user) {
        this.user = user;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public void sendMessage(String s) {
        Via.getPlatform().sendMessage(getUUID(), s);
    }

    @Override
    public UUID getUUID() {
        return user.getProtocolInfo().getUuid();
    }

    @Override
    public String getName() {
        return user.getProtocolInfo().getUsername();
    }
}
