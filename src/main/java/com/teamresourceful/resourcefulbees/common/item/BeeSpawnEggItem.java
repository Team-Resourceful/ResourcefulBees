package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.render.ColorData;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 This class is required for use to disable the use of bees in spawners as a lot of devs use bee spawn eggs in crafting recipes.
 **/
public class BeeSpawnEggItem extends SpawnEggItem {
    private final CustomBeeData beeData;

	public BeeSpawnEggItem(EntityType<? extends Mob> entityType, String beeType) {
		super(entityType, 0xffcc33, 0x303030, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_BEES));
		this.beeData = BeeRegistry.getRegistry().getBeeData(beeType);
	}

	//try to remove this
    public CustomBeeData getBeeData() {
        return beeData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        ColorData colorData = ((BeeSpawnEggItem)stack.getItem()).beeData.getRenderData().colorData();
        return tintIndex == 0 ? colorData.spawnEggPrimaryColor().getValue(): colorData.spawnEggSecondaryColor().getValue();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Player player = context.getPlayer();
        if (player != null) {
            Level world = context.getLevel();
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
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
                if (entityType.spawn((ServerLevel) world, itemstack, context.getPlayer(), blockPos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos1) && direction == Direction.UP) != null) {
                    itemstack.shrink(1);
                }

                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull Optional<Mob> spawnOffspringFromSpawnEgg(@NotNull Player playerEntity, @NotNull Mob mobEntity, @NotNull EntityType<? extends Mob> entityType, @NotNull ServerLevel world, @NotNull Vec3 vector3d, @NotNull ItemStack stack) {
        return Optional.empty();
    }
}