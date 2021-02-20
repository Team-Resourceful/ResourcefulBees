package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public class BeepediaContainer extends Container{

    private ItemStack itemStack;

    public BeepediaContainer(int id, PlayerInventory inventory) {
        super(ModContainers.BEEPEDIA_CONTAINER.get(), id);
        itemStack = inventory.getCurrentItem();
    }

    @Override
    public boolean canInteractWith(PlayerEntity p_75145_1_) {
        return true;
    }
}
