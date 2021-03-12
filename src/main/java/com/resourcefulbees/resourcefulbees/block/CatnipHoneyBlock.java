package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class CatnipHoneyBlock extends HoneyBlock {

    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public CatnipHoneyBlock() {
        super(AbstractBlock.Properties.of(Material.CLAY).speedFactor(0.4F).jumpFactor(0.5F).noOcclusion().sound(SoundType.HONEY_BLOCK));
    }

    /**
     * Data copied from minecraft:honey_block
     */

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TNTEntity || entity instanceof BoatEntity;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState blockState, @NotNull IBlockReader blockReader, @NotNull BlockPos blockPos, @NotNull ISelectionContext selectionContext) {
        return SHAPE;
    }

    /**
     * Block's chance to react to a living entity falling on it.
     */
    @Override
    public void fallOn(World world, @NotNull BlockPos blockPos, Entity entity, float distance) {
        entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (world.isClientSide) {
            addParticles(entity);
        }

        if (entity.causeFallDamage(distance, 0.2F)) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }

    }

    /**
     * @deprecated whatever
     */
    @Override
    @Deprecated
    public void entityInside(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (isSliding(blockPos, entity)) {
            triggeradvancement(entity, blockPos);
            updateSlidingVelocity(entity);
            addcollisioneffects(world, entity);
        }
        super.entityInside(state, world, blockPos, entity);
    }

    public static boolean isSliding(BlockPos blockPos, Entity entity) {
        if (entity.isOnGround()) {
            return false;
        } else if (entity.getY() > (double) blockPos.getY() + 0.9375D - 1.0E-7D) {
            return false;
        } else if (entity.getDeltaMovement().y >= -0.08D) {
            return false;
        } else {
            double d0 = Math.abs((double) blockPos.getX() + 0.5D - entity.getX());
            double d1 = Math.abs((double) blockPos.getZ() + 0.5D - entity.getZ());
            double d2 = 0.4375D + (double) (entity.getBbWidth() / 2.0F);
            return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
        }
    }

    private static void triggeradvancement(Entity entity, BlockPos blockPos) {
        if (entity instanceof ServerPlayerEntity && entity.level.getGameTime() % 20L == 0L) {
            CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayerEntity) entity, entity.level.getBlockState(blockPos));
        }
    }

    public static void updateSlidingVelocity(Entity entity) {
        Vector3d vector3d = entity.getDeltaMovement();
        if (vector3d.y < -0.13D) {
            double d0 = -0.05D / vector3d.y;
            entity.setDeltaMovement(new Vector3d(vector3d.x * d0, -0.05D, vector3d.z * d0));
        } else {
            entity.setDeltaMovement(new Vector3d(vector3d.x, -0.05D, vector3d.z));
        }

        entity.fallDistance = 0.0F;
    }

    private static void addcollisioneffects(World world, Entity entity) {
        if (hasHoneyBlockEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (world.isClientSide && world.random.nextInt(5) == 0) {
                addParticles(entity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void addParticles(Entity entity) {
        BlockState blockstate = ModBlocks.CATNIP_HONEY_BLOCK.get().defaultBlockState();

        for (int i = 0; i < 5; ++i) {
            entity.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
}
