package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.server.SyncBeepediaPacket;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaSavedData;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Beepedia extends Item {

    public Beepedia(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            BeepediaUtils.loadBeepedia(itemstack, player);
        } else {
            NetPacketHandler.CHANNEL.sendToPlayer(SyncBeepediaPacket.of(player), player);
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (entity instanceof CustomBeeEntity customBee) {
            if (player.level.isClientSide()) {
                return InteractionResult.PASS;
            } else {
                BeepediaSavedData.addBee(player, customBee.getBeeData().name());
            }
            player.setItemInHand(hand, stack);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            if (stack.getTag().getBoolean(NBTConstants.Beepedia.CREATIVE)) return TranslationConstants.Items.CREATIVE_BEEPEDIA.withStyle(ChatFormatting.LIGHT_PURPLE);
            if (stack.getTag().getBoolean(NBTConstants.Beepedia.COMPLETE)) return Component.literal("✦ ").withStyle(ChatFormatting.GREEN).append(super.getName(stack).copy().withStyle(ChatFormatting.WHITE));
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(TranslationConstants.Items.INFO_BEEPEDIA.withStyle(ChatFormatting.GREEN));
        if (stack.hasTag() && stack.getTag() != null && !stack.getTag().isEmpty()) {
            boolean complete = stack.getTag().getBoolean(NBTConstants.Beepedia.COMPLETE) || stack.getTag().getBoolean(NBTConstants.Beepedia.CREATIVE);
            int total = BeeRegistry.get().getBees().size();
            int count = stack.getTag().getList(NBTConstants.NBT_BEES, 8).size();
            tooltip.add(Component.translatable(TranslationConstants.Beepedia.PROGRESS, complete? total : count, total).withStyle(ChatFormatting.GRAY));
        }

    }

    public static boolean isCreative(ItemStack stack) {
        return stack.getItem() instanceof Beepedia && !stack.isEmpty() && stack.hasTag() && stack.getOrCreateTag().getBoolean(NBTConstants.Beepedia.CREATIVE);
    }
}
