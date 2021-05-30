package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.utils.BeepediaUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class BeeCombatPage extends BeeDataPage {

    protected BeeCombatPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent healthName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.health");
        TranslatableComponent damageName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage");
        TranslatableComponent stingerName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.stinger");
        TranslatableComponent passiveName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive");
        TranslatableComponent poisonName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.poison");

        damageName.append(new TextComponent("" + (int) beeData.getCombatData().getAttackDamage()));
        healthName.append(new TextComponent("" + (int) beeData.getCombatData().getBaseHealth()));
        stingerName.append(BeepediaUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        passiveName.append(BeepediaUtils.getYesNo(beeData.getCombatData().isPassive()));
        poisonName.append(BeepediaUtils.getYesNo(beeData.getCombatData().inflictsPoison()));

        font.draw(matrix, healthName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 34, -1);
        font.draw(matrix, damageName.withStyle(ChatFormatting.GRAY), (float) xPos + 86, (float) yPos + 34, -1);
        font.draw(matrix, passiveName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 46, -1);
        font.draw(matrix, poisonName.withStyle(ChatFormatting.GRAY), (float) xPos + 86, (float) yPos + 46, -1);
        font.draw(matrix, stingerName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 58, -1);
    }

    @Override
    public void addSearch() {

    }
}
