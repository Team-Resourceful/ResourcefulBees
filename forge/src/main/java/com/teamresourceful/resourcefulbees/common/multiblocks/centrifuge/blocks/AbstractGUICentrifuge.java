package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import static net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock.ASSEMBLED;

public abstract class AbstractGUICentrifuge extends AbstractCentrifuge {

    protected AbstractGUICentrifuge(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult onUse(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (hand == InteractionHand.MAIN_HAND && state.hasProperty(ASSEMBLED) && Boolean.TRUE.equals(state.getValue(ASSEMBLED))) {
            if (level.getBlockEntity(pos) instanceof AbstractGUICentrifugeEntity entity) {
                if (player.getItemInHand(hand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    entity.getCapability(ForgeCapabilities.FLUID_HANDLER)
                            .ifPresent(handler -> FluidUtil.interactWithFluidHandler(player, hand, level, pos, null));
                } else if (!player.isShiftKeyDown() && !level.isClientSide) {
                    NetworkHooks.openScreen((ServerPlayer) player, entity, entity::getOpenGUIPacket);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.onUse(state, level, pos, player, hand, hitResult);
    }


}
