package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModPotions {

    private ModPotions() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Potion> CALMING_POTION = POTIONS.register("calming", () -> new Potion(new MobEffectInstance(ModEffects.CALMING.get(), 6000)));
    public static final RegistryObject<Potion> LONG_CALMING_POTION = POTIONS.register("long_calming", () -> new Potion(new MobEffectInstance(ModEffects.CALMING.get(), 12000)));

    private static final TagKey<Item> HONEY_BOTTLE_TAG = ItemTags.create(new ResourceLocation("forge", "honey_bottle"));

    public static void createMixes() {
        addMix(Potions.AWKWARD, HONEY_BOTTLE_TAG, CALMING_POTION.get());
        addMix(CALMING_POTION.get(), Tags.Items.DUSTS_GLOWSTONE, LONG_CALMING_POTION.get());
    }

    private static void addMix(Potion basePotion, TagKey<Item> fromTag, Potion outputPotion) {
        ItemStack splashPotion = new ItemStack(Items.SPLASH_POTION);
        ItemStack lingeringPotion = new ItemStack(Items.LINGERING_POTION);
        Ingredient dragonBreath = Ingredient.of(Items.DRAGON_BREATH);
        Ingredient baseIngredient = Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), basePotion));
        Ingredient tagIngredient = Ingredient.of(fromTag);
        ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), outputPotion);
        Ingredient potionIngredient = Ingredient.of(outputStack);
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(baseIngredient, tagIngredient, outputStack));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(potionIngredient, Ingredient.of(Tags.Items.GUNPOWDER), PotionUtils.setPotion(splashPotion, outputPotion)));
        BrewingRecipeRegistry.addRecipe(potionIngredient, dragonBreath, PotionUtils.setPotion(lingeringPotion, outputPotion));
    }
}
