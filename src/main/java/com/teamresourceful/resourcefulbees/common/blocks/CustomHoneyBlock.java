package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.common.items.base.Tradeable;
import com.teamresourceful.resourcefulbees.common.items.honey.ColoredObject;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class CustomHoneyBlock extends HalfTransparentBlock implements Tradeable, ColoredObject {

    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected final Color color;
    protected final HoneyBlockData data;

    public CustomHoneyBlock(HoneyBlockData data) {
        super(Properties.of().speedFactor(data.speedFactor()).jumpFactor(data.jumpFactor()).noOcclusion().sound(SoundType.HONEY_BLOCK));
        this.color = data.color();
        this.data = data;
    }

    @Override
    public BeekeeperTradeData getTradeData() {
        return data.tradeData();
    }

    @Override
    public boolean isTradable() {
        return data.tradeData().isTradable();
    }

    //region Color stuff
    @Override
    public int getObjectColor(int index) {
        return color.getValue();
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (color.isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        super.animateTick(stateIn, world, pos, rand);
    }

    //endregion


    //region Item stuff

    @NotNull
    @Override
    public List<ItemStack> getDrops(@NotNull BlockState blockState, @NotNull LootParams.Builder builder) {
        return Collections.singletonList(this.asItem().getDefaultInstance());
    }

    //endregion

    @Override
    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState blockState, @NotNull BlockGetter blockReader, @NotNull BlockPos blockPos, @NotNull CollisionContext selectionContext) {
        return SHAPE;
    }

    //region Sliding Stuff

    @Override
    public void fallOn(Level level, @NotNull BlockState state, @NotNull BlockPos blockPos, Entity entity, float distance) {
        entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (level.isClientSide) {
            addParticles(entity);
        }

        if (entity.causeFallDamage(distance, 0.2F, level.damageSources().fall())) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }

    }

    private boolean isSliding(BlockPos pos, Entity entity) {
        if (entity.onGround()) return false;
        else if (entity.getY() > pos.getY() + 0.9375D - 1.0E-7D) return false;
        else if (entity.getDeltaMovement().y >= -0.08D) return false;

        double d0 = Math.abs(pos.getX() + 0.5D - entity.getX());
        double d1 = Math.abs(pos.getZ() + 0.5D - entity.getZ());
        double d2 = 0.4375D + (entity.getBbWidth() / 2.0F);
        return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (isSliding(blockPos, entity)) {
            this.triggerAdvancement(entity, blockPos);
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }
        super.entityInside(state, world, blockPos, entity);
    }

    private void triggerAdvancement(Entity entity, BlockPos blockPos) {
        if (entity instanceof ServerPlayer player && entity.level().getGameTime() % 20L == 0L) {
            CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger(player, entity.level().getBlockState(blockPos));
        }
    }

    private void updateSlidingVelocity(Entity entity) {
        Vec3 vector3d = entity.getDeltaMovement();
        if (vector3d.y < -0.13D) {
            double d0 = -0.05D / vector3d.y;
            entity.setDeltaMovement(new Vec3(vector3d.x * d0, -0.05D, vector3d.z * d0));
        } else {
            entity.setDeltaMovement(new Vec3(vector3d.x, -0.05D, vector3d.z));
        }

        entity.fallDistance = 0.0F;
    }

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof Minecart || entity instanceof PrimedTnt || entity instanceof Boat;
    }

    private void addCollisionEffects(Level world, Entity entity) {
        if (world.random.nextInt(5) == 0 && hasHoneyBlockEffects(entity)) {
            entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            if (world.isClientSide) addParticles(entity);
        }
    }

    //endregion

    @Environment(EnvType.CLIENT)
    private void addParticles(Entity entity) {
        BlockParticleOption particleData = new BlockParticleOption(ParticleTypes.BLOCK, this.defaultBlockState());
        for (int i = 0; i < 5; ++i) {
            entity.level().addParticle(particleData, entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
}

