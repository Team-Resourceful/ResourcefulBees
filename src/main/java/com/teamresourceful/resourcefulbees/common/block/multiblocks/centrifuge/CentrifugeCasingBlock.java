package com.teamresourceful.resourcefulbees.common.block.multiblocks.centrifuge;


import com.teamresourceful.resourcefulbees.common.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.centrifuge.CentrifugeCasingTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CentrifugeCasingTileEntity(ModBlockEntityTypes.CENTRIFUGE_CASING_ENTITY.get());
    }

    protected CentrifugeControllerTileEntity getControllerEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CentrifugeCasingTileEntity) {
            return ((CentrifugeCasingTileEntity) tileEntity).getController();
        }
        return null;
    }

    //TODO this can break if other casings are powered need to find an alternative solution.
    // may have to loop through every casing and check if any are still powered.
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull Block changedBlock, @NotNull BlockPos changedBlockPos, boolean bool) {
        CentrifugeControllerTileEntity centrifugeControllerTileEntity = getControllerEntity(world, pos);
        if (centrifugeControllerTileEntity != null) {
            centrifugeControllerTileEntity.setIsPoweredByRedstone(world.hasNeighborSignal(pos));
        }
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult rayTraceResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasCapability = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        CentrifugeControllerTileEntity controller = getControllerEntity(world, pos);
        if (controller != null && controller.isValidStructure()) {
            if (hasCapability) {
                controller.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                        .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
            } else if(!player.isShiftKeyDown() && !world.isClientSide) {
                NetworkHooks.openGui((ServerPlayerEntity) player, controller, controller.getBlockPos());
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }
}
