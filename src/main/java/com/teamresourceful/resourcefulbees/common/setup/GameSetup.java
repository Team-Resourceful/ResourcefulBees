package com.teamresourceful.resourcefulbees.common.setup;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.data.ConfigDatapack;
import com.teamresourceful.resourcefulbees.common.data.InMemoryDatapack;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModArguments;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.SharedConstants;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.transfer.fluid.BucketResourceHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public final class GameSetup {

    private static final TagKey<Item> HONEY_BOTTLE_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "honey_bottles"));

    private GameSetup() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void initEvents() {
//        CommandRegisterEvent.EVENT.addListener(ResourcefulBeesCommand::registerCommand);
//        //PlayerBrokeBlockEvent.EVENT.addListener(HiveBreakEnchantment::onBlockBreak);
//        BlockBonemealedEvent.EVENT.addListener(GoldenFlower::onBlockBonemealed);
//        //SyncedDatapackEvent.EVENT.addListener(DimensionalBeeHolder::onDatapackSync);
//        //RegisterIngredientsEvent.EVENT.addListener(GameSetup::initIngredients);
//        //ServerGoingToStartEvent.EVENT.addListener(ModStructures::addStructures);
//        ServerGoingToStartEvent.EVENT.addListener(ModSpawnData::initialize);
//        RegisterBurnablesEvent.EVENT.addListener(GameSetup::initBurnables);
//        RegisterSpawnPlacementsEvent.EVENT.addListener(GameSetup::initSpawns);
        //RegisterReloadListenerEvent.EVENT.addListener(RecipeBuilder::registerReloadListeners);
        //RegisterVillagerTradesEvent.EVENT.addListener(Beekeeper::setupBeekeeper);
        //RegisterEntityAttributesEvent.EVENT.addListener(GameSetup::registerAttributes);
        //RegisterHiveBreakBlocksEvent.EVENT.addListener(GameSetup::onHiveBreakConversions);
    }

    public static void initPotionRecipes() {
        //PotionRegistry.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(HONEY_BOTTLE_TAG), ModPotions.CALMING_POTION.get());
        //PotionRegistry.registerPotionRecipe(ModPotions.CALMING_POTION.get(), Ingredient.of(Items.GLOWSTONE_DUST), ModPotions.LONG_CALMING_POTION.get());
    }

    public static void initArguments() {
        ArgumentTypeInfos.registerByClass(BeeArgument.class, ModArguments.BEE_TYPE.get());
    }


    public static void initSpawns(RegisterSpawnPlacementsEvent event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                event.register(entityType.get(),
                        SpawnPlacementTypes.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        CustomBeeEntity::canBeeSpawn,
                        RegisterSpawnPlacementsEvent.Operation.REPLACE
                )
        );
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ModEntities.getModBees().forEach((s, entityType) -> event.put(
                entityType.get(),
                BeeRegistry.get().getBeeData(s).getCombatData().buildAttributes(Mob.createMobAttributes()).build()
        ));
    }

    public static void registerRepositorySources(AddPackFindersEvent event) {
        if (event.getPackType().equals(PackType.SERVER_DATA)) {
            event.addRepositorySource(InMemoryDatapack.INSTANCE);
            event.addRepositorySource(ConfigDatapack.INSTANCE);
        }

        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(consumer -> {
                PackLocationInfo locationInfo = new PackLocationInfo(ModConstants.MOD_ID, Component.literal(ModConstants.MOD_ID), PackSource.BUILT_IN, Optional.empty());
                PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
                Pack pack = Pack.readMetaAndCreate(locationInfo, new PathPackResources.PathResourcesSupplier(ModPaths.RESOURCES), PackType.CLIENT_RESOURCES, selectionConfig);

                if (pack == null) {
                    ModConstants.LOGGER.error("Failed to load Resourceful Bees client resource pack.");
                    return;
                }

                consumer.accept(pack);
            });
        }
    }

    public static void initPaths() {
        ModConstants.LOGGER.info("Setting up config paths...");

        Path packMeta = ModPaths.RESOURCES.resolve("pack.mcmeta");

        if (Files.exists(packMeta)) {
            return;
        }

        try {
            Files.createDirectories(ModPaths.RESOURCES);

            var packVersion = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES);

            String mcMetaContent = """
                {
                  "pack": {
                    "min_format": [%d, %d],
                    "max_format": [%d, %d],
                    "description": "Resourceful Bees resource pack used for user-provided bee/item translations."
                  }
                }
                """.formatted(
                    packVersion.major(),
                    packVersion.minor(),
                    packVersion.major(),
                    packVersion.minor()
            );

            Files.writeString(packMeta, mcMetaContent, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        } catch (FileAlreadyExistsException _) {
        } catch (IOException e) {
            ModConstants.LOGGER.error("Failed to create resource pack metadata at {}", packMeta, e);
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntityTypes.T1_APIARY_ENTITY.get(), (apiaryBlock, side) -> apiaryBlock.resourceHandler());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), (blockEntity, side) -> blockEntity.tank());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), (blockEntity, side) -> blockEntity.tank());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntityTypes.HONEY_POT_TILE_ENTITY.get(), (blockEntity, context) -> blockEntity.tank());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), (blockEntity, context) -> blockEntity.tank());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), (blockEntity, context) -> blockEntity.tank());


        event.registerItem(Capabilities.Fluid.ITEM, (object, context) ->  new BucketResourceHandler(context), ModItems.HONEY_BUCKET.get());
    }
}
