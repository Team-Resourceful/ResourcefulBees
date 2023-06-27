package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.items.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class FluidUtils {

    private FluidUtils() {
        throw new UtilityClassError();
    }

    public static final Codec<FluidStack> FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").orElse(1000).forGetter(FluidStack::getAmount),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(o -> Optional.ofNullable(o.getTag()))
    ).apply(instance, (fluid, amount, tag) -> new FluidStack(fluid, amount, tag.orElse(null))));

    public static Predicate<RecipeFluid> fluidsMatch(FluidStack stack) {
        return recipeFluid -> recipeFluid.fluid() == stack.getFluid() && Objects.equals(recipeFluid.tag(), stack.getTag());
    }

    public static FluidStack fromRecipe(RecipeFluid fluid) {
        return new FluidStack(fluid.fluid(), fluid.amount(), fluid.tag());
    }

    public static void fillBottle(IFluidTank tank, Player player, InteractionHand hand) {
        FluidStack tankFluid = tank.getFluid();
        FluidStack fluidStack = new FluidStack(tankFluid, BeeConstants.HONEY_PER_BOTTLE);
        ItemStack itemStack = new ItemStack(getHoneyBottleFromFluid(tankFluid.getFluid()), 1);
        if (itemStack.isEmpty()) return;
        if (tank.getFluid().isEmpty()) return;
        if (tankFluid.getAmount() >= BeeConstants.HONEY_PER_BOTTLE) {
            FluidStack drain = tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            if (!drain.isEmpty()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getCount() > 1) {
                    stack.setCount(stack.getCount() - 1);
                    player.addItem(itemStack);
                } else {
                    player.setItemInHand(hand, itemStack);
                }
                player.level().playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public static void emptyBottle(IFluidTank tank, Player player, InteractionHand hand) {
        FluidStack fluidStack = new FluidStack(getHoneyFluidFromBottle(player.getItemInHand(hand)), BeeConstants.HONEY_PER_BOTTLE);
        if (fluidStack.isEmpty()) return;
        FluidStack tankFluid = tank.getFluid();
        if (!tankFluid.isFluidEqual(fluidStack) && !tank.getFluid().isEmpty()) {
            return;
        }
        if (tank.getFluidAmount() + BeeConstants.HONEY_PER_BOTTLE <= tank.getCapacity() && tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE) != 0) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItem(new ItemStack(Items.GLASS_BOTTLE, 1));
            } else {
                player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE, 1));
            }
            player.level().playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get().getSource();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            String id = honey.getHoneyData().id();
            if (id.isEmpty()) return Fluids.EMPTY;
            HoneyFluidData fluidData = HoneyRegistry.get().getHoneyData(id).getFluidData();
            return fluidData.stillFluid().get();
        }
        return Fluids.EMPTY;
    }

    @Nullable
    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid honeyFluid) {
            String id = honeyFluid.getHoneyData().id();
            if (id.isEmpty()) return null;
            return HoneyRegistry.get().getHoneyData(id).getBottleData().bottle().get();
        }
        if (fluid.is(ModFluidTags.HONEY)) {
            return Items.HONEY_BOTTLE;
        }
        return null;
    }

    public static void checkBottleAndCapability(IFluidTank tank, BlockEntity entity, Player player, Level level, BlockPos pos, InteractionHand hand) {
        Item item = player.getItemInHand(hand).getItem();
        if (item instanceof BottleItem) {
            fillBottle(tank, player, hand);
        } else if (item instanceof HoneyBottleItem) {
            emptyBottle(tank, player, hand);
        } else {
            ModUtils.capabilityOrGuiUse(entity, player, level, pos, hand);
        }
    }
}
