package com.teamresourceful.resourcefulbees.common.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.items.base.Tradeable;
import com.teamresourceful.resourcefulbees.common.items.honey.ColoredObject;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.CustomHoneyBlockData;
import com.teamresourceful.resourcefulbees.common.setup.data.honeydata.HoneyDataImpl;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

//this class mirrors the HoneyBlock class due to the particles method at the bottom being private
public class CustomHoneyBlock extends HalfTransparentBlock implements Tradeable, ColoredObject {

    public static final MapCodec<CustomHoneyBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(CustomHoneyBlock::properties),
            CustomHoneyBlockData.CODEC.fieldOf("data").forGetter(CustomHoneyBlock::data)
    ).apply(instance, CustomHoneyBlock::new));

    private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 15.0);

    @Override
    public @NonNull MapCodec<CustomHoneyBlock> codec() {
        return CODEC;
    }

    protected final Color color;

    protected final HoneyBlockData data;
    public CustomHoneyBlock(BlockBehaviour.Properties properties, HoneyBlockData data) {
        super(properties
                .mapColor(MapColor.COLOR_ORANGE)
                .speedFactor(data.speedFactor())
                .jumpFactor(data.jumpFactor())
                .noOcclusion()
                .sound(SoundType.HONEY_BLOCK)
        );
        this.color = data.color();
        this.data = data;
    }

    private HoneyBlockData data() {
        return data;
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
    public int color() {
        return color.getValue() | 0xff000000;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (color.isSpecial()) world.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        super.animateTick(stateIn, world, pos, rand);
    }

    private static boolean doesEntityDoHoneyBlockSlideEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecart || entity instanceof PrimedTnt || entity instanceof AbstractBoat;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            level.broadcastEntityEvent(entity, (byte)54);
        }

        if (entity.causeFallDamage(fallDistance, 0.2F, level.damageSources().fall())) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (this.isSlidingDown(pos, entity)) {
            this.maybeDoSlideAchievement(entity, pos);
            this.doSlideMovement(entity);
            this.maybeDoSlideEffects(level, entity);
        }

        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
    }

    private static double getOldDeltaY(double deltaY) {
        return deltaY / 0.98F + 0.08;
    }

    private static double getNewDeltaY(double deltaY) {
        return (deltaY - 0.08) * 0.98F;
    }

    private boolean isSlidingDown(BlockPos pos, Entity entity) {
        if (entity.onGround()) {
            return false;
        } else if (entity.getY() > pos.getY() + 0.9375 - 1.0E-7) {
            return false;
        } else if (getOldDeltaY(entity.getDeltaMovement().y) >= -0.08) {
            return false;
        } else {
            double dx = Math.abs(pos.getX() + 0.5 - entity.getX());
            double dz = Math.abs(pos.getZ() + 0.5 - entity.getZ());
            double overlapDistance = 0.4375 + entity.getBbWidth() / 2.0F;
            return dx + 1.0E-7 > overlapDistance || dz + 1.0E-7 > overlapDistance;
        }
    }

    private void maybeDoSlideAchievement(Entity entity, BlockPos pos) {
        if (entity instanceof ServerPlayer serverPlayer && entity.level().getGameTime() % 20L == 0L) {
            CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger(serverPlayer, entity.level().getBlockState(pos));
        }
    }

    private void doSlideMovement(Entity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        if (getOldDeltaY(entity.getDeltaMovement().y) < -0.13) {
            double horizontalReductionFactor = -0.05 / getOldDeltaY(entity.getDeltaMovement().y);
            entity.setDeltaMovement(new Vec3(deltaMovement.x * horizontalReductionFactor, getNewDeltaY(-0.05), deltaMovement.z * horizontalReductionFactor));
        } else {
            entity.setDeltaMovement(new Vec3(deltaMovement.x, getNewDeltaY(-0.05), deltaMovement.z));
        }

        entity.resetFallDistance();
    }

    private void maybeDoSlideEffects(Level level, Entity entity) {
        if (doesEntityDoHoneyBlockSlideEffects(entity)) {
            RandomSource random = level.getRandom();
            if (random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (!level.isClientSide() && random.nextInt(5) == 0) {
                level.broadcastEntityEvent(entity, (byte)53);
            }
        }
    }

    public static void showSlideParticles(Entity entity) {
        showParticles(entity, 5);
    }

    public static void showJumpParticles(Entity entity) {
        showParticles(entity, 10);
    }

    private static void showParticles(Entity entity, int count) {
        if (entity.level().isClientSide()) {
            BlockState blockState = entity.getBlockStateOn();

            for (int i = 0; i < count; i++) {
                entity.level()
                        .addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }
}

