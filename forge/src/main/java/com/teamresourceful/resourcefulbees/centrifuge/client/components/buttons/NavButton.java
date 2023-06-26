package com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NavButton extends AbstractButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/nav_btn.png");

    private final boolean reverse;
    private final Runnable onPress;

    public NavButton(int x, int y, boolean reverse, Runnable onPress) {
        super(x, y, 7, 13, Component.empty());
        this.reverse = reverse;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (reverse) {
            graphics.blit(TEXTURE, this.getX() + 3, this.getY() + 3, isHovered ? 7 : 0, 0, 4, 7, 14, 7);
        } else {
            graphics.blit(TEXTURE, this.getX(), this.getY() + 3, isHovered ? 10 : 3, 0, 4, 7, 14, 7);
        }
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
