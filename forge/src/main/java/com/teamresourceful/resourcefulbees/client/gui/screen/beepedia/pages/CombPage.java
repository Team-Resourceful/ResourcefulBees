package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.HoneycombData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;

public class CombPage extends BeepediaPage {

    private final HoneycombData data;

    public CombPage(BeepediaScreen beepedia, HoneycombData data, int xPos, int yPos, String id) {
        super(beepedia, xPos, yPos, id);
        this.data = data;
        newListButton(data.getHoneycomb().getDefaultInstance(), data.getHoneycomb().getDefaultInstance().getDisplayName());
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void addSearch() {

    }
}
