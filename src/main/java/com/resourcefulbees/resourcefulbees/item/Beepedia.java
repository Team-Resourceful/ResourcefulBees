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

public class Beepedia extends Item {

    TranslationTextComponent containerName = new TranslationTextComponent("gui.resourcefulbees.beepedia");

    public Beepedia(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemstack = playerEntity.getHeldItem(hand);
        if (world.isRemote){
            Minecraft.getInstance().displayGuiScreen(new BeepediaScreen(containerName, null));
        }
        return ActionResult.success(itemstack, world.isRemote());
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.world.isRemote){
            if (entity instanceof CustomBeeEntity) {
                Minecraft.getInstance().displayGuiScreen(new BeepediaScreen(containerName, ((CustomBeeEntity) entity).getBeeType()));
                return ActionResultType.SUCCESS;
            }
        }
        return super.itemInteractionForEntity(stack, player, entity, hand);
    }
}
