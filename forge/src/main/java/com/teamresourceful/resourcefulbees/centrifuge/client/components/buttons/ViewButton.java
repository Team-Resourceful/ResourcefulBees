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

public class ViewButton extends AbstractButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/view_btn.png");

    private final Runnable onPress;

    public ViewButton(int x, int y, Runnable onPress) {
        super(x+1, y+1, 13, 13, CommonComponents.EMPTY);
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(TEXTURE, getX()-1, getY()-1, this.isHovered ? 17 : 0, 0, this.width+4, this.height+2, 34, 15);
        if (isHovered()) {
            ScreenUtils.setTooltip(CentrifugeTranslations.VIEW);
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
