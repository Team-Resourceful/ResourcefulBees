package com.teamresourceful.resourcefulbees.common.setup;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.data.DataPackLoader;
import com.teamresourceful.resourcefulbees.common.data.RecipeBuilder;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakEnchantment;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.entities.villager.Beekeeper;
import com.teamresourceful.resourcefulbees.common.items.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.common.recipes.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.registries.dynamic.ModSpawnData;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.*;
import com.teamresourceful.resourcefulbees.common.resources.conditions.LoadDevRecipes;
import com.teamresourceful.resourcefulbees.common.worldgen.GoldenFlower;
import com.teamresourceful.resourcefulbees.platform.common.events.*;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.ServerGoingToStartEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.registry.RegisterRepositorySourceEvent;
import com.teamresourceful.resourcefulbees.platform.common.registry.RegistryHelper;
import com.teamresourceful.resourcefulbees.platform.common.registry.potion.PotionRegistry;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.ConditionRegistry;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

public final class GameSetup {

    //TODO Change to common tag for forge and fabric.
    private static final TagKey<Item> HONEY_BOTTLE_TAG = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "honey_bottles"));

    private GameSetup() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static void initEvents() {
        CommandRegisterEvent.EVENT.addListener(ResourcefulBeesCommand::registerCommand);
        PlayerBrokeBlockEvent.EVENT.addListener(HiveBreakEnchantment::onBlockBreak);
        BlockBonemealedEvent.EVENT.addListener(GoldenFlower::onBlockBonemealed);
        SyncedDatapackEvent.EVENT.addListener(DimensionalBeeHolder::onDatapackSync);
        RegisterIngredientsEvent.EVENT.addListener(GameSetup::initIngredients);
        ServerGoingToStartEvent.EVENT.addListener(ModStructures::addStructures);
        ServerGoingToStartEvent.EVENT.addListener(ModSpawnData::initialize);
        RegisterBurnablesEvent.EVENT.addListener(GameSetup::initBurnables);
        RegisterSpawnPlacementsEvent.EVENT.addListener(GameSetup::initSpawns);
        RegisterReloadListenerEvent.EVENT.addListener(RecipeBuilder::registerReloadListeners);
        RegisterVillagerTradesEvent.EVENT.addListener(Beekeeper::setupBeekeeper);
        RegisterEntityAttributesEvent.EVENT.addListener(GameSetup::registerAttributes);
        RegisterHiveBreakBlocksEvent.EVENT.addListener(GameSetup::onHiveBreakConversions);
        RegisterRepositorySourceEvent.EVENT.addListener(GameSetup::registerRepoistorySources);
    }

    public static void init() {
        ConditionRegistry.registerCondition(new LoadDevRecipes());
    }

    public static void initPotionRecipes() {
        PotionRegistry.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(HONEY_BOTTLE_TAG), ModPotions.CALMING_POTION.get());
        PotionRegistry.registerPotionRecipe(ModPotions.CALMING_POTION.get(), Ingredient.of(Items.GLOWSTONE_DUST), ModPotions.LONG_CALMING_POTION.get());
    }

    public static void initArguments() {
        RegistryHelper.register(ModArguments.BEE_TYPE, BeeArgument.class);
    }

    public static void initIngredients(RegisterIngredientsEvent event) {
        event.register(BeeJarIngredient.SERIALIZER);
    }

    public static void initBurnables(RegisterBurnablesEvent event) {
        event.register(400, ModItems.WAX.get());
        event.register(4000, ModItems.WAX_BLOCK_ITEM.get());
    }

    public static void initSpawns(RegisterSpawnPlacementsEvent event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                event.register(entityType.get(),
                        SpawnPlacements.Type.ON_GROUND,
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        CustomBeeEntity::canBeeSpawn
                )
        );
    }

    public static void registerAttributes(RegisterEntityAttributesEvent event) {
        ModEntities.getModBees().forEach((s, entityType) -> event.register(
                entityType.get(),
                BeeRegistry.get().getBeeData(s).getCombatData().buildAttributes(Mob.createMobAttributes()).build()
        ));
    }

    public static void registerRepoistorySources(RegisterRepositorySourceEvent event) {
        if (event.type().equals(PackType.SERVER_DATA)) {
            event.register(DataPackLoader.INSTANCE);
        }
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

    public static void initPaths() {
        ModConstants.LOGGER.info("Setting up config paths...");

        try (FileWriter file = new FileWriter(Paths.get(ModPaths.RESOURCES.toAbsolutePath().toString(), "pack.mcmeta").toFile())) {
            int clientVersion = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
            String mcMetaContent = "{\"pack\":{\"pack_format\":" + clientVersion + ",\"description\":\"Resourceful Bees resource pack used for lang purposes for the user to add lang for bee/items.\"}}";
            file.write(mcMetaContent);
        } catch (FileAlreadyExistsException ignored) { //ignored
        } catch (IOException e) {
            ModConstants.LOGGER.error("Failed to create pack.mcmeta file for resource loading");
        }
    }
}
