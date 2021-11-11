package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.utils.BeepediaUtils;
import com.teamresourceful.resourcefulbees.common.utils.CycledArray;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BeeInfoPage extends BeeDataPage {

    private Entity entityFlower = null;
    private final CycledArray<Block> flowers;


    public BeeInfoPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        flowers = new CycledArray<>(beeData.getCoreData().getBlockFlowers());
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent sizeName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.SIZE, BeepediaUtils.getSizeName(beeData.getRenderData().getSizeModifier()));
        TranslationTextComponent timeName = new TranslationTextComponent(TranslationConstants.Beepedia.Info.TIME, beeData.getCoreData().getMaxTimeInHive() / 20);

        font.draw(matrix, TranslationConstants.Beepedia.Info.INFO.withStyle(TextFormatting.WHITE), xPos, (float) yPos + 8, -1);
        font.draw(matrix, sizeName.withStyle(TextFormatting.GRAY), xPos, (float) yPos + 22, -1);
        font.draw(matrix, timeName.withStyle(TextFormatting.GRAY), (float) xPos + 86, (float) yPos + 22, -1);

    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().font;
        if (!beeData.getCoreData().getBlockFlowers().isEmpty()) {
            font.draw(matrix, TranslationConstants.Beepedia.Info.FLOWER.withStyle(TextFormatting.GRAY), (float) xPos, (float) yPos + 75, -1);
            beepedia.drawSlot(matrix, flowers.get(), xPos + 36, yPos + 70);
        } else if (beeData.getCoreData().getEntityFlower().isPresent()) {
            EntityType<?> entityType = beeData.getCoreData().getEntityFlower().get();
            entityFlower = entityType.create(beepedia.getMinecraft().level);
            font.draw(matrix, TranslationConstants.Beepedia.Info.FLOWER.withStyle(TextFormatting.GRAY), (float) xPos, (float) yPos + 80, -1);
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
