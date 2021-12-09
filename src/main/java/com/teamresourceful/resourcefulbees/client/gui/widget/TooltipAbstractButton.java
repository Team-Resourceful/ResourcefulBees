package com.teamresourceful.resourcefulbees.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TooltipAbstractButton extends TooltipWidget {

    public TooltipAbstractButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public abstract void onPress();

    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    public boolean keyPressed(int keycode, int scanCode, int modifiers) {
        if (this.active && this.visible) {
            if (keycode != 257 && keycode != 32 && keycode != 335) {
                return false;
            } else {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            }
        } else {
            return false;
        }
    }
}
