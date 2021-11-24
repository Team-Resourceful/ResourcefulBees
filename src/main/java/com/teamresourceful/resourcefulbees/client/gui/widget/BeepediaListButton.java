package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.widget.beepedia.ItemSlot;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeepediaListButton extends Pane {

    public BeepediaListButton(int x, int y, int width, int height, ITextComponent text, ItemStack stack) {
        super(x, y, width, height, text);
        add(new Label(text, 22, 0));
        add(new ItemSlot(0, 0, stack));
    }

    @Override
    public void renderChild(MatrixStack matrix, TooltipWidget child, int mouseX, int mouseY, float partialTicks) {
        child.active = this.active;
        super.renderChild(matrix, child, mouseX, mouseY, partialTicks);
    }

}