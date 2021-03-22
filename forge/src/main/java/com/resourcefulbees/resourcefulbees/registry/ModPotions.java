package com.resourcefulbees.resourcefulbees.registry;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
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

    public static final RegistryObject<Potion> CALMING_POTION = POTIONS.register("calming", () -> new Potion(new MobEffectInstance(ModEffects.CALMING.get(), 6000)));
    public static final RegistryObject<Potion> LONG_CALMING_POTION = POTIONS.register("long_calming", () -> new Potion(new MobEffectInstance(ModEffects.CALMING.get(), 12000)));

    private static final Tag<Item> HONEY_BOTTLE_TAG = ItemTags.createOptional(new ResourceLocation("forge", "honey_bottle"));

    public static void createMixes() {
        addMix(Potions.AWKWARD, Ingredient.of(HONEY_BOTTLE_TAG), CALMING_POTION.get());
        addMix(CALMING_POTION.get(), Ingredient.of(Items.GLOWSTONE_DUST), LONG_CALMING_POTION.get());
    }

    private static void addMix(Potion basePotion, Ingredient fromTag, Potion outputPotion) {
        ItemStack splashPotion = new ItemStack(Items.SPLASH_POTION);
        ItemStack lingeringPotion = new ItemStack(Items.LINGERING_POTION);
        Ingredient gunpowder = Ingredient.of(Tags.Items.GUNPOWDER);
        Ingredient dragonBreath = Ingredient.of(Items.DRAGON_BREATH);
        Ingredient baseIngredient = Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), basePotion));
        ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), outputPotion);
        Ingredient potionIngredient = Ingredient.of(outputStack);
        BrewingRecipeRegistry.addRecipe(baseIngredient, fromTag, outputStack);
        BrewingRecipeRegistry.addRecipe(potionIngredient, gunpowder, PotionUtils.setPotion(splashPotion, outputPotion));
        BrewingRecipeRegistry.addRecipe(potionIngredient, dragonBreath, PotionUtils.setPotion(lingeringPotion, outputPotion));
    }
}
