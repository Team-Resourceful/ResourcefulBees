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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
        MutableComponent healthName = Component.translatable(TranslationConstants.Beepedia.Info.HEALTH, beeData.combatData().baseHealth());
        MutableComponent damageName = Component.translatable(TranslationConstants.Beepedia.Info.DAMAGE, beeData.combatData().attackDamage());
        MutableComponent stingerName = Component.translatable(TranslationConstants.Beepedia.Info.STINGER, BeepediaUtils.getYesNo(beeData.combatData().removeStingerOnAttack()));
        MutableComponent passiveName = Component.translatable(TranslationConstants.Beepedia.Info.PASSIVE, BeepediaUtils.getYesNo(beeData.combatData().isPassive()));
        MutableComponent poisonName = Component.translatable(TranslationConstants.Beepedia.Info.POISON, BeepediaUtils.getYesNo(beeData.combatData().inflictsPoison()));

        font.draw(matrix, healthName.withStyle(ChatFormatting.GRAY), x, y + 34f, -1);
        font.draw(matrix, damageName.withStyle(ChatFormatting.GRAY), x + 86f, y + 34f, -1);
        font.draw(matrix, passiveName.withStyle(ChatFormatting.GRAY), x, y + 46f, -1);
        font.draw(matrix, poisonName.withStyle(ChatFormatting.GRAY), x + 86f, y + 46f, -1);
        font.draw(matrix, stingerName.withStyle(ChatFormatting.GRAY), x, y + 58f, -1);
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
