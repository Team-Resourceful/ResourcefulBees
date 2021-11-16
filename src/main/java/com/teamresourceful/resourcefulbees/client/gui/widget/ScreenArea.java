package com.teamresourceful.resourcefulbees.client.gui.widget;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenArea {

    public final int xPos;
    public final int yPos;
    public final int height;
    public final int width;

    public ScreenArea(int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
        this.width = width;
    }

    public boolean isHovered(BeepediaScreen beepedia, double mouseX, double mouseY) {
        return MathUtils.inRangeInclusive((int) mouseX, xPos, xPos+width) && MathUtils.inRangeInclusive((int) mouseY, yPos, yPos+height);
    }
}
