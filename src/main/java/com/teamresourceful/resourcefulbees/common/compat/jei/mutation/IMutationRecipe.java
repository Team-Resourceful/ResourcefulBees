package com.teamresourceful.resourcefulbees.common.compat.jei.mutation;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public interface IMutationRecipe {

    CustomBeeData getBeeData();
    double chance();
    double weight();
    default Optional<CompoundNBT> getNBT() { return Optional.empty(); }
    default Optional<ItemStack> getInputItem() { return Optional.empty(); }
    default Optional<ItemStack> getOutputItem() { return Optional.empty(); }
    default Optional<FluidStack> getInputFluid() { return Optional.empty(); }
    default Optional<FluidStack> getOutputFluid() { return Optional.empty(); }
    default Optional<EntityType<?>> getInputEntity() { return Optional.empty(); }
    default Optional<EntityType<?>> getOutputEntity() { return Optional.empty(); }

}
