package com.teamresourceful.resourcefulbees.item;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.render.RenderData;
import com.teamresourceful.resourcefulbees.api.beedata.traits.TraitData;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.lib.constants.TraitConstants;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import com.teamresourceful.resourcefulbees.utils.color.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BeeJar extends Item {
    public BeeJar(Item.Properties properties) {
        super(properties);
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        CompoundTag tag = stack.getTag();
        if (tintIndex == 1 && tag != null) {
            if (tag.contains(NBTConstants.NBT_COLOR) && !tag.getString(NBTConstants.NBT_COLOR).equals(BeeConstants.STRING_DEFAULT_ITEM_COLOR)) {
                return Color.parse(tag.getString(NBTConstants.NBT_COLOR)).getValue();
            } else if (!tag.contains(NBTConstants.NBT_COLOR) && tag.contains(NBTConstants.NBT_ENTITY) && Minecraft.getInstance().level != null) {
                // one time check for a bee's color, if customBeeEntity, set the jar's color to the bee's color, else set it to default.
                // this code should only ever run once per beejar if the beejar does not have a color.
                String id = tag.getString(NBTConstants.NBT_ENTITY);
                EntityType<?> entityType = BeeInfoUtils.getEntityType(id);
                if (entityType != null) {
                    Entity entity = entityType.create(Minecraft.getInstance().level);
                    if (entity instanceof CustomBeeEntity) {
                        RenderData renderData = ((CustomBeeEntity) entity).getRenderData();
                        tag.putString(NBTConstants.NBT_COLOR, renderData.getColorData().getJarColor().toString());
                        return renderData.getColorData().getJarColor().getValue();
                    }
                }
                tag.putString(NBTConstants.NBT_COLOR, BeeConstants.STRING_DEFAULT_ITEM_COLOR);
                stack.setTag(tag);
            }
            return BeeConstants.VANILLA_BEE_INT_COLOR;
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_ENTITY);
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            Level playerWorld = context.getPlayer().getCommandSenderWorld();
            ItemStack stack = context.getItemInHand();
            if (playerWorld.isClientSide() || !isFilled(stack)) return InteractionResult.FAIL;
            Level worldIn = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Entity entity = getEntityFromStack(stack, worldIn, true);
            if (entity != null) {
                if (entity instanceof Bee) {
                    Bee beeEntity = (Bee) entity;
                    resetBee(beeEntity);
                    setBeeAngry(beeEntity, player);
                }
                BlockPos blockPos = pos.relative(context.getClickedFace());
                entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                worldIn.addFreshEntity(entity);
            }
            stack.setTag(null);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }


    public static void setBeeAngry(Bee beeEntity, Player player) {
        if (beeEntity.isAngry()) {
            beeEntity.setTarget(player);
            if (beeEntity instanceof ResourcefulBee) {
                ResourcefulBee customBee = (ResourcefulBee) beeEntity;
                TraitData traitData = customBee.getTraitData();
                if (traitData.getDamageTypes().stream().anyMatch(damageType -> damageType.getType().equals(TraitConstants.EXPLOSIVE))) {
                    customBee.setExplosiveCooldown(60);
                }
            }
        }
    }

    public static void resetBee(Bee beeEntity) {
        beeEntity.savedFlowerPos = null;
        beeEntity.hivePos = null;
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
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof Bee) || !targetIn.isAlive()) || (isFilled(stack))) {
            return InteractionResult.FAIL;
        }

        Bee target = (Bee) targetIn;

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
        return InteractionResult.PASS;
    }

    public static void renameJar(ItemStack stack, Entity target, String name) {
        if (stack.getTag() == null || stack.getTag().contains(NBTConstants.NBT_DISPLAY)) return;
        CompoundTag nbt = stack.getOrCreateTag();
        Component beeName = target.getName();
        TranslatableComponent bottleName = new TranslatableComponent(name);
        bottleName.append(" - ").append(beeName);
        bottleName.setStyle(Style.EMPTY.withItalic(false));
        CompoundTag displayNBT = new CompoundTag();
        displayNBT.putString("Name", Component.Serializer.toJson(bottleName));
        nbt.put(NBTConstants.NBT_DISPLAY, displayNBT);
    }

    public static void renameJar(ItemStack stack, Bee target) {
        renameJar(stack, target, stack.getItem().getDescriptionId(stack));
    }

    @OnlyIn(Dist.CLIENT)
    public static void fillJar(ItemStack stack, CustomBeeData beeData) {
        EntityType<?> entityType = beeData.getEntityType();
        Level world = Minecraft.getInstance().level;
        if (world == null) return;
        Entity entity = entityType.create(world);
        if (entity != null) {
            stack.setTag(BeeInfoUtils.createJarBeeTag((Bee) entity, NBTConstants.NBT_ENTITY));
            renameJar(stack, (Bee) entity);
        }
    }

    private void renameJar(ItemStack stack, CompoundTag tag, String name) {
        if (stack.getTag() == null || stack.getTag().contains(NBTConstants.NBT_DISPLAY)) return;
        if (Minecraft.getInstance().level == null) return;
        String id = tag.getString(NBTConstants.NBT_ENTITY);
        EntityType<?> entityType = BeeInfoUtils.getEntityType(id);
        if (entityType != null) {
            Entity entity = entityType.create(Minecraft.getInstance().level);
            if (entity != null) {
                renameJar(stack, entity, name);
            }
        }
    }

    @NotNull
    @Override
    public String getDescriptionId(@NotNull ItemStack stack) {
        String name;
        if (isFilled(stack)) {
            name = "item." + ResourcefulBees.MOD_ID + ".bee_jar_filled";
            renameJar(stack, stack.getTag(), name);
        } else
            name = "item." + ResourcefulBees.MOD_ID + ".bee_jar_empty";
        return name;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        CompoundTag tag = stack.getTag();
        if (tag != null && isFilled(stack)) {
            String id = tag.getString(NBTConstants.NBT_ENTITY);
            EntityType<?> entityType = BeeInfoUtils.getEntityType(id);
            Component name = entityType == null ? new TextComponent("NULL_ENTITY") : entityType.getDescription();
            tooltip.add(new TextComponent("  - ").append(name).withStyle(ChatFormatting.WHITE));
        }
    }
}