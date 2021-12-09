package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.widget.beepedia.ItemSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeepediaListButton extends Pane {

    public BeepediaListButton(int x, int y, int width, int height, Component text, ItemStack stack) {
        super(x, y, width, height, text);
        add(new Label(text, 22, 0));
        add(new ItemSlot(0, 0, stack));
    }

    @Override
    public void renderChild(PoseStack matrix, TooltipWidget child, int mouseX, int mouseY, float partialTicks) {
        child.active = this.active;
        super.renderChild(matrix, child, mouseX, mouseY, partialTicks);
    }

}