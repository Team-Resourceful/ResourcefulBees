package com.teamresourceful.resourcefulbees.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.network.NetworkHooks;


public abstract class AbstractTank extends RenderingBaseEntityBlock {
    protected AbstractTank(Properties properties) {
        super(properties);
    }

    //TODO consider switching this to an Interface with a default method so it can be used on blocks that can't extend this class I.E Centrifuge...
    protected void capabilityOrGuiUse(BlockEntity tileEntity, Player player, Level world, BlockPos pos, InteractionHand hand){
        if (player.getItemInHand(hand).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
        } else if (!player.isShiftKeyDown() && !world.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tileEntity, pos);
        }
    }
}
