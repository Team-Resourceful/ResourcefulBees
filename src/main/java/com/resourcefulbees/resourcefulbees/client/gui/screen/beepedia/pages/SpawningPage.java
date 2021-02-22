package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SpawningPage extends BeeDataPage {

    private int subScrollPos;
    private int scrollHeight = 10;

    public SpawningPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        this.beeData = beeData;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (beepedia.isBiomesOpen()) {
            TranslationTextComponent biomesTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.biomes");
        } else {
            FontRenderer font = beepedia.getMinecraft().fontRenderer;
            TranslationTextComponent groupName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.group");
            TranslationTextComponent heightName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.height");
            TranslationTextComponent weightName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.weight");
            TranslationTextComponent lightName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.light");
            groupName.append(new StringTextComponent(String.format("%d - %d", beeData.getSpawnData().getMinGroupSize(), beeData.getSpawnData().getMaxGroupSize())));
            heightName.append(new StringTextComponent(String.format("%d - %d", beeData.getSpawnData().getMinYLevel(), beeData.getSpawnData().getMaxYLevel())));
            weightName.append(new StringTextComponent(String.format("%d", beeData.getSpawnData().getSpawnWeight())));
            lightName.append(BeeInfoUtils.getLightName(beeData.getSpawnData().getLightLevel()));
            font.draw(matrix, groupName, xPos, yPos + 6, Color.parse("white").getRgb());
            font.draw(matrix, heightName, xPos, yPos + 18, Color.parse("white").getRgb());
            font.draw(matrix, weightName, xPos, yPos + 30, Color.parse("white").getRgb());
            font.draw(matrix, lightName, xPos, yPos + 42, Color.parse("white").getRgb());
        }
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public void tick(int ticksActive) {

    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (!beepedia.isBiomesOpen()) return false;
        if (mouseX >= xPos && mouseY >= yPos && mouseX <= xPos + 169 && mouseY <= yPos + 106) {
            subScrollPos += scrollAmount * 8;
            if (subScrollPos < 0) subScrollPos = 0;
            else if (subScrollPos > subPageHeight - scrollHeight) subScrollPos = subPageHeight - scrollHeight;
            beepedia.setSubPageScroll(subScrollPos);
            return true;
        }
        return false;
    }

    @Override
    public void closePage() {
        beepedia.setBiomesOpen(false);
        beepedia.setSubPageScroll(0);
        super.closePage();
    }
}
