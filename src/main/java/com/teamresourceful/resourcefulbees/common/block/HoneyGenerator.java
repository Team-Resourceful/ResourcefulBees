package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.common.tileentity.HoneyGeneratorTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public class HoneyGenerator extends AbstractTank {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");

    public HoneyGenerator(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PROPERTY_ON, false).setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public ActionResultType use(@NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult rayTraceResult) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof HoneyGeneratorTileEntity) {
            capabilityOrGuiUse(tileEntity, player, world, pos, hand);
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public INamedContainerProvider getMenuProvider(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos) {
        return (INamedContainerProvider) worldIn.getBlockEntity(pos);
    }

    @Override
    public void onRemove(@NotNull BlockState state1, World world, @NotNull BlockPos pos, @NotNull BlockState state, boolean isMoving) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HoneyGeneratorTileEntity && state.getBlock() != state1.getBlock()) {
            HoneyGeneratorTileEntity honeyGeneratorTileEntity = (HoneyGeneratorTileEntity) blockEntity;
            ItemStackHandler h = honeyGeneratorTileEntity.getTileStackHandler();
            IntStream.range(0, h.getSlots()).mapToObj(h::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onRemove(state1, world, pos, state, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HoneyGeneratorTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ON, FACING);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable IBlockReader worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.addAll(new TooltipBuilder()
                .addTranslatableTip("block.resourcefulbees.generator.tooltip.info", TextFormatting.GOLD)
                .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
