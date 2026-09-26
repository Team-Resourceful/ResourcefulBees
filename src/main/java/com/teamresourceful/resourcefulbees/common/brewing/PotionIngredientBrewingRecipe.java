package com.teamresourceful.resourcefulbees.common.brewing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipeSerializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class PotionIngredientBrewingRecipe extends BrewingRecipe {

    public static final MapCodec<BrewingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    PotionsPredicate.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput().potions().orElseThrow()),
                    Ingredient.CODEC.fieldOf("reagent").forGetter(recipe -> recipe.getReagent().ingredient()),
                    PotionContents.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput().components().get(DataComponentMap.EMPTY, DataComponents.POTION_CONTENTS))
            ).apply(instance, PotionIngredientBrewingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC = StreamCodec.composite(
            PotionsPredicate.STREAM_CODEC,
            recipe -> recipe.getInput().potions().orElseThrow(),

            Ingredient.CONTENTS_STREAM_CODEC,
            recipe -> recipe.getReagent().ingredient(),

            PotionContents.STREAM_CODEC,
            recipe -> recipe.getOutput().components().get(DataComponentMap.EMPTY, DataComponents.POTION_CONTENTS),

            PotionIngredientBrewingRecipe::new
    );

    public PotionIngredientBrewingRecipe(
            PotionsPredicate input,
            Ingredient reagent,
            PotionContents output
    ) {
        super(
                new PotionIngredient(Ingredient.of(Items.POTION), Optional.of(input)),
                new PotionIngredient(reagent, Optional.empty()),
                new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                                .set(DataComponents.POTION_CONTENTS, output)
                                .build())
        );
    }

    @Override
    public boolean matches(BrewingInput brew) {
        return getInput()
                .potions()
                .orElseThrow()
                .matches(brew.input().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY))
                && getReagent().test(brew.reagent());
    }

    @Override
    public @NonNull ItemStack assemble(BrewingInput brew) {
        ItemStack result = brew.input().copyWithCount(1);
        result.applyComponents(getOutput().components());
        return result;
    }

    @Override
    public @NonNull RecipeSerializer<BrewingRecipe> getSerializer() {
        return ModRecipeSerializers.POTION_INGREDIENT_BREWING.get();
    }
}