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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.VersionInfo;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.viaversion.VLBViaAPIWrapper;
import de.florianmichael.vialoadingbase.platform.viaversion.VLBViaConfig;
import de.florianmichael.vialoadingbase.util.VLBTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ViaVersionPlatformImpl implements ViaPlatform<UUID> {

    private final ViaAPI<UUID> api = new VLBViaAPIWrapper();

    private final Logger logger;
    private final VLBViaConfig config;

    public ViaVersionPlatformImpl(final Logger logger) {
        this.logger = logger;
        config = new VLBViaConfig(new File(ViaLoadingBase.getInstance().getRunDirectory(), "viaversion.yml"));
    }

    public static List<ProtocolVersion> createVersionList() {
        final List<ProtocolVersion> versions = new ArrayList<>(ProtocolVersion.getProtocols()).stream().filter(protocolVersion -> protocolVersion != ProtocolVersion.unknown && ProtocolVersion.getProtocols().indexOf(protocolVersion) >= 7).collect(Collectors.toList());
        Collections.reverse(versions);
        return versions;
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        return new ViaCommandSender[0];
    }

    @Override
    public void sendMessage(UUID uuid, String msg) {
        if (uuid == null) {
            this.getLogger().info(msg);
        } else {
            this.getLogger().info("[" + uuid + "] " + msg);
        }
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        return false;
    }

    @Override
    public boolean disconnect(UserConnection connection, String message) {
        return ViaPlatform.super.disconnect(connection, message);
    }

    @Override
    public VLBTask runAsync(Runnable runnable) {
        return new VLBTask(Via.getManager().getScheduler().execute(runnable));
    }

    @Override
    public VLBTask runRepeatingAsync(Runnable runnable, long ticks) {
        return new VLBTask(Via.getManager().getScheduler().scheduleRepeating(runnable, 0, ticks * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public VLBTask runSync(Runnable runnable) {
        return this.runAsync(runnable);
    }

    @Override
    public VLBTask runSync(Runnable runnable, long ticks) {
        return new VLBTask(Via.getManager().getScheduler().schedule(runnable, ticks * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public VLBTask runRepeatingSync(Runnable runnable, long ticks) {
        return this.runRepeatingAsync(runnable, ticks);
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    @Override
    public void onReload() {
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public ViaVersionConfig getConf() {
        return config;
    }

    @Override
    public ViaAPI<UUID> getApi() {
        return api;
    }

    @Override
    public File getDataFolder() {
        return ViaLoadingBase.getInstance().getRunDirectory();
    }

    @Override
    public String getPluginVersion() {
        return VersionInfo.VERSION;
    }

    @Override
    public String getPlatformName() {
        return "ViaLoadingBase by FlorianMichael";
    }

    @Override
    public String getPlatformVersion() {
        return ViaLoadingBase.VERSION;
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
    }

    public VLBViaConfig getConfig() {
        return config;
    }

    @Override
    public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        return ViaPlatform.super.getUnsupportedSoftwareClasses();
    }

    @Override
    public boolean hasPlugin(String s) {
        return false;
    }

    @Override
    public JsonObject getDump() {
        if (ViaLoadingBase.getInstance().getDumpSupplier() == null) return new JsonObject();

        return ViaLoadingBase.getInstance().getDumpSupplier().get();
    }
}
