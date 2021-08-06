package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BeeCombatPage extends BeeDataPage {

    protected BeeCombatPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent healthName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.health");
        TranslationTextComponent damageName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage");
        TranslationTextComponent stingerName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.stinger");
        TranslationTextComponent passiveName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive");
        TranslationTextComponent poisonName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.poison");

        damageName.append(new StringTextComponent("" + (int) beeData.getCombatData().getAttackDamage()));
        healthName.append(new StringTextComponent("" + (int) beeData.getCombatData().getBaseHealth()));
        stingerName.append(BeepediaUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        passiveName.append(BeepediaUtils.getYesNo(beeData.getCombatData().isPassive()));
        poisonName.append(BeepediaUtils.getYesNo(beeData.getCombatData().inflictsPoison()));

        font.draw(matrix, healthName.withStyle(TextFormatting.GRAY), xPos, (float) yPos + 34, -1);
        font.draw(matrix, damageName.withStyle(TextFormatting.GRAY), (float) xPos + 86, (float) yPos + 34, -1);
        font.draw(matrix, passiveName.withStyle(TextFormatting.GRAY), xPos, (float) yPos + 46, -1);
        font.draw(matrix, poisonName.withStyle(TextFormatting.GRAY), (float) xPos + 86, (float) yPos + 46, -1);
        font.draw(matrix, stingerName.withStyle(TextFormatting.GRAY), xPos, (float) yPos + 58, -1);
    }

    @Override
    public void addSearch() {

    }
}
