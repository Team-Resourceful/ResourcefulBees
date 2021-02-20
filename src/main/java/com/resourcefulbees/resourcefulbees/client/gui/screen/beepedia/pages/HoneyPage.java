package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.DefaultHoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class HoneyPage extends BeepediaPage {

    private HoneyBottleData bottleData;
    String translation;

    public HoneyPage(BeepediaScreen beepedia, HoneyBottleData bottleData) {
        super(beepedia, BeepediaScreen.Page.HONEY);
        this.bottleData = bottleData;
        initTranslation();
        ItemStack stack;
        if (bottleData instanceof DefaultHoneyBottleData) {
            stack = new ItemStack(((DefaultHoneyBottleData) bottleData).bottle);
        } else {
            stack = new ItemStack(bottleData.getHoneyBottleRegistryObject().get());
        }

        listButton = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, stack, 2, 2, new TranslationTextComponent(stack.getItem().getTranslationKey()), 20, 4, onPress -> {
            beepedia.setActive(this);
        });
    }

    private void initTranslation() {
        translation = "";
        if (bottleData.getHoneyBottleRegistryObject() != null)
            translation += " " + new TranslationTextComponent(bottleData.getHoneyBottleRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyFluidBlockRegistryObject() != null)
            translation += " " + new TranslationTextComponent(bottleData.getHoneyFluidBlockRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBlockItemRegistryObject() != null)
            translation += " " + new TranslationTextComponent(bottleData.getHoneyBlockItemRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBucketItemRegistryObject() != null)
            translation += " " + new TranslationTextComponent(bottleData.getHoneyBucketItemRegistryObject().get().getTranslationKey()).getString();
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public String getTranslation() {
        return translation;
    }
}
