package com.teamresourceful.resourcefulbees.common.compat.jei.ingredients;

import com.teamresourceful.resourcefulbees.common.inventory.slots.FilterSlot;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.SetFilterSlotPacket;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CentrifugeInputGhostIngredientHandler<S extends AbstractContainerScreen<?>> implements IGhostIngredientHandler<S> {

    @Override
    public <I> @NotNull List<Target<I>> getTargetsTyped(@NotNull S gui, @NotNull ITypedIngredient<I> ingredient, boolean doStart) {
        var targets = new ArrayList<Target<I>>();
        if (ingredient.getIngredient() instanceof ItemStack ghostStack) {
            gui.getMenu().slots.stream()
                .filter(FilterSlot.class::isInstance)
                .filter(slot -> slot.mayPlace(ghostStack))
                .forEach(slot -> targets.add(new ItemStackTarget<>(gui, slot)));
        }
        return targets;
    }

    @Override
    public void onComplete() {
        //idk
    }

    private record ItemStackTarget<I, S extends AbstractContainerScreen<?>>(@NotNull S gui, Slot slot) implements Target<I> {

        @Override
            public @NotNull Rect2i getArea() {
                return new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
            }

            @Override
            public void accept(@NotNull I ingredient) {
                NetworkHandler.CHANNEL.sendToServer(new SetFilterSlotPacket((ItemStack) ingredient, slot.index));
            }
        }
}
