package com.teamresourceful.resourcefulbees.common.init;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.platform.common.events.RegisterHiveBreakBlocksEvent;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

public final class ModSetup {

    private ModSetup() {
        throw new UtilityClassError();
    }

    public static void initialize() {
        setupPaths();
        RegisterHiveBreakBlocksEvent.EVENT.addListener(ModSetup::onHiveBreakConversions);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ModSetup::loadResources);
    }

    private static void setupPaths() {
        ModConstants.LOGGER.info("Setting up config paths...");

        try (FileWriter file = new FileWriter(Paths.get(ModPaths.RESOURCES.toAbsolutePath().toString(), "pack.mcmeta").toFile())) {
            String mcMetaContent = "{\"pack\":{\"pack_format\":"+ PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion()) +",\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
        } catch (FileAlreadyExistsException ignored) { //ignored
        } catch (IOException e) {
            ModConstants.LOGGER.error("Failed to create pack.mcmeta file for resource loading");
        }
    }

    private static void loadResources() {
        //This is needed for data gen as Minecraft.getInstance() is null in data gen.
        //noinspection ConstantConditions
        if (Minecraft.getInstance() == null) return;

        Minecraft.getInstance().getResourcePackRepository().addPackFinder((consumer, factory) -> {
            final Pack packInfo = Pack.create(
                    ModConstants.MOD_ID,
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
                ModConstants.LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            consumer.accept(packInfo);
        });
    }

    public static void onHiveBreakConversions(RegisterHiveBreakBlocksEvent event) {
        event.register(() -> Blocks.STRIPPED_ACACIA_LOG, ModBlocks.ACACIA_BEE_NEST);
        event.register(() -> Blocks.STRIPPED_BIRCH_LOG, ModBlocks.BIRCH_BEE_NEST);
        event.register(() -> Blocks.STRIPPED_JUNGLE_LOG, ModBlocks.JUNGLE_BEE_NEST);
        event.register(() -> Blocks.STRIPPED_OAK_LOG, ModBlocks.OAK_BEE_NEST);
        event.register(() -> Blocks.STRIPPED_SPRUCE_LOG, ModBlocks.SPRUCE_BEE_NEST);
        event.register(() -> Blocks.STRIPPED_DARK_OAK_LOG, ModBlocks.DARK_OAK_BEE_NEST);
        event.register(() -> Blocks.GRASS_BLOCK, ModBlocks.GRASS_BEE_NEST);
        event.register(() -> Blocks.CRIMSON_NYLIUM, ModBlocks.CRIMSON_NYLIUM_BEE_NEST);
        event.register(() -> Blocks.CRIMSON_STEM, ModBlocks.CRIMSON_BEE_NEST);
        event.register(() -> Blocks.WARPED_NYLIUM, ModBlocks.WARPED_NYLIUM_BEE_NEST);
        event.register(() -> Blocks.WARPED_STEM, ModBlocks.WARPED_BEE_NEST);
        event.register(() -> Blocks.RED_MUSHROOM_BLOCK, ModBlocks.RED_MUSHROOM_BEE_NEST);
        event.register(() -> Blocks.BROWN_MUSHROOM_BLOCK, ModBlocks.BROWN_MUSHROOM_BEE_NEST);
        event.register(() -> Blocks.PURPUR_BLOCK, ModBlocks.PURPUR_BEE_NEST);
        event.register(() -> Blocks.NETHERRACK, ModBlocks.NETHER_BEE_NEST);
        event.register(() -> Blocks.PRISMARINE, ModBlocks.PRISMARINE_BEE_NEST);
    }
}
