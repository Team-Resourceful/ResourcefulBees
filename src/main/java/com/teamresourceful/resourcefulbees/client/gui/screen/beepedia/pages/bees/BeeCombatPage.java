package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TranslatableComponent;
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
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent healthName = new TranslatableComponent(TranslationConstants.Beepedia.Info.HEALTH, beeData.getCombatData().getBaseHealth());
        TranslatableComponent damageName = new TranslatableComponent(TranslationConstants.Beepedia.Info.DAMAGE, beeData.getCombatData().getAttackDamage());
        TranslatableComponent stingerName = new TranslatableComponent(TranslationConstants.Beepedia.Info.STINGER, BeepediaUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        TranslatableComponent passiveName = new TranslatableComponent(TranslationConstants.Beepedia.Info.PASSIVE, BeepediaUtils.getYesNo(beeData.getCombatData().isPassive()));
        TranslatableComponent poisonName = new TranslatableComponent(TranslationConstants.Beepedia.Info.POISON, BeepediaUtils.getYesNo(beeData.getCombatData().inflictsPoison()));

        font.draw(matrix, healthName.withStyle(ChatFormatting.GRAY), x, y + 34, -1);
        font.draw(matrix, damageName.withStyle(ChatFormatting.GRAY), x + 86, y + 34, -1);
        font.draw(matrix, passiveName.withStyle(ChatFormatting.GRAY), x, y + 46, -1);
        font.draw(matrix, poisonName.withStyle(ChatFormatting.GRAY), x + 86, y + 46, -1);
        font.draw(matrix, stingerName.withStyle(ChatFormatting.GRAY), x, y + 58, -1);
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
