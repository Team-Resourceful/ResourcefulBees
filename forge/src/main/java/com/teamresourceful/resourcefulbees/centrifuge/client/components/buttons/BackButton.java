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

public class BackButton extends AbstractButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/back_btn.png");

    private final Runnable onPress;

    public BackButton(int x, int y, Runnable onPress) {
        super(x+1, y+1, 11, 11, CommonComponents.EMPTY);
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(TEXTURE, this.getX()-1, this.getY()-1, this.isHovered ? 15 : 0, 0, this.width+4, this.height+2, 30, 13);
        if (isHovered()) {
            ScreenUtils.setTooltip(CentrifugeTranslations.BACK);
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
