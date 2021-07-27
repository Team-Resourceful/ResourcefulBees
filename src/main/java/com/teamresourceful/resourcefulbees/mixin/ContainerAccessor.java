package com.teamresourceful.resourcefulbees.mixin;

import net.minecraft.inventory.container.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.awt.event.ContainerListener;
import java.util.List;

@Mixin(Container.class)
public interface ContainerAccessor {

    @Accessor("containerListeners")
    List<ContainerListener> getListeners();
}
