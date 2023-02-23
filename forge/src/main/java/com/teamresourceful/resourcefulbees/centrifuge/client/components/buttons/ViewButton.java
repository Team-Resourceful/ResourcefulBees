package com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewButton extends AbstractButton implements TooltipProvider {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/view_btn.png");

    private final Runnable onPress;

    public ViewButton(int x, int y, Runnable onPress) {
        super(x+1, y+1, 13, 13, CommonComponents.EMPTY);
        this.onPress = onPress;
    }

    @Override
    public void renderButton(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(TEXTURE);
        blit(stack, this.x-1, this.y-1, this.isHovered ? 17 : 0, 0, this.width+4, this.height+2, 34, 15);
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
        return this.isHovered ? List.of(CentrifugeTranslations.VIEW) : List.of();
    }
}
