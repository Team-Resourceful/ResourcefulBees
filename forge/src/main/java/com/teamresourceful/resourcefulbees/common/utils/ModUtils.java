package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public final class ModUtils {

    private ModUtils() {
        throw new UtilityClassError();
    }

    public static void capabilityOrGuiUse(BlockEntity tileEntity, Player player, Level level, BlockPos pos, InteractionHand hand){
        if (player.getItemInHand(hand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
            tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER)
                    .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, level, pos, null));
        } else if (!player.isShiftKeyDown() && !level.isClientSide()) {
            NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) tileEntity, pos);
        }
    }

    public static ItemStack insertItem(ItemStackHandler handler, int slot, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack existing = handler.getStackInSlot(slot);
        int limit = Math.min(64, stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;

        boolean reachedLimit = stack.getCount() > limit;
        //TODO Change to copyWithCount in 1.19.3
        if (existing.isEmpty()) handler.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
        else existing.grow(reachedLimit ? limit : stack.getCount());

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    public static boolean IS_DATAGEN = false;
}
