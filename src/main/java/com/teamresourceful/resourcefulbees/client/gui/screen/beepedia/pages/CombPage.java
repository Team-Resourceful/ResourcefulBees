package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.CombBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;

public class CombPage extends BeepediaPage {

    private CombBeepediaStats stats;

    public CombPage(BeepediaScreenArea screenArea) {
        super(screenArea);
    }

    public void preInit(BeepediaScreen beepedia, CombBeepediaStats stats) {
        super.preInit(beepedia, beeStats.get(listItem), subPage, subPageTab);
        this.stats = stats;
    }

    @Override
    public void registerButtons(BeepediaScreen beepedia) {

    }

    public static void initPages() {

    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }

    @Override
    public void addSearch() {

    }
}
