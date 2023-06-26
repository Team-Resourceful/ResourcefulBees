package com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HelpButton extends AbstractWidget {

    //TODO consider maybe making this a button that enables certain tooltips in various menus
    private static final ResourceLocation TEXTURE = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/centrifuges/help_btn.png");

    public HelpButton(int x, int y) {
        super(x+1, y+1, 11, 11, CommonComponents.EMPTY);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(TEXTURE, this.getX()-1, this.getY()-1, this.isHovered ? 13 : 0, 0, this.width+2, this.height+2, 26, 13);
        if (isHovered()) {
            ScreenUtils.setTooltip(CentrifugeTranslations.HELP);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {

    }
}
