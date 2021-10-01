package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.*;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.BeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.client.gui.widget.ButtonList;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;

public class ListPage extends BeepediaPage {

    private final String type;
    private final ItemStack item;
    private final ITextComponent tooltip;
    private final Map<String, ? extends BeepediaStats> stats;
//    private final int buttonX;
//    private final int buttonY;

    public ListPage(String type, ItemStack item, BeepediaScreenArea screenArea, int offset, ITextComponent tooltip, Map<String, ? extends BeepediaStats> stats) {
        super(screenArea);
        this.type = type;
        this.stats = stats;
        this.item = item;
        this.tooltip = tooltip;
//        this.buttonX = x + offset;
//        this.buttonY = y + 8;
    }

//    @Override
//    public void registerButtons(BeepediaScreen beepedia) {
//        TabImageButton button = new TabImageButton(buttonX, buttonY, 20, 20, 0, 0, 20, BeepediaImages.BUTTON_IMAGE, item, 2, 2, onPress ->
//                BeepediaHandler.setActiveList(type), beepedia.getTooltipProvider(tooltip));
//        beepedia.addButton(button);
//        ButtonList buttonList = new ButtonList(beepedia.guiLeft + 8, beepedia.guiTop + 31, 121, 141, 21, button, stats);
//    }

    @Override
    public void registerButtons(BeepediaScreen beepedia) {

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
}
