package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BeeSpawnEggItem extends SpawnEggItem {

    protected static final List<BeeSpawnEggItem> eggsToAdd = new ArrayList<>();
	private final Lazy<? extends EntityType<?>> entityType;
	private final CustomBeeData beeData;

	public BeeSpawnEggItem(RegistryObject<? extends EntityType<?>> entityTypeSupplier, int firstColor, int secondColor, String beeType, Properties properties) {
		super(null, firstColor, secondColor, properties);
		this.beeData = BeeRegistry.getRegistry().getBeeData(beeType);
		this.entityType = Lazy.of(entityTypeSupplier);
		eggsToAdd.add(this);
	}

	@Override
	public @NotNull EntityType<?> getType(@Nullable final CompoundTag nbt) {
		return entityType.get();
	}

	//try to remove this
    public CustomBeeData getBeeData() {
        return beeData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        ColorData colorData = ((BeeSpawnEggItem)stack.getItem()).beeData.getRenderData().getColorData();
        return tintIndex == 0 ? colorData.getSpawnEggPrimaryColor().getValue(): colorData.getSpawnEggSecondaryColor().getValue();
    }

    public static void initSpawnEggs() {
        for (final SpawnEggItem spawnEgg : eggsToAdd) {
            BY_ID.put(spawnEgg.getType(null), spawnEgg);
        }
        eggsToAdd.clear();
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
                BlockPos blockpos = context.getClickedPos();
                Direction direction = context.getClickedFace();
                BlockState blockstate = world.getBlockState(blockpos);

                BlockPos blockpos1;
                if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                    blockpos1 = blockpos;
                } else {
                    blockpos1 = blockpos.relative(direction);
                }

                EntityType<?> entitytype = this.getType(itemstack.getTag());
                if (entitytype.spawn((ServerLevel) world, itemstack, context.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
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