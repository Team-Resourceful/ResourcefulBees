package com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CloseButton extends AbstractButton implements TooltipProvider {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/close_btn.png");

    private final Runnable onPress;

    public CloseButton(int x, int y, Runnable onPress) {
        super(x+1, y+1, 11, 11, CommonComponents.EMPTY);
        this.onPress = onPress;
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(TEXTURE);
        blit(stack, this.x-1, this.y-1, this.isHovered ? 13 : 0, 0, this.width+2, this.height+2, 26, 13);
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
        //not needed
    }

    @Override
    public @NotNull List<Component> getTooltip(int mouseX, int mouseY) {
        return this.isHovered ? List.of(TranslationConstants.Centrifuge.CLOSE) : List.of();
    }
}
