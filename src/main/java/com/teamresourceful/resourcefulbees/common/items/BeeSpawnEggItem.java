package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.items.honey.ColoredObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class BeeSpawnEggItem extends SpawnEggItem implements ColoredObject {

    protected final Supplier<EntityType<? extends CustomBeeEntity>> defaultType;
    protected final CustomBeeData data;

    public BeeSpawnEggItem(Supplier<EntityType<? extends CustomBeeEntity>> entityType, String beeType) {
        super(null, 0xFFCC33, 0x303030, new Properties());
        this.defaultType = entityType;
        this.data = BeeRegistry.get().getBeeData(beeType);
    }

    @Override
    public int getColor(int tintIndex) {
        BeeColorData colorData = data.getRenderData().colorData();
        return tintIndex == 0 ? colorData.primarySpawnEggColor().getValue(): colorData.secondarySpawnEggColor().getValue();
    }

    @Override
    public int getObjectColor(int index) {
        return getColor(index);
    }

    @Override
    @SuppressWarnings("ConstantValue")
    public @NotNull EntityType<?> getType(@Nullable CompoundTag compoundTag) {
        EntityType<?> entityType = super.getType(compoundTag);
        return entityType == null ? this.getDefaultType() : entityType;
    }

    public EntityType<? extends Mob> getDefaultType() {
        return this.defaultType.get();
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
                boolean shouldOffsetYMore = !Objects.equals(blockPos, pos) && direction == Direction.UP;
                CustomBeeEntity entity = (CustomBeeEntity) entityType.spawn(level, stack, context.getPlayer(), pos, MobSpawnType.SPAWN_EGG, true, shouldOffsetYMore);

                if (entity != null) {
                    stack.shrink(1);
                    entity.setPersistenceRequired();
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

    @Override
    public @NotNull FeatureFlagSet requiredFeatures() {
        return getDefaultType().requiredFeatures();
    }

    public void registerDispenserBehavior() {
        DispenserBlock.registerBehavior(this, (source, stack) -> {
            Direction face = source.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> type = ((SpawnEggItem)stack.getItem()).getType(stack.getTag());

            try {
                type.spawn(source.getLevel(), stack, null, source.getPos().relative(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
                stack.shrink(1);
                source.getLevel().gameEvent(GameEvent.ENTITY_PLACE, source.getPos(), GameEvent.Context.of(source.getBlockState()));
                return stack;
            } catch (Exception exception) {
                DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), exception);
                return ItemStack.EMPTY;
            }
        });
    }
}
