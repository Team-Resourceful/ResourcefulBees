
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


@SuppressWarnings({"unused", "deprecation"})
public class CustomHoneyBlock extends BreakableBlock {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected final HoneyBottleData honeyData;

    public CustomHoneyBlock(HoneyBottleData honeyData) {
        super(AbstractBlock.Properties.create(Material.CLAY).velocityMultiplier(0.4F).jumpVelocityMultiplier(0.5F).nonOpaque().sound(SoundType.HONEY));
        this.honeyData = honeyData;
    }

    public int getHoneyColor() {
        return honeyData.getHoneyColorInt();
    }

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex) {
        CustomHoneyBlock honeycombBlock = ((CustomHoneyBlock) state.getBlock());
        return honeycombBlock.honeyData.isRainbow() ? RainbowColor.getRGB() : honeycombBlock.getHoneyColor();
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        BlockItem blockItem = (BlockItem) stack.getItem();
        if (!(blockItem.getBlock() instanceof CustomHoneyBlock)) return -1;
        CustomHoneyBlock honeycombBlock = (CustomHoneyBlock) blockItem.getBlock();
        return honeycombBlock.honeyData.isRainbow() ? RainbowColor.getRGB() : honeycombBlock.getHoneyColor();
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.add(BeeRegistry.getRegistry().getHoneyData(honeyData.getName()).getHoneyBlockItemRegistryObject().get().getDefaultInstance());
        return drops;
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (honeyData.isRainbow())
            world.notifyBlockUpdate(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return BeeRegistry.getRegistry().getHoneyData(honeyData.getName()).getHoneyBlockItemRegistryObject().get().getDefaultInstance();
    }


    /**
     * Data copyied from minecraft:honey_block
     */

    private static boolean hasHoneyBlockEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecartEntity || entity instanceof TNTEntity || entity instanceof BoatEntity;
    }

    @Override
    @Nonnull
    public VoxelShape getCollisionShape(@NotNull BlockState blockState, @NotNull IBlockReader blockReader, @NotNull BlockPos blockPos, @NotNull ISelectionContext selectionContext) {
        return SHAPE;
    }


    /**
     * Block's chance to react to a living entity falling on it.
     */
    @Override
    public void onFallenUpon(World world, @NotNull BlockPos blockPos, Entity entity, float distance) {
        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (world.isRemote) {
            addParticles(entity);
        }

        if (entity.handleFallDamage(distance, 0.2F)) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }

    }

    @Override
    public void onEntityCollision(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (this.isSliding(blockPos, entity)) {
            this.triggerAdvancement(entity, blockPos);
            this.updateSlidingVelocity(entity);
            this.addCollisionEffects(world, entity);
        }
        super.onEntityCollision(state, world, blockPos, entity);
    }

    private boolean isSliding(BlockPos blockPos, Entity entity) {
        return isSliding(blockPos, entity);
    }

    private void triggerAdvancement(Entity entity, BlockPos blockPos) {
        if (entity instanceof ServerPlayerEntity && entity.world.getGameTime() % 20L == 0L) {
            CriteriaTriggers.SLIDE_DOWN_BLOCK.test((ServerPlayerEntity) entity, entity.world.getBlockState(blockPos));
        }
    }

    private void updateSlidingVelocity(Entity entity) {
        CatnipHoneyBlock.updateSlidingVelocity(entity);
    }

    private void addCollisionEffects(World world, Entity entity) {
        if (hasHoneyBlockEffects(entity)) {
            if (world.rand.nextInt(5) == 0) {
                entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (world.isRemote && world.rand.nextInt(5) == 0) {
                addParticles(entity);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void addParticles(Entity entity) {
        BlockState blockstate = honeyData.getHoneyBlockRegistryObject().get().getDefaultState();

        for (int i = 0; i < 5; ++i) {
            entity.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
}

