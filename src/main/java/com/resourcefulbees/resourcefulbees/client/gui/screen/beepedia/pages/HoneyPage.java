package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.honeydata.DefaultHoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyEffect;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Foods;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class HoneyPage extends BeepediaPage {



    private HoneyBottleData bottleData;
    private String honeySearch;
    private TranslationTextComponent text;

    private int hunger;
    private float saturation;
    private ItemStack bottle;
    private ItemStack bucket = ItemStack.EMPTY;
    private ItemStack block = ItemStack.EMPTY;
    private FluidStack fluid = FluidStack.EMPTY;
    private List<HoneyEffect> effects = new ArrayList<>();

    ResourceLocation hungerBar = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger_bar.png");
    ResourceLocation hungerIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger.png");
    ResourceLocation saturationIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/saturation.png");

    public HoneyPage(BeepediaScreen beepedia, HoneyBottleData bottleData, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.bottleData = bottleData;
        initSearch();
        if (bottleData instanceof DefaultHoneyBottleData) {
            DefaultHoneyBottleData data = (DefaultHoneyBottleData) bottleData;
            this.bottle = new ItemStack(data.bottle);
            this.hunger = Foods.HONEY_BOTTLE.getHealing();
            this.saturation = Foods.HONEY_BOTTLE.getSaturation();
            this.block = new ItemStack(data.block);
            this.fluid = new FluidStack(data.flowingFluid.get(), 1000);
            this.bucket = new ItemStack(data.bucket.get());
            this.text = new TranslationTextComponent("fluid.resourcefulbees.honey");
        } else {
            this.bottle = new ItemStack(bottleData.getHoneyBottleRegistryObject().get());
            this.hunger = bottleData.getHunger();
            this.saturation = bottleData.getSaturation();
            this.effects = bottleData.getEffects();
            if (bottleData.doGenerateHoneyBlock()) {
                this.block = new ItemStack(bottleData.getHoneyBlockItemRegistryObject().get());
            }
            if (bottleData.doGenerateHoneyFluid()) {
                this.bucket = new ItemStack(bottleData.getHoneyBucketItemRegistryObject().get());
                this.fluid = new FluidStack(bottleData.getHoneyFlowingFluidRegistryObject().get(), 1000);
                this.text = bottleData.getFluidTranslation();
            }else {
                this.text = bottleData.getBottleTranslation();
            }
        }
        newListButton(bottle, text);
    }

    private void initSearch() {
        honeySearch = "";
        if (bottleData.getHoneyBottleRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyBottleRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyFluidBlockRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyFluidBlockRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBlockItemRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyBlockItemRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBucketItemRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyBucketItemRegistryObject().get().getTranslationKey()).getString();
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.draw(matrix, text, xPos, (float)yPos + 10, TextFormatting.WHITE.getColor());
    }

    @Override
    public String getSearch() {
        return honeySearch;
    }
}
