package com.teamresourceful.resourcefulbees.common.setup;

import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakEnchantment;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.items.locator.DimensionalBeeHolder;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipes.ingredients.BeeJarIngredient;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.*;
import com.teamresourceful.resourcefulbees.common.resources.conditions.LoadDevRecipes;
import com.teamresourceful.resourcefulbees.common.worldgen.GoldenFlower;
import com.teamresourceful.resourcefulbees.platform.common.events.*;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.ServerGoingToStartEvent;
import com.teamresourceful.resourcefulbees.platform.common.registry.RegistryHelper;
import com.teamresourceful.resourcefulbees.platform.common.registry.potion.PotionRegistry;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.ConditionRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.levelgen.Heightmap;

public final class GameSetup {

    //TODO Change to common tag for forge and fabric.
    private static final TagKey<Item> HONEY_BOTTLE_TAG = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "honey_bottles"));

    private GameSetup() {
        throw new UtilityClassError();
    }

    public static void initEvents() {
        CommandRegisterEvent.EVENT.addListener(ResourcefulBeesCommand::registerCommand);
        PlayerBrokeBlockEvent.EVENT.addListener(HiveBreakEnchantment::onBlockBreak);
        BlockBonemealedEvent.EVENT.addListener(GoldenFlower::onBlockBonemealed);
        SyncedDatapackEvent.EVENT.addListener(DimensionalBeeHolder::onDatapackSync);
        RegisterIngredientsEvent.EVENT.addListener(GameSetup::initIngredients);
        ServerGoingToStartEvent.EVENT.addListener(ModStructures::addStructures);
        RegisterBurnablesEvent.EVENT.addListener(GameSetup::initBurnables);
        RegisterSpawnPlacementsEvent.EVENT.addListener(GameSetup::initSpawns);
    }

    public static void initSerializersAndConditions() {
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
}
