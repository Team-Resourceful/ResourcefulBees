package com.resourcefulbees.resourcefulbees.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import org.jetbrains.annotations.Nullable;

public class BeepediaContainer extends Container {

    protected BeepediaContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Override
    public boolean canInteractWith(PlayerEntity p_75145_1_) {
        return true;
    }
}
