package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TooltipButton extends TooltipAbstractButton{

    protected final IPressable onPress;

    public TooltipButton(int x, int y, int width, int height, Component message, IPressable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    public void onPress() {
        this.onPress.onPress(this);
    }

    public void renderButton(@NotNull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrix, mouseX, mouseY, partialTicks);
        if (this.isHovered(mouseX, mouseY)) {
            this.renderToolTip(matrix, mouseX, mouseY);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public interface IPressable {
        void onPress(TooltipButton button);
    }
}
