package com.dungeonderps.resourcefulbees.block;

import com.dungeonderps.resourcefulbees.tileentity.MechanicalCentrifugeTileEntity;
import com.dungeonderps.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class MechanicalCentrifugeBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");
    public static final IntegerProperty PROPERTY_ROTATION = IntegerProperty.create("rotations", 0, 7);

    public MechanicalCentrifugeBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(PROPERTY_ON, false).with(PROPERTY_ROTATION, 0));
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote) {
            INamedContainerProvider blockEntity = state.getContainer(world, pos);
            MechanicalCentrifugeTileEntity tile = (MechanicalCentrifugeTileEntity)world.getTileEntity(pos);
            if (player.isCrouching() && !(player instanceof FakePlayer)){
                if (tile !=null && tile.canProcess(tile.getRecipe())) {
                    tile.clicks++;
                    world.setBlockState(pos, state.with(PROPERTY_ROTATION, state.get(PROPERTY_ROTATION) == 7 ? 0 : state.get(PROPERTY_ROTATION) + 1), 3);
                }
            }
            else if (blockEntity != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
        return (INamedContainerProvider) worldIn.getTileEntity(pos);
    }

    @Override
    public void onReplaced(@Nonnull BlockState state1, World world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isMoving) {
        TileEntity blockEntity = world.getTileEntity(pos);
        if (blockEntity instanceof MechanicalCentrifugeTileEntity && state.getBlock() != state1.getBlock()) {
            MechanicalCentrifugeTileEntity centrifugeTileEntity = (MechanicalCentrifugeTileEntity) blockEntity;
            ItemStackHandler h = centrifugeTileEntity.h;
            IntStream.range(0, h.getSlots()).mapToObj(h::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onReplaced(state1, world, pos, state, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MechanicalCentrifugeTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ON, PROPERTY_ROTATION, FACING);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.format("block.resourcefulbees.mech_centrifuge.tooltip.info"), TextFormatting.GOLD)
                    .build());
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
