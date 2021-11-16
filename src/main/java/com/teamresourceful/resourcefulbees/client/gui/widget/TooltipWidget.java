package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.tooltip.AbstractTooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TooltipWidget extends Widget {

    private List<AbstractTooltip> tooltips = new LinkedList<>();

    public TooltipWidget(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
    }

    public void setTooltips(List<AbstractTooltip> tooltips) {
        this.tooltips = tooltips;
    }

    public List<AbstractTooltip> getTooltips() {
        return tooltips;
    }

    public void drawTooltips(@NotNull MatrixStack matrix, Screen screen, int mouseX, int mouseY) {
        boolean showAdvanced = Minecraft.getInstance().options.advancedItemTooltips;
        tooltips.forEach(t -> {
            if (t.isHovered(mouseX, mouseY)) {
                screen.renderComponentTooltip(matrix, showAdvanced? t.getAdvancedTooltip(): t.getTooltip(), mouseX, mouseY);
            }
        });
    }

    public boolean isClicked(double mouseX, double mouseY) {
        return this.clicked(mouseX, mouseY);
    }

    public void tick(int ticksActive) {
        // implement to use
    }
}
