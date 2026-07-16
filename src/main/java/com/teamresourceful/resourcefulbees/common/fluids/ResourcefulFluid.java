package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefullib.common.fluid.ResourcefulFlowingFluid;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public abstract class ResourcefulFluid extends ResourcefulFlowingFluid {

    private final FluidData info;

    protected ResourcefulFluid(FluidData info, boolean source) {
        super(info);
        this.info = info;
        if (source) {
            info.setStill(() -> this);
        } else {
            info.setFlowing(() -> this);
        }
    }

    public FluidData info() {
        return info;
    }

    @Override
    public @NonNull Fluid getFlowing() {
        return info.flowing().get();
    }

    @Override
    public @NonNull Fluid getSource() {
        return info.still().get();
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return info.properties().canConvertToSource();
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState blockState) {
        final BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(blockState, level, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return info.properties().slopeFindDistance();
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return info.properties().dropOff();
    }

    @Override
    public @NonNull Item getBucket() {
        final Item bucket = info.bucket().get();
        return bucket == null ? Items.AIR : bucket;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return Direction.DOWN == direction && !isSame(fluid);
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return info.properties().tickDelay();
    }

    @Override
    protected float getExplosionResistance() {
        return info.properties().explosionResistance();
    }

    @Override
    protected @NonNull BlockState createLegacyBlock(@NotNull FluidState state) {
        Block block = info.block().get();
        if (block == null) return Blocks.AIR.defaultBlockState();
        return block.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(@NotNull Fluid fluid) {
        return fluid == info.still().get() || fluid == info.flowing().get();
    }

    @Override
    public @NonNull Optional<SoundEvent> getPickupSound() {
        final SoundEvent event = info.properties().sounds().getOrDefault("bucket_fill", null);
        return event == null ? Optional.of(SoundEvents.BUCKET_FILL) : Optional.of(event);
    }

    public static class Source extends ResourcefulFluid {
        public Source(FluidData info) {
            super(info, true);
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return true;
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return 8;
        }
    }

    public static class Flowing extends ResourcefulFluid {
        public Flowing(FluidData info) {
            super(info, false);
            this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(@NotNull StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return false;
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return state.getValue(LEVEL);
        }
    }
}
