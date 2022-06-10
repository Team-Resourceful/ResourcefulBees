package com.teamresourceful.resourcefulbees.common.recipe.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.recipe.base.CodecRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeSerializers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record SolidificationRecipe(ResourceLocation id, FluidStack fluid, ItemStack stack) implements CodecRecipe<Container> {

    public static Codec<SolidificationRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> id)).forGetter(SolidificationRecipe::getId),
                CodecUtils.FLUID_STACK_CODEC.fieldOf("fluid").forGetter(SolidificationRecipe::fluid),
                CodecUtils.ITEM_STACK_CODEC.fieldOf("result").forGetter(SolidificationRecipe::stack)
        ).apply(instance, SolidificationRecipe::new));
    }

    public static Optional<SolidificationRecipe> findRecipe(RecipeManager manager, FluidStack fluid) {
        return manager
                .getAllRecipesFor(ModRecipeTypes.SOLIDIFICATION_RECIPE_TYPE.get())
                .stream()
                .filter(recipe -> recipe.fluid().isFluidEqual(fluid)).findFirst();
    }

    public static boolean matches(RecipeManager manager, FluidStack fluid) {
        return findRecipe(manager, fluid).isPresent();
    }

    @Override
    public boolean matches(@NotNull Container inventory, @NotNull Level world) {
        return false;
    }

    @Override
    public @NotNull
    RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOLIDIFICATION_RECIPE.get();
    }

    @Override
    public @NotNull
    RecipeType<?> getType() {
        return ModRecipeTypes.SOLIDIFICATION_RECIPE_TYPE.get();
    }
}
