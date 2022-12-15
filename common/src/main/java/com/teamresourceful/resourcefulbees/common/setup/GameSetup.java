package com.teamresourceful.resourcefulbees.common.setup;

import com.teamresourceful.resourcefulbees.common.commands.ResourcefulBeesCommand;
import com.teamresourceful.resourcefulbees.common.commands.arguments.BeeArgument;
import com.teamresourceful.resourcefulbees.common.enchantments.HiveBreakEnchantment;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModArguments;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModPotions;
import com.teamresourceful.resourcefulbees.common.resources.conditions.LoadDevRecipes;
import com.teamresourceful.resourcefulbees.common.worldgen.GoldenFlower;
import com.teamresourceful.resourcefulbees.platform.common.events.BlockBonemealedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.CommandRegisterEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.PlayerBrokeBlockEvent;
import com.teamresourceful.resourcefulbees.platform.common.registry.RegistryHelper;
import com.teamresourceful.resourcefulbees.platform.common.registry.potion.PotionRegistry;
import com.teamresourceful.resourcefulbees.platform.common.resources.conditions.ConditionRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public final class GameSetup {

    //TODO Change to common tag for forge and fabric.
    private static final TagKey<Item> HONEY_BOTTLE_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "honey_bottles"));

    private GameSetup() {
        throw new UtilityClassError();
    }

    public static void initEvents() {
        CommandRegisterEvent.EVENT.addListener(ResourcefulBeesCommand::registerCommand);
        PlayerBrokeBlockEvent.EVENT.addListener(HiveBreakEnchantment::onBlockBreak);
        BlockBonemealedEvent.EVENT.addListener(GoldenFlower::onBlockBonemealed);
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
}
