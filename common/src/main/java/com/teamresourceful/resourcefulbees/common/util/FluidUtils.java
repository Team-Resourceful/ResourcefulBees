package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.items.honey.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class FluidUtils {

    public static FluidHolder exactExtract(FluidContainer container, FluidHolder holder) {
        FluidHolder simulated = container.internalExtract(holder, true);
        if (simulated.getFluidAmount() == holder.getFluidAmount()) {
            return container.internalExtract(holder, false);
        }
        return FluidHolder.empty();
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

        if (holder.getFluidAmount() < FluidConstants.getBottleAmount()) return;
        if (holder.isEmpty()) return;
        if (itemStack.isEmpty()) return;

        FluidHolder extracted = exactExtract(tank, holder.copyWithAmount(FluidConstants.getBottleAmount()));
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
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.addItem(returnStack);
        } else {
            player.setItemInHand(hand, returnStack);
        }
        player.level().playSound(null, player.blockPosition(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            String id = honey.getHoneyData().id();
            if (id.isEmpty()) return Fluids.EMPTY;
            HoneyFluidData fluidData = HoneyRegistry.get().getHoneyData(id).getFluidData();
            return fluidData.stillFluid().get();
        }
        return Fluids.EMPTY;
    }

    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid honeyFluid) {
            String id = honeyFluid.getHoneyData().id();
            if (id.isEmpty()) return Items.AIR;
            return HoneyRegistry.get().getHoneyData(id).getBottleData().bottle().get();
        } else if (fluid.is(ModFluidTags.HONEY)) {
            return Items.HONEY_BOTTLE;
        }
        return Items.AIR;
    }

    public static void checkBottleAndCapability(FluidContainer tank, BlockEntity entity, Player player, Level level, BlockPos pos, InteractionHand hand) {
        Item item = player.getItemInHand(hand).getItem();
        if (item instanceof BottleItem) {
            fillBottle(tank, player, hand);
        } else if (item instanceof HoneyBottleItem) {
            emptyBottle(tank, player, hand);
        } else if (!player.isShiftKeyDown() && !level.isClientSide() && player instanceof ServerPlayer serverPlayer && entity instanceof ContentMenuProvider<?> provider) {
            provider.openMenu(serverPlayer);
        }
    }

    public static void writeToBuffer(FluidHolder holder, FriendlyByteBuf buffer) {
        if (holder.isEmpty()) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeVarInt(BuiltInRegistries.FLUID.getId(holder.getFluid()));
            buffer.writeVarLong(holder.getFluidAmount());
            buffer.writeNbt(holder.getCompound());
        }
    }

    public static FluidHolder readFromBuffer(FriendlyByteBuf buffer) {
        if (!buffer.readBoolean()) return FluidHolder.empty();
        Fluid fluid = BuiltInRegistries.FLUID.byId(buffer.readVarInt());
        long amount = buffer.readVarLong();
        return FluidHolder.of(fluid, amount, buffer.readNbt());
    }
}
