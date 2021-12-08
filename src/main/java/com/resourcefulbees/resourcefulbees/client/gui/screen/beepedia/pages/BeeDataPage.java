package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public abstract class BeeDataPage extends BeepediaPage {

    CustomBeeData beeData;
    BeePage parent;

    public static final TextFormatting titleStyle = TextFormatting.GRAY;
    public static final Style subStyle = Style.EMPTY.withColor(Color.fromRgb(2986296));

    protected BeeDataPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, xPos, yPos, parent.id);
        this.parent = parent;
        this.beeData = beeData;
    }
}
