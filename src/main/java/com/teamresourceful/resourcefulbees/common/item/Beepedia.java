package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.common.capabilities.Capabilities;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Beepedia extends Item {

    public static final String COMPLETE_TAG = "Complete";
    public static final String CREATIVE_TAG = "Creative";

    public Beepedia(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        LazyOptional<IBeepediaData> data = player.getCapability(Capabilities.BEEPEDIA_DATA);
        ItemStack itemstack = player.getItemInHand(hand);
        if (world.isClientSide) {
            BeepediaUtils.loadBeepedia(itemstack, data);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        LazyOptional<IBeepediaData> data = player.getCapability(Capabilities.BEEPEDIA_DATA);
        if (entity instanceof CustomBeeEntity) {
            if (!player.level.isClientSide) {
                sendPacket(entity, player);
            }
            if (player.level.isClientSide) BeepediaUtils.loadBeepedia(stack, data);
            player.setItemInHand(hand, stack);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    private void sendPacket(LivingEntity entity, Player player) {

    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            if (stack.getTag().getBoolean(CREATIVE_TAG)) return TranslationConstants.Items.CREATIVE_BEEPEDIA.withStyle(ChatFormatting.LIGHT_PURPLE);
            if (stack.getTag().getBoolean(COMPLETE_TAG)) return Component.literal("✦ ").withStyle(ChatFormatting.GREEN).append(super.getName(stack).copy().withStyle(ChatFormatting.WHITE));
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(TranslationConstants.Items.INFO_BEEPEDIA.withStyle(ChatFormatting.GREEN));
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            boolean complete = stack.getTag().getBoolean(COMPLETE_TAG) || stack.getTag().getBoolean(CREATIVE_TAG);
            int total = BeeRegistry.getRegistry().getBees().size();
            int count = stack.getTag().getList(NBTConstants.NBT_BEES, 8).size();
            tooltip.add(Component.translatable(TranslationConstants.Beepedia.PROGRESS, complete? total : count, total).withStyle(ChatFormatting.GRAY));
        }

    }

    public static boolean isCreative(ItemStack stack) {
        return stack.getItem() instanceof Beepedia && !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().getBoolean(CREATIVE_TAG);
    }
}
