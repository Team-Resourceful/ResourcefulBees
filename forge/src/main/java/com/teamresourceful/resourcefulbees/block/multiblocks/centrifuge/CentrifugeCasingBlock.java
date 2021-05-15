package com.teamresourceful.resourcefulbees.block.multiblocks.centrifuge;


import com.teamresourceful.resourcefulbees.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeCasingTileEntity;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CentrifugeCasingBlock extends Block {
    public CentrifugeCasingBlock(Properties properties) { super(properties); }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new CentrifugeCasingTileEntity(ModBlockEntityTypes.CENTRIFUGE_CASING_ENTITY.get());
    }

    protected CentrifugeControllerTileEntity getControllerEntity(Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CentrifugeCasingTileEntity) {
            return ((CentrifugeCasingTileEntity) tileEntity).getController();
        }
        return null;
    }

    //TODO this can break if other casings are powered need to find an alternative solution.
    // may have to loop through every casing and check if any are still powered.
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Block changedBlock, @NotNull BlockPos changedBlockPos, boolean bool) {
        CentrifugeControllerTileEntity centrifugeControllerTileEntity = getControllerEntity(world, pos);
        if (centrifugeControllerTileEntity != null) {
            centrifugeControllerTileEntity.setIsPoweredByRedstone(world.hasNeighborSignal(pos));
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        CentrifugeControllerTileEntity controller = getControllerEntity(world, pos);
        if (controller != null && controller.isValidStructure()) {
            if (hasCapability) {
                controller.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                        .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
            } else if(!player.isShiftKeyDown() && !world.isClientSide) {
                NetworkHooks.openGui((ServerPlayer) player, controller, controller.getBlockPos());
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }
}
