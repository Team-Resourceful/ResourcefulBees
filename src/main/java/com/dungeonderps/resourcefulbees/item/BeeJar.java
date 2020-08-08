package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.ItemGroupResourcefulBees;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeeJar extends Item {
    public BeeJar() {
        super(new Properties().group(ItemGroupResourcefulBees.RESOURCEFUL_BEES).maxStackSize(16));

    }

    public static int getColor(ItemStack stack, int tintIndex) {
        CompoundNBT tag = stack.getTag();
        if (tintIndex == 1 && tag != null && tag.contains(BeeConstants.NBT_COLOR) && !tag.getString(BeeConstants.NBT_COLOR).equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR)) {
            return Color.parseInt(tag.getString(BeeConstants.NBT_COLOR));
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(BeeConstants.NBT_ENTITY);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World playerWorld = context.getPlayer().getEntityWorld();
            ItemStack stack = context.getItem();
            if (playerWorld.isRemote() || !isFilled(stack)) return ActionResultType.FAIL;
            World worldIn = context.getWorld();
            BlockPos pos = context.getPos();
            Entity entity = getEntityFromStack(stack, worldIn, true);
            if (entity != null) {
                BlockPos blockPos = pos.offset(context.getFace());
                entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                worldIn.addEntity(entity);
                stack.setTag(null);//     setTag(new CompoundNBT());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            EntityType<?> type = EntityType.byKey(tag.getString(BeeConstants.NBT_ENTITY)).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (entity != null && withInfo) entity.read(stack.getTag());
                return entity;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public ActionResultType itemInteractionForEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, LivingEntity targetIn, @Nonnull Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(stack))) {
            return ActionResultType.FAIL;
        }

        BeeEntity target = (BeeEntity) targetIn;
        String type = EntityType.getKey(target.getType()).toString();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(BeeConstants.NBT_ENTITY, type);
        target.writeWithoutTypeId(nbt);
        if (target instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) target;
            if (beeEntity.getBeeInfo().getPrimaryColor() != null && !beeEntity.getBeeInfo().getPrimaryColor().isEmpty()) {
                nbt.putString(BeeConstants.NBT_COLOR, beeEntity.getBeeInfo().getPrimaryColor());
            } else {
                nbt.putString(BeeConstants.NBT_COLOR, String.valueOf(BeeConstants.DEFAULT_ITEM_COLOR));
            }
        }
        if (stack.getCount() > 1) {
            ItemStack newJar = new ItemStack(RegistryHandler.BEE_JAR.get());
            newJar.setTag(nbt);
            stack.shrink(1);
             if (!player.addItemStackToInventory(newJar)) {
                 player.dropItem(newJar, false);
             }
        } else {
            stack.setTag(nbt);
        }
        player.setHeldItem(hand, stack);
        player.swingArm(hand);
        target.remove(true);
        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        String name;
        if (isFilled(stack)) {
            name = I18n.format("item." + ResourcefulBees.MOD_ID + '.' + "bee_jar_filled");
        } else
            name = I18n.format("item." + ResourcefulBees.MOD_ID + '.' + "bee_jar_empty");
        return name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        CompoundNBT tag = stack.getTag();
        if (tag != null && isFilled(stack)) {
            if (tag.getString(BeeConstants.NBT_ENTITY).equals("resourcefulbees:bee")) {
                String type = stack.getTag().getString(BeeConstants.NBT_BEE_TYPE);
                tooltip.add(new StringTextComponent(I18n.format(ResourcefulBees.MOD_ID + ".information.bee_type.custom") + StringUtils.capitalize(type)).mergeStyle(TextFormatting.WHITE));
            } else if (stack.getTag().getString(BeeConstants.NBT_ENTITY).equals("minecraft:bee")) {
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.vanilla").mergeStyle(TextFormatting.WHITE));
            } else
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.unknown"));
        }
    }
}
