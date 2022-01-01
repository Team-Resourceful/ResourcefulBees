package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.TagConstants;
import com.resourcefulbees.resourcefulbees.recipe.LazyLoadedTagIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModPotions {

    private ModPotions() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, ResourcefulBees.MOD_ID);

    public static final RegistryObject<Potion> CALMING_POTION = POTIONS.register("calming", () -> new Potion(new EffectInstance(ModEffects.CALMING.get(), 6000)));
    public static final RegistryObject<Potion> LONG_CALMING_POTION = POTIONS.register("long_calming", () -> new Potion(new EffectInstance(ModEffects.CALMING.get(), 12000)));

    public static void createMixes() {
        LOGGER.info("Generation Potion Mixes");
        addMix(Potions.AWKWARD, TagConstants.RESOURCEFUL_HONEY_BOTTLE, CALMING_POTION.get());
        addMix(CALMING_POTION.get(), Tags.Items.DUSTS_GLOWSTONE, LONG_CALMING_POTION.get());
    }

    private static void addMix(Potion basePotion, ITag<Item> fromTag, Potion outputPotion) {
        LOGGER.info("Generating Potion Recipe for: {}", outputPotion.getRegistryName());
        ItemStack splashPotion = new ItemStack(Items.SPLASH_POTION);
        ItemStack lingeringPotion = new ItemStack(Items.LINGERING_POTION);
        Ingredient dragonBreath = Ingredient.of(Items.DRAGON_BREATH);
        Ingredient baseIngredient = Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), basePotion));
        ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), outputPotion);
        Ingredient potionIngredient = Ingredient.of(outputStack);
        BrewingRecipeRegistry.addRecipe(baseIngredient, new LazyLoadedTagIngredient(fromTag), outputStack);
        BrewingRecipeRegistry.addRecipe(potionIngredient, new LazyLoadedTagIngredient(Tags.Items.GUNPOWDER), PotionUtils.setPotion(splashPotion, outputPotion));
        BrewingRecipeRegistry.addRecipe(potionIngredient, dragonBreath, PotionUtils.setPotion(lingeringPotion, outputPotion));
    }
}
