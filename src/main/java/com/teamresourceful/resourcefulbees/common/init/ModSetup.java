package com.teamresourceful.resourcefulbees.common.init;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.ModPaths;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

import static com.teamresourceful.resourcefulbees.ResourcefulBees.LOGGER;

public class ModSetup {

    private ModSetup() {
        throw new IllegalStateException("Utility Class");
    }

    public static void initialize() {
        setupPaths();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ModSetup::loadResources);
    }

    private static void setupPaths() {
        LOGGER.info("Setting up config paths...");

        try (FileWriter file = new FileWriter(Paths.get(ModPaths.RESOURCES.toAbsolutePath().toString(), "pack.mcmeta").toFile())) {
            String mcMetaContent = "{\"pack\":{\"pack_format\":6,\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
        } catch (FileAlreadyExistsException ignored) { //ignored
        } catch (IOException e) { LOGGER.error("Failed to create pack.mcmeta file for resource loading");}
    }

    private static void loadResources() {
        Minecraft.getInstance().getResourcePackRepository().addPackFinder((consumer, factory) -> {
            final ResourcePackInfo packInfo = ResourcePackInfo.create(
                    ResourcefulBees.MOD_ID,
                    true,
                    () -> new FolderPack(ModPaths.RESOURCES.toFile()) {
                        @Override
                        public boolean isHidden() {
                            return true;
                        }
                    },
                    factory,
                    ResourcePackInfo.Priority.TOP,
                    IPackNameDecorator.BUILT_IN
            );
            if (packInfo == null) {
                LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            consumer.accept(packInfo);
        });
    }
}
