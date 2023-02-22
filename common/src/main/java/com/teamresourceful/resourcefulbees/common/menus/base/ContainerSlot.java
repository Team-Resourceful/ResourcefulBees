package com.teamresourceful.resourcefulbees.common.menus.base;

import com.teamresourceful.resourcefulbees.common.blockentities.base.BasicContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerSlot extends Slot {

    private final BasicContainer basicContainer;

    public ContainerSlot(BasicContainer container, int i, int j, int k) {
        super(container, i, j, k);
        this.basicContainer = container;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.basicContainer.canPlaceItem(this.index, stack);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return this.basicContainer.canTakeItem(this.index, player);
    }
}
