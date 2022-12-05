package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 This class is required for use to disable the use of bees in spawners as a lot of devs use bee spawn eggs in crafting recipes.
 **/
public class BeeSpawnEggItem extends ForgeSpawnEggItem {
    private final CustomBeeData beeData;

	public BeeSpawnEggItem(Supplier<EntityType<? extends CustomBeeEntity>> entityType, String beeType) {
		super(entityType, 0xffcc33, 0x303030, new Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_BEES));
		this.beeData = BeeRegistry.get().getBeeData(beeType);
	}

    @Override
    public int getColor(int tintIndex) {
        BeeColorData colorData = beeData.getRenderData().colorData();
        return tintIndex == 0 ? colorData.primarySpawnEggColor().getValue(): colorData.secondarySpawnEggColor().getValue();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (context.getPlayer() != null) {
            if (context.getLevel() instanceof ServerLevel level) {
                BlockPos blockPos = context.getClickedPos();
                Direction direction = context.getClickedFace();

                BlockPos pos = level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty() ? blockPos : blockPos.relative(direction);

                EntityType<?> entityType = this.getType(stack.getTag());
                if (entityType.spawn(level, stack, context.getPlayer(), pos, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, pos) && direction == Direction.UP) != null) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull Optional<Mob> spawnOffspringFromSpawnEgg(@NotNull Player playerEntity, @NotNull Mob mobEntity, @NotNull EntityType<? extends Mob> entityType, @NotNull ServerLevel world, @NotNull Vec3 vector3d, @NotNull ItemStack stack) {
        return Optional.empty();
    }
}