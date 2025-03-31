package com.teamresourceful.resourcefulbees.common.menus.base;

import com.teamresourceful.resourcefulbees.common.util.containers.AutomationSensitiveContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AutomationSensitiveSlot extends Slot {

    private final AutomationSensitiveContainer container;

    public AutomationSensitiveSlot(AutomationSensitiveContainer container, int i, int j, int k) {
        super(container, i, j, k);
        this.container = container;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.container.canAccept(this.getContainerSlot(), stack, false);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return this.container.canRemove(this.getContainerSlot(), false);
    }

    @Override
    public int getMaxStackSize() {
        return this.container.getMaxStackSize(this.getContainerSlot());
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        this.container.setItem(this.getContainerSlot(), stack, false);
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return this.container.removeItem(this.getContainerSlot(), amount, false);
    }
}
