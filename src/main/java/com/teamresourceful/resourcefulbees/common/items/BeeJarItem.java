package com.teamresourceful.resourcefulbees.common.items;

import com.teamresourceful.resourcefulbees.common.components.JarOccupant;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
        super(properties.component(ModDataComponents.JAR_BEE.get(), JarOccupant.EMPTY));
    }

    public static boolean isFilled(ItemStack stack) {
        return stack.has(ModDataComponents.JAR_BEE.get()) && !occupantFrom(stack).equals(JarOccupant.EMPTY);
    }

    public static JarOccupant occupantFrom(ItemStack stack) {
        return stack.get(ModDataComponents.JAR_BEE.get());
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

            var pos = context.getClickedPos();
            var relPos = pos.relative(context.getClickedFace());
            var entity = occupantFrom(stack).createEntity(level, relPos);
            if (entity != null) {
                EntityUtils.setEntityLocationAndAngle(relPos, context.getClickedFace(), entity);
                level.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.addFreshEntity(entity);
            }
            stack.set(ModDataComponents.JAR_BEE.get(), JarOccupant.EMPTY);
            if (!player.isCreative() && stack.getCount() > 1) {
                if (!player.addItem(new ItemStack(ModItems.BEE_JAR.get()))) {
                    player.drop(new ItemStack(ModItems.BEE_JAR.get()), false);
                }
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }


    @Override
    public @NonNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, LivingEntity entity, @NotNull InteractionHand hand) {
        if (entity.level().isClientSide() || !(entity instanceof Bee target) || !entity.isAlive() || isFilled(stack)) {
            return InteractionResult.FAIL;
        }

        create(stack, player, target);
        player.setItemInHand(hand, stack);
        player.swing(hand);
        entity.level().playSound(null, target, SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
        target.discard();
        return InteractionResult.SUCCESS;
    }

    private static void create(@NonNull ItemStack stack, @NonNull Player player, Bee target) {
        if (stack.getCount() > 1) {
            ItemStack newJar = ModItems.BEE_JAR.get().getDefaultInstance();
            newJar.set(ModDataComponents.JAR_BEE.get(), JarOccupant.of(target));
            stack.shrink(1);
            if (!player.addItem(newJar)) {
                player.drop(newJar, false);
            }
        } else {
            var jarOccupant = JarOccupant.of(target);
            stack.set(ModDataComponents.JAR_BEE.get(), jarOccupant);
        }
    }

//    @Override
//    public @NotNull Component getName(@NotNull ItemStack stack) {
//        MutableComponent component = super.getName(stack).copy();
//        if (BeeJarItem.hasEntityDisplay(stack)) {
//            MutableComponent display = Component.Serializer.fromJson(stack.getOrCreateTag().getString(NBTConstants.BeeJar.DISPLAY_NAME));
//            if (display != null) {
//                Color color = getColor(stack);
//                display = color != null ? display.withStyle(Style.EMPTY.withColor(color.getValue())) : display.withStyle(ChatFormatting.GRAY);
//                component.append(Component.translatable(ItemTranslations.BEE_BOX_ENTITY_NAME, display));
//            }
//        }
//        return component;
//    }
//
//    @NotNull
//    @Override
//    public String getDescriptionId(@NotNull ItemStack stack) {
//        return isFilled(stack) ? ItemTranslations.BEE_JAR_FILLED : ItemTranslations.BEE_JAR_EMPTY;
//    }
}