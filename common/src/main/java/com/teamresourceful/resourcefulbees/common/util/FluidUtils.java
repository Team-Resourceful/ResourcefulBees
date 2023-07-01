package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.items.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class FluidUtils {

    public static FluidHolder exactExtract(FluidContainer container, FluidHolder holder) {
        FluidHolder simulated = container.internalExtract(holder, true);
        if (simulated.getFluidAmount() == holder.getFluidAmount()) {
            return container.internalExtract(holder, false);
        }
        return FluidHooks.emptyFluid();
    }

    public static long exactInsert(FluidContainer container, FluidHolder holder) {
        long simulated = container.internalInsert(holder, true);
        if (simulated == holder.getFluidAmount()) {
            return container.internalInsert(holder, false);
        }
        return 0L;
    }

    public static void fillBottle(FluidContainer tank, Player player, InteractionHand hand) {
        if (tank.getFluids().isEmpty()) return;
        if (tank.getSize() < 1) return;
        FluidHolder holder = tank.getFluids().get(0);
        ItemStack itemStack = new ItemStack(getHoneyBottleFromFluid(holder.getFluid()), 1);

        if (holder.getFluidAmount() < BeeConstants.HONEY_PER_BOTTLE) return;
        if (holder.isEmpty()) return;
        if (itemStack.isEmpty()) return;

        FluidHolder extracted = exactExtract(tank, holder);
        if (extracted.isEmpty()) return;

        bottleAction(itemStack, SoundEvents.BOTTLE_FILL, player, hand);
    }

    public static void emptyBottle(FluidContainer tank, Player player, InteractionHand hand) {
        FluidHolder holder = FluidHolder.of(getHoneyFluidFromBottle(player.getItemInHand(hand)), BeeConstants.HONEY_PER_BOTTLE_BUCKETS, null);
        if (holder.isEmpty()) return;

        long inserted = exactInsert(tank, holder);
        if (inserted == 0) return;

        bottleAction(new ItemStack(Items.GLASS_BOTTLE), SoundEvents.BOTTLE_EMPTY, player, hand);
    }

    private static void bottleAction(ItemStack returnStack, SoundEvent sound, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getCount() > 1) {
            stack.shrink(1);
            player.addItem(returnStack);
        } else {
            player.setItemInHand(hand, returnStack);
        }
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            //TODO fix this
            //return ModFluids.HONEY_STILL.get().getSource();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            String id = honey.getHoneyData().id();
            if (id.isEmpty()) return Fluids.EMPTY;
            HoneyFluidData fluidData = HoneyRegistry.get().getHoneyData(id).getFluidData();
            return fluidData.stillFluid().get();
        }
        return Fluids.EMPTY;
    }

    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        //TODO fix this
//        if (fluid instanceof CustomHoneyFluid honeyFluid) {
//            String id = honeyFluid.getHoneyData().id();
//            if (id.isEmpty()) return null;
//            return HoneyRegistry.get().getHoneyData(id).getBottleData().bottle().get();
//        }
        if (fluid.is(ModFluidTags.HONEY)) {
            return Items.HONEY_BOTTLE;
        }
        return Items.AIR;
    }
}
