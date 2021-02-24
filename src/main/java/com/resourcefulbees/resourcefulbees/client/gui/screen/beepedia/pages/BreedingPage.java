package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.item.Beepedia;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BreedingPage extends BeeDataPage {
    Map<Pair<String, String>, RandomCollection<CustomBeeData>> children;
    Map<Pair<String, String>, CustomBeeData> parents;
    List<BreedingObject> parentBreeding = new LinkedList<>();
    List<BreedingObject> childrenBreeding = new LinkedList<>();

    BreedingObject breeding = null;

    private ResourceLocation breedingImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/breeding.png");

    public BreedingPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        children = BeeRegistry.getRegistry().getChildren(beeData);
        parents = BeeRegistry.getRegistry().getParents(beeData);
        children.forEach((b, p) -> childrenBreeding.add(new BreedingObject(b, p)));
        parents.forEach((b, p) -> parentBreeding.add(new BreedingObject(b, p)));
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft.getInstance().textureManager.bindTexture(breedingImage);
        beepedia.drawTexture(matrix, xPos, yPos + 22, 0, 0, 128, 64, 128, 64);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding");
        font.draw(matrix, title, xPos, yPos + 8, Color.parse("white").getRgb());
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        if (!parentBreeding.isEmpty()) {
            breeding = parentBreeding.get(0);
            breeding.draw(matrix, mouseX, mouseY, 0);
        }
    }

    @Override
    public void tick(int ticksActive) {
        if (breeding != null) {
            breeding.tick(ticksActive);
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        breeding.drawTooltips(matrixStack, mouseX, mouseY, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }


    public class BreedingObject {
        Entity parent1Entity;
        Entity parent2Entity;
        CustomBeeData parent1Data;
        CustomBeeData parent2Data;
        List<ItemStack> parent1Items = new LinkedList<>();
        List<ItemStack> parent2Items = new LinkedList<>();
        TranslationTextComponent parent1Name;
        TranslationTextComponent parent2Name;
        int parent1Counter = 0;
        int parent2Counter = 0;
        List<Child> children = new LinkedList<>();
        boolean isParent;
        DecimalFormat decimalFormat = new DecimalFormat("##%");

        private void initParents(Pair<String, String> parents) {
            parent1Data = BeeRegistry.getRegistry().getBeeData(parents.getLeft());
            parent2Data = BeeRegistry.getRegistry().getBeeData(parents.getRight());
            parent1Entity = ForgeRegistries.ENTITIES.getValue(parent1Data.getEntityTypeRegistryID()).create(beepedia.getMinecraft().world);
            parent1Name = parent1Data.getTranslation();
            parent2Entity = ForgeRegistries.ENTITIES.getValue(parent2Data.getEntityTypeRegistryID()).create(beepedia.getMinecraft().world);
            parent2Name = parent2Data.getTranslation();
            parent1Items = BeeInfoUtils.getBreedItems(parent1Data);
            parent2Items = BeeInfoUtils.getBreedItems(parent2Data);
        }

        public BreedingObject(Pair<String, String> parents, RandomCollection<CustomBeeData> children) {
            initParents(parents);
            children.getMap().forEach((weight, beeData) -> {
                this.children.add(new Child(parents, beeData));
            });
            isParent = true;
        }

        public BreedingObject(Pair<String, String> parents, CustomBeeData child) {
            initParents(parents);
            this.children.add(new Child(parents, child));
            isParent = false;
        }

        public void drawParent1(MatrixStack matrix) {
            BeepediaScreen.renderEntity(matrix, breeding.parent1Entity, beepedia.getMinecraft().world, xPos + 6, yPos + 32, 225, 1);
        }

        public void drawParent2(MatrixStack matrix) {
            BeepediaScreen.renderEntity(matrix, breeding.parent2Entity, beepedia.getMinecraft().world, xPos + 60, yPos + 32, -45, 1);
        }

        private void drawChild(MatrixStack matrix, int counter) {
            Child child = breeding.children.get(counter);
            FontRenderer font = beepedia.getMinecraft().fontRenderer;
            BeepediaScreen.renderEntity(matrix, child.entity, beepedia.getMinecraft().world, xPos + 130, yPos + 42, 225, 1);

            StringTextComponent text = new StringTextComponent(decimalFormat.format(child.weight));
            int padding = font.getWidth(text) / 2;
            font.draw(matrix, text, xPos + 120 - padding, yPos + 50, Color.parse("gray").getRgb());
        }

        public void drawParent1Item(MatrixStack matrix, int mouseX, int mouseY) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent1Items.get(parent1Counter), xPos + 5, yPos + 53, mouseX, mouseY);
        }

        public void drawParent2Item(MatrixStack matrix, int mouseX, int mouseY) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent2Items.get(parent2Counter), xPos + 59, yPos + 53, mouseX, mouseY);
        }

        public void tick(int ticksActive) {
            if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
                parent1Counter++;
                if (parent1Counter >= parent1Items.size()) parent1Counter = 0;
                parent2Counter++;
                if (parent2Counter >= parent2Items.size()) parent2Counter = 0;
            }
        }

        public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY, int counter) {
            // todo this
        }

        public void draw(MatrixStack matrix, int mouseX, int mouseY, int counter) {
            breeding.drawParent1(matrix);
            breeding.drawParent2(matrix);
            breeding.drawChild(matrix, counter);
            breeding.drawParent1Item(matrix, mouseX, mouseY);
            breeding.drawParent2Item(matrix, mouseX, mouseY);
        }

        public class Child {
            Entity entity;
            TranslationTextComponent name;
            double weight;
            double chance;

            public Child(Pair<String, String> parents, CustomBeeData beeData) {
                entity = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID()).create(beepedia.getMinecraft().world);
                name = beeData.getTranslation();
                weight = BeeRegistry.getRegistry().getAdjustedWeightForChild(beeData, parents.getLeft(), parents.getRight());
                chance = beeData.getBreedData().getBreedChance();
            }
        }
    }
}
