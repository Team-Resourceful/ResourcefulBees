package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeeEntityAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeeJar extends Item {
    public BeeJar(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getColor(ItemStack stack, int tintIndex) {
        CompoundTag tag = stack.getTag();
        if (tintIndex == 1 && tag != null) {
            String color = tag.getString(NBTConstants.NBT_COLOR);
            return color.isBlank() || color.equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR) ? BeeConstants.VANILLA_BEE_INT_COLOR : Color.parse(tag.getString(NBTConstants.NBT_COLOR)).getValue();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_ENTITY);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            ItemStack stack = context.getItemInHand();
            Level worldIn = context.getLevel();
            if (worldIn.isClientSide() || !isFilled(stack)) return InteractionResult.FAIL;
            Entity entity = getEntityFromStack(stack, worldIn, true);

            if (entity != null) {
                String name = stack.getTag().getCompound(NBTConstants.NBT_DISPLAY).getString(NBTConstants.NBT_NAME);
                if (!name.isBlank()) entity.setCustomName(Component.Serializer.fromJson(name));
                if (entity instanceof Bee bee) updateCapturedBee(bee, player);
                BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace());
                entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                worldIn.addFreshEntity(entity);
            }
            if (player.isCreative()) return InteractionResult.SUCCESS;
            if (stack.getCount() > 1) {
                if (!player.addItem(new ItemStack(ModItems.BEE_JAR.get()))) {
                    player.drop(new ItemStack(ModItems.BEE_JAR.get()), false);
                }
                stack.shrink(1);
            } else {
                stack.setTag(null);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public static void updateCapturedBee(Bee bee, Player player) {
        bee.setSavedFlowerPos(null);
        ((BeeEntityAccessor) bee).setHivePos(null);
        if (bee.isAngry()) {
            bee.setTarget(player);
            if (bee instanceof ResourcefulBee customBee) {
                TraitData traitData = customBee.getTraitData();
                if (traitData.getDamageTypes().stream().anyMatch(damageType -> damageType.getType().equals(TraitConstants.EXPLOSIVE))) {
                    customBee.setExplosiveCooldown(60);
                }
            }
        }
    }

    @Nullable
    public static Entity getEntityFromStack(ItemStack stack, Level world, boolean withInfo) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            EntityType<?> type = EntityType.byString(tag.getString(NBTConstants.NBT_ENTITY)).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (entity != null && withInfo) entity.load(tag);
                return entity;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, LivingEntity targetIn, @NotNull InteractionHand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof Bee target) || !targetIn.isAlive()) || (isFilled(stack))) {
            return InteractionResult.FAIL;
        }

        CompoundTag tag = BeeInfoUtils.createJarBeeTag(target, NBTConstants.NBT_ENTITY);

        if (stack.getCount() > 1) {
            ItemStack newJar = new ItemStack(ModItems.BEE_JAR.get());
            newJar.setTag(tag);
            stack.shrink(1);
            if (!player.addItem(newJar)) {
                player.drop(newJar, false);
            }
        } else {
            stack.setTag(tag);
        }

        player.setItemInHand(hand, stack);
        player.swing(hand);
        target.discard();
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        MutableComponent component = super.getName(stack).copy();
        if (BeeJar.isFilled(stack)) {
            ResourceLocation id = new ResourceLocation(stack.getTag().getString(NBTConstants.NBT_ENTITY));
            component.append(new TextComponent(" - ")).append(new TranslatableComponent("entity." + id.getNamespace() + "." + id.getPath()));
        }
        return component;
    }

    @NotNull
    @Override
    public String getDescriptionId(@NotNull ItemStack stack) {
        return isFilled(stack) ? TranslationConstants.Items.BEE_JAR_FILLED : TranslationConstants.Items.BEE_JAR_EMPTY;
    }
}