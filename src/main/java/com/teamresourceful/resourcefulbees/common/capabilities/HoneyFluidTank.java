package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

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
        FluidStack fluidStack = new FluidStack(tank.getFluid(), ModConstants.HONEY_PER_BOTTLE);
        ItemStack itemStack = new ItemStack(BeeInfoUtils.getHoneyBottleFromFluid(tank.getFluid().getFluid()), 1);
        if (tank.isEmpty()) return;
        if (tank.getFluidAmount() >= ModConstants.HONEY_PER_BOTTLE) {
            tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
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

    public void emptyBottle(Player player, InteractionHand hand) {
        emptyBottle(this, player, hand);
    }

    public static void emptyBottle(FluidTank tank, Player player, InteractionHand hand){
        FluidStack fluidStack = new FluidStack(BeeInfoUtils.getHoneyFluidFromBottle(player.getItemInHand(hand)), ModConstants.HONEY_PER_BOTTLE);
        if (!tank.getFluid().isFluidEqual(fluidStack) && !tank.isEmpty()) {
            return;
        }
        if (tank.getFluidAmount() + ModConstants.HONEY_PER_BOTTLE <= tank.getTankCapacity(0)) {
            tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
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
}
