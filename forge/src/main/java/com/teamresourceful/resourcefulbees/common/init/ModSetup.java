package com.teamresourceful.resourcefulbees.common.init;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public final class ModSetup {

    private ModSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void initialize() {
        setupPaths();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ModSetup::loadResources);
    }

    private static void setupPaths() {
        LOGGER.info("Setting up config paths...");

        try (FileWriter file = new FileWriter(Paths.get(ModPaths.RESOURCES.toAbsolutePath().toString(), "pack.mcmeta").toFile())) {
            String mcMetaContent = "{\"pack\":{\"pack_format\":"+ PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()) +",\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
        } catch (FileAlreadyExistsException ignored) { //ignored
        } catch (IOException e) { LOGGER.error("Failed to create pack.mcmeta file for resource loading");}
    }

    private static void loadResources() {
        //This is needed for data gen as Minecraft.getInstance() is null in data gen.
        //noinspection ConstantConditions
        if (Minecraft.getInstance() == null) return;

        Minecraft.getInstance().getResourcePackRepository().addPackFinder((consumer, factory) -> {
            final Pack packInfo = Pack.create(
                    ResourcefulBees.MOD_ID,
                    true,
                    () -> new FolderPackResources(ModPaths.RESOURCES.toFile()) {
                        @Override
                        public boolean isHidden() {
                            return true;
                        }
                    },
                    factory,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );
            if (packInfo == null) {
                LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            consumer.accept(packInfo);
        });
    }
}
