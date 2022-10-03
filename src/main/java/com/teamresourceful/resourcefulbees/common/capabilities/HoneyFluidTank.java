package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class HoneyFluidTank extends FluidTank {

    private static final Predicate<FluidStack> FLUID_VALIDATOR = fluidStack -> fluidStack.getFluid().is(ModTags.Fluids.HONEY);

    public HoneyFluidTank(int capacity) {
        this(capacity, FLUID_VALIDATOR);
    }

    public HoneyFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public void fillBottle(Player player, InteractionHand hand) {
        fillBottle(this, player, hand);
    }

    public static void fillBottle(FluidTank tank, Player player, InteractionHand hand){
        FluidStack tankFluid = tank.getFluid();
        FluidStack fluidStack = new FluidStack(tankFluid, ModConstants.HONEY_PER_BOTTLE);
        ItemStack itemStack = new ItemStack(getHoneyBottleFromFluid(tankFluid.getFluid()), 1);
        if (itemStack.isEmpty()) return;
        if (tank.isEmpty()) return;
        if (tankFluid.getAmount() >= ModConstants.HONEY_PER_BOTTLE) {
            FluidStack drain = tank.drain(fluidStack, FluidAction.EXECUTE);
            if (!drain.isEmpty()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getCount() > 1) {
                    stack.setCount(stack.getCount() - 1);
                    player.addItem(itemStack);
                } else {
                    player.setItemInHand(hand, itemStack);
                }
                player.level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public void emptyBottle(Player player, InteractionHand hand) {
        emptyBottle(this, player, hand);
    }

    public static void emptyBottle(FluidTank tank, Player player, InteractionHand hand){
        FluidStack fluidStack = new FluidStack(getHoneyFluidFromBottle(player.getItemInHand(hand)), ModConstants.HONEY_PER_BOTTLE);
        if (fluidStack.isEmpty()) return;
        FluidStack tankFluid = tank.getFluid();
        if (!tankFluid.isFluidEqual(fluidStack) && !tank.isEmpty()) {
            return;
        }
        if (tank.getFluidAmount() + ModConstants.HONEY_PER_BOTTLE <= tank.getTankCapacity(0) && tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE) != 0) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItem(new ItemStack(Items.GLASS_BOTTLE, 1));
            } else {
                player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE, 1));
            }
            player.level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get().getSource();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            HoneyFluidData fluidData = HoneyRegistry.getRegistry().getHoneyData(honey.getHoneyData().name()).fluidData();
            return fluidData.stillFluid().get();
        }
        return Fluids.EMPTY;
    }

    @Nullable
    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid honeyFluid) {
            return HoneyRegistry.getRegistry().getHoneyData(honeyFluid.getHoneyData().name()).bottleData().honeyBottle().get();
        }
        if (fluid.is(ModTags.Fluids.HONEY)) {
            return Items.HONEY_BOTTLE;
        }
        return null;
    }
}
