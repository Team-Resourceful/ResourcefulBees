package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.utils.FluidUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class HoneyFluidTank extends FluidTank {

    private static final Predicate<FluidStack> FLUID_VALIDATOR = fluidStack -> fluidStack.getFluid().is(ModFluidTags.HONEY);

    public HoneyFluidTank(int capacity) {
        this(capacity, FLUID_VALIDATOR);
    }

    public HoneyFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    //TODO do we still need these methods? are they for API purposes?
    public void fillBottle(Player player, InteractionHand hand) {
        FluidUtils.fillBottle(this, player, hand);
    }

    public void emptyBottle(Player player, InteractionHand hand) {
        FluidUtils.emptyBottle(this, player, hand);
    }
}
