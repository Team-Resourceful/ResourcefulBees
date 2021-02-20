package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class BeeDataPage extends BeepediaPage {

    CustomBeeData beeData;

    public BeeDataPage(BeepediaScreen beepedia, BeepediaScreen.Page type, CustomBeeData beeData) {
        super(beepedia, type);
        this.beeData = beeData;
    }

    @Override
    public String getTranslation() {
        return beeData.getTranslation().getString();
    }
}
