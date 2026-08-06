package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BeeJarItem extends Item {
    public BeeJarItem(Properties properties) {
        super(properties.component(ModDataComponents.JAR_BEE, JarOccupant.EMPTY));
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.has(ModDataComponents.JAR_BEE) && !occupantFrom(stack).equals(JarOccupant.EMPTY);
    }

    public static boolean hasEntityData(ItemStack stack) {
        return isFilled(stack) && occupantFrom(stack).entityData().isPresent();
    }

    public static JarOccupant occupantFrom(ItemStack stack) {
        return stack.get(ModDataComponents.JAR_BEE);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack itemStack, @NonNull ServerLevel level, @NonNull Entity owner, @Nullable EquipmentSlot slot) {
        if (isFilled(itemStack)) {
            itemStack.set(ModDataComponents.JAR_BEE, occupantFrom(itemStack).withTickOffSet());
        }
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack stack = context.getItemInHand();
            Level level = context.getLevel();
            if (level.isClientSide() || !isFilled(stack)) return InteractionResult.FAIL;
            return spawnOccupant(context, stack, level, player);
        }
        return InteractionResult.FAIL;
    }

    private static InteractionResult.Success spawnOccupant(UseOnContext context, ItemStack stack, Level level, Player player) {
        var pos = context.getClickedPos();
        var relPos = pos.relative(context.getClickedFace());
        var entity = getEntity(stack, level, relPos);
        if (entity != null) {
            if (entity instanceof Mob mob) mob.setPersistenceRequired();
            EntityUtils.setEntityLocationAndAngle(relPos, context.getClickedFace(), entity);
            level.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.addFreshEntity(entity);
        }

        if (!player.isCreative()) {
            stack.shrink(1);
            if (!player.addItem(new ItemStack(ModItems.BEE_JAR.get()))) {
                player.drop(new ItemStack(ModItems.BEE_JAR.get()), false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static @Nullable Entity getEntity(ItemStack stack, Level level, BlockPos relPos) {
        return BeeJarItem.hasEntityData(stack)
                ? occupantFrom(stack).createEntity(level, relPos)
                : occupantFrom(stack).createEntity(level, EntitySpawnReason.SPAWN_ITEM_USE);
    }


    @Override
    public @NonNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, LivingEntity entity, @NotNull InteractionHand hand) {
        if (entity.level().isClientSide() || !(entity instanceof Bee target) || !entity.isAlive() || isFilled(stack)) {
            return InteractionResult.FAIL;
        }

        createFilledJar(stack, player, target);
        player.setItemInHand(hand, stack);
        player.swing(hand);
        entity.level().playSound(null, target, SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
        target.discard();
        return InteractionResult.SUCCESS;
    }

    private static void createFilledJar(@NonNull ItemStack stack, @NonNull Player player, Bee target) {
        if (stack.getCount() > 1) {
            ItemStack newJar = ModItems.BEE_JAR.get().getDefaultInstance();
            newJar.set(ModDataComponents.JAR_BEE.get(), JarOccupant.from(target));
            stack.shrink(1);
            if (!player.addItem(newJar)) {
                player.drop(newJar, false);
            }
        } else {
            var jarOccupant = JarOccupant.from(target);
            stack.set(ModDataComponents.JAR_BEE.get(), jarOccupant);
        }
    }

    public static ItemStack createFilledJar(EntityType<?> id, int color) {
        ItemStack newJar = ModItems.BEE_JAR.get().getDefaultInstance();
        newJar.set(ModDataComponents.JAR_BEE, JarOccupant.from(id, color));
        return newJar;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        MutableComponent name = isFilled(stack) ? ItemTranslations.BEE_JAR_FILLED.copy() : ItemTranslations.BEE_JAR_EMPTY.copy();
        JarOccupant occupant = stack.getOrDefault(ModDataComponents.JAR_BEE, JarOccupant.EMPTY);
        if (occupant == JarOccupant.EMPTY) return name;

        MutableComponent display = occupant.displayName().copy().withStyle(Style.EMPTY.withColor(occupant.color()));

        return name.append(Component.translatable(ItemTranslations.BEE_BOX_ENTITY_NAME, display));
    }
}