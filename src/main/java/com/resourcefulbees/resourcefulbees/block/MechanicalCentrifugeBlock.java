package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.tileentity.MechanicalCentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.block.AbstractBlock.Properties;

@SuppressWarnings("deprecation")
public class MechanicalCentrifugeBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");
    public static final IntegerProperty PROPERTY_ROTATION = IntegerProperty.create("rotations", 0, 7);

    public MechanicalCentrifugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PROPERTY_ON, false).setValue(PROPERTY_ROTATION, 0));
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult blockRayTraceResult) {
        if (!world.isClientSide) {
            INamedContainerProvider blockEntity = state.getMenuProvider(world, pos);
            MechanicalCentrifugeTileEntity tile = (MechanicalCentrifugeTileEntity)world.getBlockEntity(pos);
            if (player.isShiftKeyDown() && !(player instanceof FakePlayer)) {
                if (tile != null && tile.canProcess(tile.getRecipe())) {
                    player.causeFoodExhaustion(Config.PLAYER_EXHAUSTION.get().floatValue());
                    tile.setClicks(tile.getClicks() + 1);
                    if (state.getValue(PROPERTY_ROTATION) == 7)
                        world.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundCategory.BLOCKS, 0.5F, 0.1F);
                    world.playSound(null, pos, SoundEvents.FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 0.5F, 0.1F);
                    world.setBlock(pos, state.setValue(PROPERTY_ROTATION, state.getValue(PROPERTY_ROTATION) == 7 ? 0 : state.getValue(PROPERTY_ROTATION) + 1), 3);
                }
            } else if (blockEntity != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, blockEntity, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos) {
        return (INamedContainerProvider) worldIn.getBlockEntity(pos);
    }

    @Override
    public void onRemove(@Nonnull BlockState state1, World world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isMoving) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MechanicalCentrifugeTileEntity && state.getBlock() != state1.getBlock()) {
            MechanicalCentrifugeTileEntity centrifugeTileEntity = (MechanicalCentrifugeTileEntity) blockEntity;
            ItemStackHandler h = centrifugeTileEntity.getItemStackHandler();
            IntStream.range(0, h.getSlots()).mapToObj(h::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onRemove(state1, world, pos, state, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MechanicalCentrifugeTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ON, PROPERTY_ROTATION, FACING);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.get("block.resourcefulbees.mech_centrifuge.tooltip.info"), TextFormatting.GOLD)
                    .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
