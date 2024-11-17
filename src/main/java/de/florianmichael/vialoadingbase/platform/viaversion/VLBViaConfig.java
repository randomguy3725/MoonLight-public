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

import com.viaversion.viaversion.configuration.AbstractViaConfig;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VLBViaConfig extends AbstractViaConfig {

    // Stolen from Sponge
    private final static List<String> UNSUPPORTED = Arrays.asList("anti-xray-patch", "bungee-ping-interval",
            "bungee-ping-save", "bungee-servers", "quick-move-action-fix", "nms-player-ticking",
            "velocity-ping-interval", "velocity-ping-save", "velocity-servers",
            "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox",
            "show-shield-when-sword-in-hand", "left-handed-handling");


    public VLBViaConfig(File configFile) {
        super(configFile);

        this.reload();
    }

    @Override
    public URL getDefaultConfigURL() {
        return getClass().getClassLoader().getResource("assets/viaversion/config.yml");
    }

    @Override
    protected void handleConfig(Map<String, Object> config) {
        // Nothing Currently
    }

    @Override
    public List<String> getUnsupportedOptions() {
        return UNSUPPORTED;
    }
}
