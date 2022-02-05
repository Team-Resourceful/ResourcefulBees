package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IShiftingToolTip {

    @OnlyIn(Dist.CLIENT)
    default void setupTooltip(@NotNull ItemStack stack, @Nullable BlockGetter pLevel, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        boolean shiftDown = Screen.hasShiftDown();
        boolean ctrlDown = !shiftDown && Screen.hasControlDown();
        String shift = shiftDown ? TranslationConstants.Items.SHIFT_TOOLTIP_HIGHLIGHT : TranslationConstants.Items.SHIFT_TOOLTIP;
        components.add(new TranslatableComponent(shift, getShiftingDisplay()));
        if (getControlDisplay() != null) {
            String ctrl = ctrlDown ? TranslationConstants.Items.CTRL_TOOLTIP_HIGHLIGHT : TranslationConstants.Items.CTRL_TOOLTIP;
            components.add(new TranslatableComponent(ctrl, getControlDisplay()));
        }
        if (shiftDown){
            components.add(TextComponent.EMPTY);
            appendShiftTooltip(stack, pLevel, components, flag);
        } else if (ctrlDown) {
            components.add(TextComponent.EMPTY);
            appendControlTooltip(stack, pLevel, components, flag);
        }
    }

    @OnlyIn(Dist.CLIENT)
    Component getShiftingDisplay();

    @OnlyIn(Dist.CLIENT)
    void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag);

    @OnlyIn(Dist.CLIENT)
    default Component getControlDisplay() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    default void appendControlTooltip(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {

    }

}
