package com.teamresourceful.resourcefulbees.common.items.base;

import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ExpandableTooltip {

    default void setupTooltip(@NotNull ItemStack stack, @Nullable BlockGetter pLevel, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        boolean shiftDown = Minecraft.getInstance().hasShiftDown();
        boolean ctrlDown = !shiftDown && Minecraft.getInstance().hasControlDown();
        String shift = shiftDown ? ItemTranslations.SHIFT_TOOLTIP_HIGHLIGHT : ItemTranslations.SHIFT_TOOLTIP;
        components.add(Component.translatable(shift, getShiftingDisplay()));
        if (getControlDisplay() != null) {
            String ctrl = ctrlDown ? ItemTranslations.CTRL_TOOLTIP_HIGHLIGHT : ItemTranslations.CTRL_TOOLTIP;
            components.add(Component.translatable(ctrl, getControlDisplay()));
        }
        if (shiftDown){
            components.add(Component.empty());
            appendShiftTooltip(stack, pLevel, components, flag);
        } else if (ctrlDown && getControlDisplay() != null) {
            components.add(Component.empty());
            appendControlTooltip(stack, pLevel, components, flag);
        }
    }

    Component getShiftingDisplay();

    void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag);

    default Component getControlDisplay() {
        return null;
    }

    default void appendControlTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {

    }

}
