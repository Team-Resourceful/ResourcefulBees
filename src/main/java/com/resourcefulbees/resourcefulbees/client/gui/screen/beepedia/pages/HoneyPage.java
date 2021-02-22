package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.DefaultHoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TranslationTextComponent;

public class HoneyPage extends BeepediaPage {

    private HoneyBottleData bottleData;
    String honey;
    TranslationTextComponent text;

    public HoneyPage(BeepediaScreen beepedia, HoneyBottleData bottleData, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.bottleData = bottleData;
        initTranslation();
        ItemStack stack;
        if (bottleData instanceof DefaultHoneyBottleData) {
            stack = new ItemStack(((DefaultHoneyBottleData) bottleData).bottle);
        } else {
            stack = new ItemStack(bottleData.getHoneyBottleRegistryObject().get());
        }
        this.text = new TranslationTextComponent(stack.getItem().getTranslationKey());
        newListButton(stack, text);
    }

    private void initTranslation() {
        honey = "";
        if (bottleData.getHoneyBottleRegistryObject() != null)
            honey += " " + new TranslationTextComponent(bottleData.getHoneyBottleRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyFluidBlockRegistryObject() != null)
            honey += " " + new TranslationTextComponent(bottleData.getHoneyFluidBlockRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBlockItemRegistryObject() != null)
            honey += " " + new TranslationTextComponent(bottleData.getHoneyBlockItemRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBucketItemRegistryObject() != null)
            honey += " " + new TranslationTextComponent(bottleData.getHoneyBucketItemRegistryObject().get().getTranslationKey()).getString();
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.draw(matrix, text, xPos, yPos + 10, Color.parse("white").getRgb());
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public String getSearch() {
        return honey;
    }

    @Override
    public void tick(int ticksActive) {

    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

    }
}
