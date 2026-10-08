package com.teamresourceful.resourcefulbees.client.component;

import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee.BeepediaTickable;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class CyclingItemStackDisplayWidget extends ItemStackDisplayWidget implements BeepediaTickable {

    public static final int CYCLE_TICKS = 20;

    private final List<ItemStack> stacks;

    private int index;
    private int ticks;

    public CyclingItemStackDisplayWidget(ItemStack stack) {
        this(List.of(stack));
    }

    public CyclingItemStackDisplayWidget(List<ItemStack> stacks) {
        super(ItemStack.EMPTY);
        this.stacks = List.copyOf(stacks);
    }

    @Override
    public void tick() {
        if (stacks.size() <= 1) {
            return;
        }

        if (++ticks >= CYCLE_TICKS) {
            ticks = 0;
            index = (index + 1) % stacks.size();
        }
    }

    @Override
    public ItemStack getStack() {
        if (stacks.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return stacks.get(index);
    }
}