package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BeeCombatPage extends BeeDataPage {

    protected BeeCombatPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent healthName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.HEALTH, beeData.getCombatData().getBaseHealth());
        TranslationTextComponent damageName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.DAMAGE, beeData.getCombatData().getAttackDamage());
        TranslationTextComponent stingerName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.STINGER, BeepediaUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        TranslationTextComponent passiveName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.PASSIVE, BeepediaUtils.getYesNo(beeData.getCombatData().isPassive()));
        TranslationTextComponent poisonName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.POISON, BeepediaUtils.getYesNo(beeData.getCombatData().inflictsPoison()));

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
