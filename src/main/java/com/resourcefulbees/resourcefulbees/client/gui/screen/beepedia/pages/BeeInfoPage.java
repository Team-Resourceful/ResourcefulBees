package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
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
        TranslationTextComponent damageName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.damage");
        TranslationTextComponent passiveName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info.passive");

        sizeName.append(BeeInfoUtils.getSizeName(beeData.getSizeModifier()));
        font.draw(matrix, flowerName, xPos, yPos + 20, Color.parse("white").getRgb());
        if (!flowers.isEmpty()) {
            beepedia.getMinecraft().getItemRenderer().renderInGui(new ItemStack(flowers.get(counter)), xPos + 32, yPos + 14);
        }
        font.draw(matrix, sizeName, xPos, yPos + 32, Color.parse("white").getRgb());
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

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
}
