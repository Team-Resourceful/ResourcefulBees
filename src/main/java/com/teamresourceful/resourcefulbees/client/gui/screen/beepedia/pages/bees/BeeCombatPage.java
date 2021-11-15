package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeCombatPage extends BeeDataPage {

    public BeeCombatPage(ScreenArea screenArea) {
        super(screenArea);
    }

    @Override
    protected void preInit(BeepediaScreen beepedia, ScreenArea screenArea, CustomBeeData beeData) {
        super.preInit(beepedia, screenArea, beeData);
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent healthName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.HEALTH, beeData.getCombatData().getBaseHealth());
        TranslationTextComponent damageName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.DAMAGE, beeData.getCombatData().getAttackDamage());
        TranslationTextComponent stingerName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.STINGER, BeepediaUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        TranslationTextComponent passiveName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.PASSIVE, BeepediaUtils.getYesNo(beeData.getCombatData().isPassive()));
        TranslationTextComponent poisonName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.POISON, BeepediaUtils.getYesNo(beeData.getCombatData().inflictsPoison()));

        font.draw(matrix, healthName.withStyle(TextFormatting.GRAY), x, y + 34, -1);
        font.draw(matrix, damageName.withStyle(TextFormatting.GRAY), x + 86, y + 34, -1);
        font.draw(matrix, passiveName.withStyle(TextFormatting.GRAY), x, y + 46, -1);
        font.draw(matrix, poisonName.withStyle(TextFormatting.GRAY), x + 86, y + 46, -1);
        font.draw(matrix, stingerName.withStyle(TextFormatting.GRAY), x, y + 58, -1);
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
