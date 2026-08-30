package com.teamresourceful.resourcefulbees.common.data;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public final class ConfigDatapack implements RepositorySource {

    public static final ConfigDatapack INSTANCE = new ConfigDatapack();

    private static final String PACK_ID = "resourcefulbees:config_resources";

    private static final Component TITLE = Component.literal("Resourceful Bees Config Resources");

    private static final Path ROOT = FMLPaths.CONFIGDIR.get()
            .resolve("resourcefulbees")
            .resolve("resources");

    private ConfigDatapack() {
    }

    @Override
    public void loadPacks(@NonNull Consumer<Pack> onLoad) {
        if (!Files.isDirectory(ROOT)) {
            return;
        }

        PackLocationInfo info = new PackLocationInfo(PACK_ID, TITLE, PackSource.BUILT_IN, Optional.empty());
        PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, true);
        Pack.ResourcesSupplier resources = new PathPackResources.PathResourcesSupplier(ROOT);
        Pack pack = Pack.readMetaAndCreate(info, resources, PackType.SERVER_DATA, selectionConfig);

        if (pack != null) {
            onLoad.accept(pack);
        }
    }
}