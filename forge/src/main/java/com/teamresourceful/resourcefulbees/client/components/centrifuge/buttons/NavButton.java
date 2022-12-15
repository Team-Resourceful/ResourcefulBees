package com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
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
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(TEXTURE);
        if (reverse) {
            drawButton(stack,this.x+3, this.y+3, isHovered ? 7 : 0);
        } else {
            drawButton(stack,this.x, this.y+3, isHovered ? 10 : 3);
        }
    }

    private void drawButton(PoseStack stack, int x, int y, int u) {
        blit(stack, x, y, u, 0, 4, 7, 14, 7);
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
        //not used
    }
}
