package com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CloseButton extends AbstractButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/close_btn.png");

    private final Runnable onPress;

    public CloseButton(int x, int y, Runnable onPress) {
        super(x + 1, y + 1, 11, 11, CommonComponents.EMPTY);
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(TEXTURE, getX() - 1, getY() - 1, this.isHovered ? 13 : 0, 0, this.width + 2, this.height + 2, 26, 13);
        if (this.isHovered()) {
            ScreenUtils.setTooltip(CentrifugeTranslations.CLOSE);
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
