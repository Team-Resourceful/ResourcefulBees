package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.container.BeepediaContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
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
        playerEntity.openContainer(new SimpleNamedContainerProvider((id, inventory, name) -> new BeepediaContainer(id, inventory), containerName));
        return ActionResult.success(itemstack, world.isRemote());
    }
}
