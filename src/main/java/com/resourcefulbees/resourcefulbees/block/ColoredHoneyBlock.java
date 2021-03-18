package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"unused", "deprecation"})
public class ColoredHoneyBlock extends BreakableBlock {

    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected final int color;
    protected final boolean isRainbow;

    public ColoredHoneyBlock(int color, boolean isRainbow) {
        super(AbstractBlock.Properties.of(Material.CLAY).speedFactor(0.4F).jumpFactor(0.5F).noOcclusion().sound(SoundType.HONEY_BLOCK));
        this.color = color;
        this.isRainbow = isRainbow;
    }


    //region Color stuff
    public int getHoneyColor() {
        return color;
    }

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex) {
        ColoredHoneyBlock honeycombBlock = ((ColoredHoneyBlock) state.getBlock());
        return honeycombBlock.isRainbow ? RainbowColor.getRGB() : honeycombBlock.getHoneyColor();
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        BlockItem blockItem = (BlockItem) stack.getItem();
        if (!(blockItem.getBlock() instanceof ColoredHoneyBlock)) return -1;
        ColoredHoneyBlock honeycombBlock = (ColoredHoneyBlock) blockItem.getBlock();
        return honeycombBlock.isRainbow ? RainbowColor.getRGB() : honeycombBlock.getHoneyColor();
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull World world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (isRainbow) world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }

    //endregion


    //region Item stuff
    @NotNull
    @Override
    public List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootContext.Builder builder) {
        return Collections.singletonList(this.asItem().getDefaultInstance());
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return this.asItem().getDefaultInstance();
    }

    //endregion

    @Override
    @NotNull
    public VoxelShape getCollisionShape(@NotNull BlockState blockState, @NotNull IBlockReader blockReader, @NotNull BlockPos blockPos, @NotNull ISelectionContext selectionContext) {
        return SHAPE;
    }

    //region Sliding Stuff

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

    private boolean isSliding(BlockPos pos, Entity entity){
        if (entity.isOnGround()) {
            return false;
        } else if (entity.getY() > (double) pos.getY() + 0.9375D - 1.0E-7D) {
            return false;
        } else if (entity.getDeltaMovement().y >= -0.08D) {
            return false;
        } else {
            double d0 = Math.abs((double) pos.getX() + 0.5D - entity.getX());
            double d1 = Math.abs((double) pos.getZ() + 0.5D - entity.getZ());
            double d2 = 0.4375D + (double) (entity.getBbWidth() / 2.0F);
            return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
        }
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (isSliding(blockPos, entity)) {
            this.triggerAdvancement(entity, blockPos);
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }
        super.entityInside(state, world, blockPos, entity);
    }

    private void triggerAdvancement(Entity entity, BlockPos blockPos) {
        if (entity instanceof ServerPlayerEntity && entity.level.getGameTime() % 20L == 0L) {
            CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayerEntity) entity, entity.level.getBlockState(blockPos));
        }
    }

    private void updateSlidingVelocity(Entity entity) {
        Vector3d vector3d = entity.getDeltaMovement();
        if (vector3d.y < -0.13D) {
            double d0 = -0.05D / vector3d.y;
            entity.setDeltaMovement(new Vector3d(vector3d.x * d0, -0.05D, vector3d.z * d0));
        } else {
            entity.setDeltaMovement(new Vector3d(vector3d.x, -0.05D, vector3d.z));
        }

        entity.fallDistance = 0.0F;
    }

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TNTEntity || entity instanceof BoatEntity;
    }

    private void addCollisionEffects(World world, Entity entity) {
        if (hasHoneyBlockEffects(entity)) {
            if (world.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
                if (world.isClientSide) addParticles(entity);
            }
        }
    }

    //endregion

    @OnlyIn(Dist.CLIENT)
    private void addParticles(Entity entity) {
        BlockParticleData particleData = new BlockParticleData(ParticleTypes.BLOCK, this.getBlock().defaultBlockState());
        for (int i = 0; i < 5; ++i) {
            entity.level.addParticle(particleData, entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
}

