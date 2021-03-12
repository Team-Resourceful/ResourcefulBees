package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.color.Color;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeeJar extends Item {
    public BeeJar(Item.Properties properties) {
        super(properties);
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        CompoundNBT tag = stack.getTag();
        if (tintIndex == 1 && tag != null && tag.contains(NBTConstants.NBT_COLOR) && !tag.getString(NBTConstants.NBT_COLOR).equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR)) {
            return tag.getString(NBTConstants.NBT_COLOR).equals(BeeConstants.RAINBOW_COLOR) ? RainbowColor.getRGB() : Color.parseInt(tag.getString(NBTConstants.NBT_COLOR));
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_ENTITY);
    }

    @Nonnull
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World playerWorld = context.getPlayer().getCommandSenderWorld();
            ItemStack stack = context.getItemInHand();
            if (playerWorld.isClientSide() || !isFilled(stack)) return ActionResultType.FAIL;
            World worldIn = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Entity entity = getEntityFromStack(stack, worldIn, true);
            if (entity != null) {
                if (entity instanceof BeeEntity) {
                    resetBee((BeeEntity) entity);
                }
                BlockPos blockPos = pos.relative(context.getClickedFace());
                entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                worldIn.addFreshEntity(entity);
            }
            stack.setTag(null);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    private void resetBee(BeeEntity beeEntity) {
        beeEntity.savedFlowerPos = null;
        beeEntity.hivePos = null;
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            EntityType<?> type = EntityType.byString(tag.getString(NBTConstants.NBT_ENTITY)).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (entity != null && withInfo) entity.load(stack.getTag());
                return entity;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public ActionResultType interactLivingEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, LivingEntity targetIn, @Nonnull Hand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(stack))) {
            return ActionResultType.FAIL;
        }

        BeeEntity target = (BeeEntity) targetIn;

        if (stack.getCount() > 1) {
            ItemStack newJar = new ItemStack(ModItems.BEE_JAR.get());
            newJar.setTag(BeeInfoUtils.createJarBeeTag(target, NBTConstants.NBT_ENTITY));
            stack.shrink(1);
            renameJar(newJar, target);
            if (!player.addItem(newJar)) {
                player.drop(newJar, false);
            }
        } else {
            stack.setTag(BeeInfoUtils.createJarBeeTag(target, NBTConstants.NBT_ENTITY));
            renameJar(stack, target);
        }
        player.setItemInHand(hand, stack);
        player.swing(hand);
        target.remove(true);
        return ActionResultType.PASS;
    }

    public static void renameJar(ItemStack stack, BeeEntity target) {
        CompoundNBT nbt = stack.getOrCreateTag();
        ITextComponent beeName = target.getName();
        TranslationTextComponent bottleName = new TranslationTextComponent(stack.getItem().getDescriptionId(stack));
        bottleName.append(" - ").append(beeName);
        bottleName.setStyle(Style.EMPTY.withItalic(false));
        CompoundNBT displayNBT = new CompoundNBT();
        displayNBT.putString("Name", ITextComponent.Serializer.toJson(bottleName));
        nbt.put("display", displayNBT);
    }

    @OnlyIn(Dist.CLIENT)
    public static void fillJar(ItemStack stack, CustomBeeData beeData) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID());
        World world = Minecraft.getInstance().level;
        if (world == null || entityType == null) return;
        Entity entity = entityType.create(world);
        if (entity instanceof BeeEntity) {
            stack.setTag(BeeInfoUtils.createJarBeeTag((BeeEntity) entity, NBTConstants.NBT_ENTITY));
            renameJar(stack, (BeeEntity) entity);
        }
    }

    @Nonnull
    @Override
    public String getDescriptionId(@Nonnull ItemStack stack) {
        String name;
        if (isFilled(stack)) {
            name = "item." + ResourcefulBees.MOD_ID + ".bee_jar_filled";
        } else
            name = "item." + ResourcefulBees.MOD_ID + ".bee_jar_empty";
        return name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        CompoundNBT tag = stack.getTag();
        if (tag != null && isFilled(stack)) {
            String type = tag.getString(NBTConstants.NBT_ENTITY);
            if (tag.contains(NBTConstants.NBT_BEE_TYPE)) {
                String rbType = tag.getString(NBTConstants.NBT_BEE_TYPE);
                tooltip.add(new StringTextComponent(I18n.get(ResourcefulBees.MOD_ID + ".information.bee_type.custom")
                        + (I18n.exists("entity.resourcefulbees." + rbType) ? I18n.get("entity.resourcefulbees."
                        + rbType) : WordUtils.capitalize(rbType.replace("_", " ")))).withStyle(TextFormatting.WHITE));
            } else if (type.equals(BeeConstants.VANILLA_BEE_ID)) {
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.vanilla").withStyle(TextFormatting.WHITE));
            } else {
                tooltip.add(new TranslationTextComponent(ResourcefulBees.MOD_ID + ".information.bee_type.unknown"));
            }
        }
    }
}