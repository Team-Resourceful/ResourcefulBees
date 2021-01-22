package com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge.CentrifugeControllerTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CentrifugeControllerBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty PROPERTY_VALID = BooleanProperty.create("valid");

    public CentrifugeControllerBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(PROPERTY_VALID,false).with(FACING, Direction.NORTH));
    }

    protected CentrifugeControllerTileEntity getControllerEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof CentrifugeControllerTileEntity) {
            return (CentrifugeControllerTileEntity) tileEntity;
        }
        return null;
    }

    @Nonnull
    @Override
    public ActionResultType onUse(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(hand);
            boolean usingBucket = heldItem.getItem() instanceof BucketItem;
            CentrifugeControllerTileEntity controller = getControllerEntity(world, pos);

            if (controller != null && controller.isValidStructure()) {
                if (usingBucket) {
                    controller.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                            .ifPresent(iFluidHandler -> FluidUtil.interactWithFluidHandler(player, hand, world, pos, null));
                } else if (!player.isSneaking()) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, controller, pos);
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull Block changedBlock, @Nonnull BlockPos changedBlockPos, boolean bool) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof CentrifugeTileEntity) {
            CentrifugeTileEntity centrifugeTileEntity = (CentrifugeTileEntity) tileEntity;
            centrifugeTileEntity.setIsPoweredByRedstone(world.isBlockPowered(pos));
        }
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        return getControllerEntity(worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new CentrifugeControllerTileEntity(ModTileEntityTypes.CENTRIFUGE_CONTROLLER_ENTITY.get()); }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) { return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(PROPERTY_VALID, FACING); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTip(I18n.format("block.resourcefulbees.centrifuge.tooltip.info"), TextFormatting.GOLD)
                .build());
        if (Screen.hasControlDown()){
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.format("block.resourcefulbees.centrifuge.tooltip.structure_size"), TextFormatting.AQUA)
                    .addTip(I18n.format("block.resourcefulbees.centrifuge.tooltip.requisites"), TextFormatting.AQUA)
                    .addTip(I18n.format("block.resourcefulbees.centrifuge.tooltip.capabilities"), TextFormatting.AQUA)
                    .build());
        } else {
            tooltip.add(new StringTextComponent(TextFormatting.AQUA + I18n.format("resourcefulbees.ctrl_info")));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }


}



