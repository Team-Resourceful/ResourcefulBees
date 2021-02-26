package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class Beepedia extends Item {

    TranslationTextComponent containerName = new TranslationTextComponent("gui.resourcefulbees.beepedia");

    public Beepedia(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, @NotNull Hand hand) {
        ItemStack itemstack = playerEntity.getHeldItem(hand);
        if (world.isRemote){
            Minecraft.getInstance().displayGuiScreen(new BeepediaScreen(containerName, null));
        }
        return ActionResult.success(itemstack, world.isRemote());
    }

    @Override
    public @NotNull ActionResultType itemInteractionForEntity(@NotNull ItemStack stack, PlayerEntity player, @NotNull LivingEntity entity, @NotNull Hand hand) {
        if (player.world.isRemote && entity instanceof CustomBeeEntity) {
            Minecraft.getInstance().displayGuiScreen(new BeepediaScreen(containerName, ((CustomBeeEntity) entity).getBeeType()));
            return ActionResultType.SUCCESS;
        }
        return super.itemInteractionForEntity(stack, player, entity, hand);
    }
}
