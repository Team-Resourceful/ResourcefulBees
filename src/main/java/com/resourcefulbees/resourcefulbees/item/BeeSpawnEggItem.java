package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
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
    public String getTranslationKey(ItemStack stack) {
        CompoundNBT nbt = stack.getChildTag(NBTConstants.NBT_ROOT);
        String name;
        if ((nbt != null && nbt.contains(NBTConstants.NBT_BEE_TYPE))) {
            name = String.format("item.%1$s.%2$s_spawn_egg", ResourcefulBees.MOD_ID, nbt.getString(NBTConstants.NBT_BEE_TYPE));
        } else {
            name = String.format("item.%1$s.bee_spawn_egg", ResourcefulBees.MOD_ID);
        }
        return name;
    }

    @Nonnull
	@Override
	public EntityType<?> getType(@Nullable final CompoundNBT p_208076_1_) {
		return entityType.get();
	}

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemstack = context.getItem();
        PlayerEntity player = context.getPlayer();
        CompoundNBT tag = itemstack.getChildTag(NBTConstants.NBT_ROOT);
        if (tag != null && player != null) {
            String bee = tag.getString(NBTConstants.NBT_BEE_TYPE);
            if (BeeInfo.getInfo(bee) == null && !itemstack.isEmpty()) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    if (player.inventory.getStackInSlot(i) == itemstack) {
                        player.inventory.removeStackFromSlot(i);
                    }
                }
                return ActionResultType.FAIL;
            } else {
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

                    EntityType<?> entitytype = this.getType(itemstack.getTag());
                    if (entitytype.spawn((ServerWorld) world, itemstack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                        itemstack.shrink(1);
                    }

                    return ActionResultType.CONSUME;
                }
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