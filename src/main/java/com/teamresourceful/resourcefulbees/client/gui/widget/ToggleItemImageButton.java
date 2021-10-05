package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class ToggleItemImageButton extends ToggleImageButton {


    private final int itemX;
    private final int itemY;
    private final ItemStack item;

    public ToggleItemImageButton(int xIn, int yIn, int xOffset, boolean enabled, ButtonTemplate template, IPressable pressable, ITextComponent tooltip, int itemX, int itemY, ItemStack item) {
        super(xIn, yIn, xOffset, enabled, template, pressable, tooltip);
        this.itemX = itemX;
        this.itemY = itemY;
        this.item = item;
    }

    @Override
    public void renderButton(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        if (this.item != null)
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(this.item, this.x + this.itemX, this.y + this.itemY);
        RenderSystem.enableDepthTest();
    }
}
