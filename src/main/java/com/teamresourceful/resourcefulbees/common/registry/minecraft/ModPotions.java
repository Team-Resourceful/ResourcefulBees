package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.recipe.TagPotionRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPotions {

    private ModPotions() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Potion> CALMING_POTION = POTIONS.register("calming", () -> new Potion(new EffectInstance(ModEffects.CALMING.get(), 6000)));
    public static final RegistryObject<Potion> LONG_CALMING_POTION = POTIONS.register("long_calming", () -> new Potion(new EffectInstance(ModEffects.CALMING.get(), 12000)));

    private static final Tags.IOptionalNamedTag<Item> HONEY_BOTTLE_TAG = ItemTags.createOptional(new ResourceLocation("forge", "honey_bottle"));

    public static void createMixes() {
        addMix(Potions.AWKWARD, HONEY_BOTTLE_TAG, CALMING_POTION.get());
        addMix(CALMING_POTION.get(), Tags.Items.DUSTS_GLOWSTONE, LONG_CALMING_POTION.get());
    }

    private static void addMix(Potion basePotion, ITag<Item> fromTag, Potion outputPotion) {
        ItemStack splashPotion = new ItemStack(Items.SPLASH_POTION);
        ItemStack lingeringPotion = new ItemStack(Items.LINGERING_POTION);
        Ingredient dragonBreath = Ingredient.of(Items.DRAGON_BREATH);
        Ingredient baseIngredient = Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), basePotion));
        ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), outputPotion);
        Ingredient potionIngredient = Ingredient.of(outputStack);
        BrewingRecipeRegistry.addRecipe(new TagPotionRecipe(baseIngredient, fromTag, outputStack));
        BrewingRecipeRegistry.addRecipe(new TagPotionRecipe(potionIngredient, Tags.Items.GUNPOWDER, PotionUtils.setPotion(splashPotion, outputPotion)));
        BrewingRecipeRegistry.addRecipe(potionIngredient, dragonBreath, PotionUtils.setPotion(lingeringPotion, outputPotion));
    }
}
