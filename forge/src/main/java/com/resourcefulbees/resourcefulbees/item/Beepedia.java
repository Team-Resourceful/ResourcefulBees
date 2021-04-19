package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeepediaUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class Beepedia extends Item {

    public static final String COMPLETE_TAG = "Complete";
    public static final String CREATIVE_TAG = "Creative";

    public Beepedia(Properties properties) {
        super(properties);
    }

    public void checkAndAddBees(ItemStack stack, CustomBeeEntity entity) {
        CompoundTag nbt = stack.hasTag() && stack.getTag() != null ? stack.getTag() : new CompoundTag();
        ListTag listNBT;
        if (nbt.getBoolean(CREATIVE_TAG)) return;
        if (nbt.contains(NBTConstants.NBT_BEES)) {
            listNBT = nbt.getList(NBTConstants.NBT_BEES, 8).copy();
        } else {
            listNBT = new ListTag();
        }
        if (entity != null) {
            if (!listNBT.contains(StringTag.valueOf(entity.getBeeType()))) {
                listNBT.add(StringTag.valueOf(entity.getBeeType()));
            }
            listNBT.removeIf(b -> BeeRegistry.getRegistry().getBeeData(b.getAsString()) == null);
            listNBT = listNBT.stream().distinct().collect(Collectors.toCollection(ListTag::new));
            nbt.putBoolean(COMPLETE_TAG, listNBT.size() == BeeRegistry.getRegistry().getBees().size());
            nbt.put(NBTConstants.NBT_BEES, listNBT);
            stack.setTag(nbt);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        checkAndAddBees(itemstack, null);
        if (world.isClientSide) {
            BeepediaUtils.loadBeepedia(itemstack, null);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (entity instanceof CustomBeeEntity) {
            checkAndAddBees(stack, (CustomBeeEntity) entity);
            if (player.level.isClientSide) BeepediaUtils.loadBeepedia(stack, entity);
            player.setItemInHand(hand, stack);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            if (stack.getTag().getBoolean(CREATIVE_TAG)) return new TranslatableComponent("item.resourcefulbees.creative_beepedia").withStyle(ChatFormatting.LIGHT_PURPLE);
            if (stack.getTag().getBoolean(COMPLETE_TAG)) return new TextComponent("✦ ").withStyle(ChatFormatting.GREEN).append(super.getName(stack).copy().withStyle(ChatFormatting.WHITE));
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("item.resourcefulbees.information.beepedia").withStyle(ChatFormatting.GREEN));
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            boolean complete = stack.getTag().getBoolean(COMPLETE_TAG) || stack.getTag().getBoolean(CREATIVE_TAG);
            int total = BeeRegistry.getRegistry().getBees().size();
            int count = stack.getTag().getList(NBTConstants.NBT_BEES, 8).size();
            tooltip.add(new TranslatableComponent("gui.resourcefulbees.beepedia.home.progress").withStyle(ChatFormatting.GRAY)
                    .append(String.format("%d / %d", complete? total : count, total)).withStyle(ChatFormatting.GOLD));
        }

    }
}
