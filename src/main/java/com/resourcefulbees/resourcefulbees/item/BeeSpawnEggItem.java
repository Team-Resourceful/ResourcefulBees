package com.resourcefulbees.resourcefulbees.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class BeeSpawnEggItem extends SpawnEggItem {

	private final Lazy<? extends EntityType<?>> entityType;

	public BeeSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, final int firstColor, final int SecondColor, final Properties properties) {
		super(null, firstColor, SecondColor, properties);
		this.entityType = Lazy.of(entityTypeSupplier);
	}

    @Nonnull
	@Override
	public EntityType<?> getType(@Nullable final CompoundNBT nbt) {
		return entityType.get();
	}

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemstack = context.getItem();
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World world = context.getWorld();
            if (world.isRemote) {
                return ActionResultType.SUCCESS;
            } else {
                BlockPos blockpos = context.getPos();
                Direction direction = context.getFace();
                BlockState blockstate = world.getBlockState(blockpos);

                BlockPos blockpos1;
                if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                    blockpos1 = blockpos;
                } else {
                    blockpos1 = blockpos.offset(direction);
                }

                EntityType<?> entitytype = this.getType(itemstack.getTag()); //TODO check this against JEI Ingredient Helper cheat stack method
                if (entitytype.spawn((ServerWorld) world, itemstack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                    itemstack.shrink(1);
                }

                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nonnull
    @Override
    public Optional<MobEntity> spawnBaby(@Nonnull PlayerEntity playerEntity, @Nonnull MobEntity mobEntity, @Nonnull EntityType<? extends MobEntity> entityType, @Nonnull ServerWorld world, @Nonnull Vector3d vector3d, @Nonnull ItemStack stack) {
        return Optional.empty();
    }
}