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

package de.florianmichael.vialoadingbase;

import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import de.florianmichael.vialoadingbase.model.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.model.Platform;
import de.florianmichael.vialoadingbase.platform.ViaBackwardsPlatformImpl;
import de.florianmichael.vialoadingbase.platform.ViaRewindPlatformImpl;
import de.florianmichael.vialoadingbase.platform.ViaVersionPlatformImpl;
import de.florianmichael.vialoadingbase.platform.viaversion.VLBViaCommandHandler;
import de.florianmichael.vialoadingbase.platform.viaversion.VLBViaInjector;
import de.florianmichael.vialoadingbase.platform.viaversion.VLBViaProviders;
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ViaLoadingBase {
    public final static String VERSION = "${vialoadingbase_version}";
    public final static Logger LOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaLoadingBase"));

    public final static Platform PSEUDO_VIA_VERSION = new Platform("ViaVersion", () -> true, () -> {
        // Empty
    }, protocolVersions -> protocolVersions.addAll(ViaVersionPlatformImpl.createVersionList()));
    public final static Platform PLATFORM_VIA_BACKWARDS = new Platform("ViaBackwards", () -> inClassPath("com.viaversion.viabackwards.api.ViaBackwardsPlatform"), () -> new ViaBackwardsPlatformImpl(Via.getManager().getPlatform().getDataFolder()));
    public final static Platform PLATFORM_VIA_REWIND = new Platform("ViaRewind", () -> inClassPath("com.viaversion.viarewind.api.ViaRewindPlatform"), () -> new ViaRewindPlatformImpl(Via.getManager().getPlatform().getDataFolder()));

    public final static Map<ProtocolVersion, ComparableProtocolVersion> PROTOCOLS = new LinkedHashMap<>();
    private static ViaLoadingBase instance;

    private final LinkedList<Platform> platforms;
    private final File runDirectory;
    private final int nativeVersion;
    private final BooleanSupplier forceNativeVersionCondition;
    private final Supplier<JsonObject> dumpSupplier;
    private final Consumer<ViaProviders> providers;
    private final Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer;
    private final Consumer<ComparableProtocolVersion> onProtocolReload;

    private ComparableProtocolVersion nativeProtocolVersion;
    private ComparableProtocolVersion targetProtocolVersion;

    public ViaLoadingBase(LinkedList<Platform> platforms, File runDirectory, int nativeVersion, BooleanSupplier forceNativeVersionCondition, Supplier<JsonObject> dumpSupplier, Consumer<ViaProviders> providers, Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer, Consumer<ComparableProtocolVersion> onProtocolReload) {
        this.platforms = platforms;

        this.runDirectory = new File(runDirectory, "ViaLoadingBase");
        this.nativeVersion = nativeVersion;
        this.forceNativeVersionCondition = forceNativeVersionCondition;
        this.dumpSupplier = dumpSupplier;
        this.providers = providers;
        this.managerBuilderConsumer = managerBuilderConsumer;
        this.onProtocolReload = onProtocolReload;

        instance = this;
        initPlatform();
    }

    public ComparableProtocolVersion getTargetVersion() {
        if (forceNativeVersionCondition != null && forceNativeVersionCondition.getAsBoolean()) return nativeProtocolVersion;

        return targetProtocolVersion;
    }
    public void reload(final int version) {
        reload(ProtocolVersion.getProtocol(version));
    }
    public void reload(final ProtocolVersion protocolVersion) {
        reload(fromProtocolVersion(protocolVersion));
    }

    public void reload(final ComparableProtocolVersion protocolVersion) {
        this.targetProtocolVersion = protocolVersion;

        if (this.onProtocolReload != null) this.onProtocolReload.accept(targetProtocolVersion);
    }

    public void initPlatform() {
        for (Platform platform : platforms) platform.createProtocolPath();
        for (ProtocolVersion preProtocol : Platform.TEMP_INPUT_PROTOCOLS) PROTOCOLS.put(preProtocol, new ComparableProtocolVersion(preProtocol.getVersion(), preProtocol.getName(), Platform.TEMP_INPUT_PROTOCOLS.indexOf(preProtocol)));

        this.nativeProtocolVersion = fromProtocolVersion(ProtocolVersion.getProtocol(this.nativeVersion));
        this.targetProtocolVersion = this.nativeProtocolVersion;

        final ViaVersionPlatformImpl viaVersionPlatform = new ViaVersionPlatformImpl(ViaLoadingBase.LOGGER);
        final ViaManagerImpl.ViaManagerBuilder builder = ViaManagerImpl.builder().
                platform(viaVersionPlatform).
                loader(new VLBViaProviders()).
                injector(new VLBViaInjector()).
                commandHandler(new VLBViaCommandHandler())
                ;

        if (this.managerBuilderConsumer != null) this.managerBuilderConsumer.accept(builder);

        Via.init(builder.build());

        final ViaManagerImpl manager = (ViaManagerImpl) Via.getManager();
        manager.addEnableListener(() -> {
            for (Platform platform : this.platforms) platform.build(ViaLoadingBase.LOGGER);
        });

        manager.init();
        manager.onServerLoaded();
        manager.getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
        manager.getProtocolManager().setMaxPathDeltaIncrease(-1);
        ((ProtocolManagerImpl) manager.getProtocolManager()).refreshVersions();

        ViaLoadingBase.LOGGER.info("ViaLoadingBase has loaded " + Platform.COUNT + "/" + platforms.size() + " platforms");
    }

    public static ViaLoadingBase getInstance() {
        return instance;
    }

    public List<Platform> getSubPlatforms() {
        return platforms;
    }

    public File getRunDirectory() {
        return runDirectory;
    }

    public int getNativeVersion() {
        return nativeVersion;
    }

    public Supplier<JsonObject> getDumpSupplier() {
        return dumpSupplier;
    }

    public Consumer<ViaProviders> getProviders() {
        return providers;
    }

    public static boolean inClassPath(final String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static ComparableProtocolVersion fromProtocolVersion(final ProtocolVersion protocolVersion) {
        return PROTOCOLS.get(protocolVersion);
    }

    public static ComparableProtocolVersion fromProtocolId(final int protocolId) {
        return PROTOCOLS.values().stream().filter(protocol -> protocol.getVersion() == protocolId).findFirst().orElse(null);
    }

    public static List<ProtocolVersion> getProtocols() {
        return new LinkedList<>(PROTOCOLS.keySet());
    }

    public static class ViaLoadingBaseBuilder {
        private final LinkedList<Platform> platforms = new LinkedList<>();

        private File runDirectory;
        private Integer nativeVersion;
        private BooleanSupplier forceNativeVersionCondition;
        private Supplier<JsonObject> dumpSupplier;
        private Consumer<ViaProviders> providers;
        private Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer;
        private Consumer<ComparableProtocolVersion> onProtocolReload;

        public ViaLoadingBaseBuilder() {
            platforms.add(PSEUDO_VIA_VERSION);

            platforms.add(PLATFORM_VIA_BACKWARDS);
            platforms.add(PLATFORM_VIA_REWIND);
        }

        public static ViaLoadingBaseBuilder create() {
            return new ViaLoadingBaseBuilder();
        }

        public ViaLoadingBaseBuilder platform(final Platform platform) {
            this.platforms.add(platform);
            return this;
        }

        public ViaLoadingBaseBuilder platform(final Platform platform, final int position) {
            this.platforms.add(position, platform);
            return this;
        }

        public ViaLoadingBaseBuilder runDirectory(final File runDirectory) {
            this.runDirectory = runDirectory;
            return this;
        }

        public ViaLoadingBaseBuilder nativeVersion(final int nativeVersion) {
            this.nativeVersion = nativeVersion;
            return this;
        }

        public ViaLoadingBaseBuilder forceNativeVersionCondition(final BooleanSupplier forceNativeVersionCondition) {
            this.forceNativeVersionCondition = forceNativeVersionCondition;
            return this;
        }

        public ViaLoadingBaseBuilder dumpSupplier(final Supplier<JsonObject> dumpSupplier) {
            this.dumpSupplier = dumpSupplier;
            return this;
        }

        public ViaLoadingBaseBuilder providers(final Consumer<ViaProviders> providers) {
            this.providers = providers;
            return this;
        }

        public ViaLoadingBaseBuilder managerBuilderConsumer(final Consumer<ViaManagerImpl.ViaManagerBuilder> managerBuilderConsumer) {
            this.managerBuilderConsumer = managerBuilderConsumer;
            return this;
        }

        public ViaLoadingBaseBuilder onProtocolReload(final Consumer<ComparableProtocolVersion> onProtocolReload) {
            this.onProtocolReload = onProtocolReload;
            return this;
        }

        public void build() {
            if (ViaLoadingBase.getInstance() != null) {
                ViaLoadingBase.LOGGER.severe("ViaLoadingBase has already started the platform!");
                return;
            }
            if (runDirectory == null || nativeVersion == null) {
                ViaLoadingBase.LOGGER.severe("Please check your ViaLoadingBaseBuilder arguments!");
                return;
            }
            new ViaLoadingBase(platforms, runDirectory, nativeVersion, forceNativeVersionCondition, dumpSupplier, providers, managerBuilderConsumer, onProtocolReload);
        }
    }
}
