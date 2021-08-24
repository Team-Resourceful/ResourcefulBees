package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.render.ColorData;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class BeeSpawnEggItem extends SpawnEggItem {
    private final CustomBeeData beeData;

	public BeeSpawnEggItem(EntityType<?> entityType, int firstColor, int secondColor, String beeType, Properties properties) {
		super(entityType, firstColor, secondColor, properties);
		this.beeData = BeeRegistry.getRegistry().getBeeData(beeType);
	}

	//try to remove this
    public CustomBeeData getBeeData() {
        return beeData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        ColorData colorData = ((BeeSpawnEggItem)stack.getItem()).beeData.getRenderData().getColorData();
        return tintIndex == 0 ? colorData.getSpawnEggPrimaryColor().getValue(): colorData.getSpawnEggSecondaryColor().getValue();
    }

    @Override
    public @NotNull ActionResultType useOn(ItemUseContext context) {
        ItemStack itemstack = context.getItemInHand();
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World world = context.getLevel();
            if (world.isClientSide) {
                return ActionResultType.SUCCESS;
            } else {
                BlockPos blockPos = context.getClickedPos();
                Direction direction = context.getClickedFace();
                BlockState blockstate = world.getBlockState(blockPos);

                BlockPos blockPos1;
                if (blockstate.getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos1 = blockPos;
                } else {
                    blockPos1 = blockPos.relative(direction);
                }

                EntityType<?> entityType = this.getType(itemstack.getTag());
                if (entityType.spawn((ServerWorld) world, itemstack, context.getPlayer(), blockPos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos1) && direction == Direction.UP) != null) {
                    itemstack.shrink(1);
                }

                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public @NotNull Optional<MobEntity> spawnOffspringFromSpawnEgg(@NotNull PlayerEntity playerEntity, @NotNull MobEntity mobEntity, @NotNull EntityType<? extends MobEntity> entityType, @NotNull ServerWorld world, @NotNull Vector3d vector3d, @NotNull ItemStack stack) {
        return Optional.empty();
    }
}