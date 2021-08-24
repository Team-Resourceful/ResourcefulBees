package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.utils.BeepediaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BeeCombatPage extends BeeDataPage {

    @Override
    protected void preInit(BeepediaScreen beepedia, BeepediaScreenArea screenArea, CustomBeeData beeData) {
        super.preInit(beepedia, screenArea, beeData);
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

        font.draw(matrix, healthName.withStyle(TextFormatting.GRAY), getXPos(), getYPos() + 34, -1);
        font.draw(matrix, damageName.withStyle(TextFormatting.GRAY), getXPos() + 86, getYPos() + 34, -1);
        font.draw(matrix, passiveName.withStyle(TextFormatting.GRAY), getXPos(), getYPos() + 46, -1);
        font.draw(matrix, poisonName.withStyle(TextFormatting.GRAY), getXPos() + 86, getYPos() + 46, -1);
        font.draw(matrix, stingerName.withStyle(TextFormatting.GRAY), getXPos(), getYPos() + 58, -1);
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
