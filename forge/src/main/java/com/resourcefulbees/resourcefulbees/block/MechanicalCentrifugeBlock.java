package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.tileentity.MechanicalCentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public class MechanicalCentrifugeBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty PROPERTY_ON = BooleanProperty.create("on");
    public static final IntegerProperty PROPERTY_ROTATION = IntegerProperty.create("rotations", 0, 7);

    public MechanicalCentrifugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PROPERTY_ON, false).setValue(PROPERTY_ROTATION, 0));
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult rayTraceResult) {
        MechanicalCentrifugeTileEntity tile = (MechanicalCentrifugeTileEntity) world.getBlockEntity(pos);
        if (player.isShiftKeyDown() && !(player instanceof FakePlayer)) {
            if (!world.isClientSide && tile != null && tile.canProcess(tile.getRecipe())) {
                player.causeFoodExhaustion(Config.PLAYER_EXHAUSTION.get().floatValue());
                tile.setClicks(tile.getClicks() + 1);
                if (state.getValue(PROPERTY_ROTATION) == 7)
                    world.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.BLOCKS, 0.5F, 0.1F);
                world.playSound(null, pos, SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 0.5F, 0.1F);
                world.setBlock(pos, state.cycle(PROPERTY_ROTATION), 3);
            }
        } else if (!player.isShiftKeyDown() && !world.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player, tile, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos) {
        return (MenuProvider) worldIn.getBlockEntity(pos);
    }

    @Override
    public void onRemove(@NotNull BlockState state1, Level world, @NotNull BlockPos pos, @NotNull BlockState state, boolean isMoving) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MechanicalCentrifugeTileEntity && state.getBlock() != state1.getBlock()) {
            MechanicalCentrifugeTileEntity centrifugeTileEntity = (MechanicalCentrifugeTileEntity) blockEntity;
            ItemStackHandler h = centrifugeTileEntity.getItemStackHandler();
            IntStream.range(0, h.getSlots()).mapToObj(h::getStackInSlot).filter(s -> !s.isEmpty()).forEach(stack -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onRemove(state1, world, pos, state, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new MechanicalCentrifugeTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_ON, PROPERTY_ROTATION, FACING);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
            tooltip.addAll(new TooltipBuilder()
                    .addTip(I18n.get("block.resourcefulbees.mech_centrifuge.tooltip.info"), ChatFormatting.GOLD)
                    .build());
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
