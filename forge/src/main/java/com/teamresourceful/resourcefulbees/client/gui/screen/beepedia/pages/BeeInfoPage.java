package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.utils.BeepediaUtils;
import com.teamresourceful.resourcefulbees.utils.CycledArray;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class BeeInfoPage extends BeeDataPage {
    private static final TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info");
    private static final TranslatableComponent sizeName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.size");
    private static final TranslatableComponent healthName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.health");
    private static final TranslatableComponent damageName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage");
    private static final TranslatableComponent stingerName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.stinger");
    private static final TranslatableComponent passiveName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive");
    private static final TranslatableComponent poisonName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.poison");
    private static final TranslatableComponent timeName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.time");

    private Entity entityFlower = null;
    private final CycledArray<Block> flowers;


    public BeeInfoPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        flowers = new CycledArray<>(beeData.getCoreData().getBlockFlowers());
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        sizeName.copy().append(BeepediaUtils.getSizeName(beeData.getRenderData().getSizeModifier()));
        damageName.copy().append(new TextComponent("" + (int) beeData.getCombatData().getAttackDamage()));
        healthName.copy().append(new TextComponent("" + (int) beeData.getCombatData().getBaseHealth()));
        stingerName.copy().append(BeepediaUtils.getYesNo(beeData.getCombatData().removeStingerOnAttack()));
        passiveName.copy().append(BeepediaUtils.getYesNo(beeData.getCombatData().isPassive()));
        poisonName.copy().append(BeepediaUtils.getYesNo(beeData.getCombatData().inflictsPoison()));
        timeName.copy().append(beeData.getCoreData().getMaxTimeInHive() / 20 + "s");

        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), xPos, (float) yPos + 8, -1);
        font.draw(matrix, sizeName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 22, -1);
        font.draw(matrix, timeName.withStyle(ChatFormatting.GRAY), (float) xPos + 76, (float) yPos + 22, -1);
        font.draw(matrix, healthName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 34, -1);
        font.draw(matrix, damageName.withStyle(ChatFormatting.GRAY), (float) xPos + 76, (float) yPos + 34, -1);
        font.draw(matrix, passiveName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 46, -1);
        font.draw(matrix, poisonName.withStyle(ChatFormatting.GRAY), (float) xPos + 76, (float) yPos + 46, -1);
        font.draw(matrix, stingerName.withStyle(ChatFormatting.GRAY), xPos, (float) yPos + 58, -1);
    }

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent flowerName = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info.flower");
        if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
            font.draw(matrix, flowerName.withStyle(ChatFormatting.GRAY), (float) xPos, (float) yPos + 75, -1);
            beepedia.drawSlot(matrix, flowers.get(), xPos + 36, yPos + 70);
        } else if (beeData.getCoreData().getEntityFlower().isPresent()) {
            EntityType<?> entityType = beeData.getCoreData().getEntityFlower().get();
            entityFlower = entityType.create(beepedia.getMinecraft().level);
            font.draw(matrix, flowerName.withStyle(ChatFormatting.GRAY), (float) xPos, (float) yPos + 80, -1);
            RenderUtils.renderEntity(matrix, entityFlower, beepedia.getMinecraft().level, (float) xPos + 45, (float) yPos + 75, -45, 1.25f);
        }
    }

    @Override
    public void addSearch() {
        parent.addSearchBeeTag(beeData.getCombatData().isPassive() ? "passive" : "aggressive");
        if (beeData.getCombatData().inflictsPoison()) parent.addSearchBeeTag("poisonous");
        if (beeData.getCombatData().removeStingerOnAttack()) parent.addSearchBeeTag("stinger");
        if (beeData.getCoreData().getEntityFlower().isPresent()) {
            if (entityFlower instanceof CustomBeeEntity) {
                parent.addSearchBee(entityFlower, ((CustomBeeEntity) entityFlower).getBeeType());
            } else {
                parent.addSearchEntity(entityFlower);
            }
        } else {
            beeData.getCoreData().getBlockFlowers().forEach(parent::addSearchItem);
        }
        parent.addSearchBeeTag(BeepediaUtils.getSizeName(beeData.getRenderData().getSizeModifier()).getString());
    }

    @Override
    public void tick(int ticksActive) {
        if (Screen.hasShiftDown()) return;
        if (ticksActive % 20 == 0) {
            if (flowers.isEmpty()) return;
            flowers.cycle();
        }
    }
}
