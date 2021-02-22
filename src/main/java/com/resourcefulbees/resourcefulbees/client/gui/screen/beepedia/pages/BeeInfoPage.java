package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class BeeInfoPage extends BeeDataPage {

    List<Block> flowers;
    int counter;
    int size;

    public BeeInfoPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        flowers = BeeInfoUtils.getFlowers(beeData.getFlower());
        counter = 0;
        size = flowers.size();
    }

    @Override
    public void openPage() {
        super.openPage();
    }

    @Override
    public void closePage() {
        super.closePage();
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent sizeName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.size");
        TranslationTextComponent flowerName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");
        TranslationTextComponent healthName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.health");
        TranslationTextComponent damageName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage");
        TranslationTextComponent stingerName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.stinger");
        TranslationTextComponent passiveName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive");
        TranslationTextComponent poisonName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.poison");

        sizeName.append(BeeInfoUtils.getSizeName(beeData.getSizeModifier()));
        damageName.append(new StringTextComponent("" + (int) beeData.getCombatData().getAttackDamage()));
        healthName.append(new StringTextComponent("" + (int) beeData.getCombatData().getBaseHealth()));
        stingerName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        passiveName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().isPassive()));
        poisonName.append(BeeInfoUtils.getYesNo(beeData.getCombatData().inflictsPoison()));
        font.draw(matrix, flowerName, xPos, yPos + 8, Color.parse("white").getRgb());
        font.draw(matrix, sizeName, xPos, yPos + 26, Color.parse("white").getRgb());
        font.draw(matrix, healthName, xPos, yPos + 38, Color.parse("white").getRgb());
        font.draw(matrix, damageName, xPos + 84, yPos + 38, Color.parse("white").getRgb());
        font.draw(matrix, passiveName, xPos, yPos + 50, Color.parse("white").getRgb());
        font.draw(matrix, poisonName, xPos + 84, yPos + 50, Color.parse("white").getRgb());
        font.draw(matrix, stingerName, xPos, yPos + 62, Color.parse("white").getRgb());
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        if (!flowers.isEmpty()) {
            beepedia.drawSlot(matrix, flowers.get(counter), xPos + 36, yPos + 2, mouseX, mouseY);
        }
    }

    @Override
    public void tick(int ticksActive) {
        if (BeeInfoUtils.isShiftPressed()) return;
        if (ticksActive % 20 == 0) {
            counter++;
            if (counter >= size) {
                counter = 0;
            }
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }
}
