package com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.centrifuge.BaseCentrifugeScreen;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class DisplayTab extends AbstractDisplayModule<BaseCentrifugeScreen<?>> {

    private final @Nullable Component displayText;
    private boolean isSelected;

    //TODO figure out a way to make x/y respect screen resizing without needing to pass in the screen since leftPos/topPos are set
    // on resize and not when the screen is created. maybe override the init method in the screen and cache the leftPos/rightPos in
    // AbstractDisplayModule?
    public DisplayTab(int x, int y, int width, int height, @Nullable Component displayText, BaseCentrifugeScreen<?> screen) {
        super(x, y, width, height, screen);
        this.displayText = displayText;
    }

    public DisplayTab(int x, int y, int width, int height, BaseCentrifugeScreen<?> screen) {
        super(x, y, width, height, screen);
        this.displayText = null;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public void renderBackground(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (isSelected || isMouseOver(mouseX, mouseY)) {
            blit(matrix, screen.getGuiLeft() + getMaxX() - 7, screen.getGuiTop() + y, 66, 196, 6, 13);
        }
    }

    //TODO Why tf is this already taking into account guiLeft and guiTop but renderBG isn't???
    @Override
    public void renderText(PoseStack matrix, int mouseX, int mouseY) {
        if (displayText != null) {
            int color = isSelected || isMouseOver(mouseX, mouseY) ? ClientUtils.FONT_COLOR_2 : ClientUtils.FONT_COLOR_1;
            ClientUtils.TERMINAL_FONT_8.draw(matrix, displayText, x + 4f, y + 6f, color);
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return (button == 0 && isMouseOver(mouseX, mouseY));
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        int w = this.width;
        int h = this.height;
        if ((w | h) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }

        int alteredX = x + screen.getGuiLeft();
        int alteredY = y + screen.getGuiTop();

        // Note: if either dimension is zero, tests below must return false...
        if (mouseX < alteredX || mouseY < alteredY) {
            return false;
        }
        w += alteredX;
        h += alteredY;
        //    overflow || intersect
        return ((w < alteredX || w > mouseX) &&
                (h < alteredY || h > mouseY));
    }
}
