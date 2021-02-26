package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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

    Button leftArrow;
    Button rightArrow;
    Button parentsButton;
    Button childrenButton;

    private final ResourceLocation breedingImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/breeding.png");

    private final TranslationTextComponent parentsTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");
    private final TranslationTextComponent childrenTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");
    private List<BreedingObject> activeList = null;
    private int activePage = 0;

    public BreedingPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        children = BeeRegistry.getRegistry().getChildren(beeData);
        parents = BeeRegistry.getRegistry().getParents(beeData);
        children.forEach((p, l) -> l.getMap().forEach((w, b) -> childrenBreeding.add(new BreedingObject(p, b))));
        parents.forEach((p, b) -> parentBreeding.add(new BreedingObject(p, b)));
        leftArrow = new ImageButton(xPos + (subPageWidth / 2) - 28, yPos + subPageHeight - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
        rightArrow = new ImageButton(xPos + (subPageWidth / 2) + 20, yPos + subPageHeight - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
        parentsButton = new ImageButton(xPos + (subPageWidth / 2) - 38, yPos + 6, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> toggleActiveList());
        childrenButton = new ImageButton(xPos + (subPageWidth / 2) + 30, yPos + 6, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> toggleActiveList());
        leftArrow.visible = false;
        rightArrow.visible = false;
        parentsButton.visible = false;
        childrenButton.visible = false;
        beepedia.addButton(leftArrow);
        beepedia.addButton(rightArrow);
        beepedia.addButton(parentsButton);
        beepedia.addButton(childrenButton);

        parentBreeding.sort((o1, o2) -> {
            if (o1.isBase) return 1;
            else return -1;
        });
        childrenBreeding.sort((o1, o2) -> {
            if (o1.isBase) return 1;
            else return -1;
        });
    }


    private void toggleActiveList(boolean parentsList) {
        BeepediaScreen.currScreenState.setParentBreeding(parentsList);
        if (parentsList && !parentBreeding.isEmpty()) {
            activeList = parentBreeding;
        } else if (!parentsList && !childrenBreeding.isEmpty()) {
            activeList = childrenBreeding;
        } else {
            activeList = null;
        }
        activePage = BeepediaScreen.currScreenState.getBreedingPage();
        if (activePage >= activeList.size()) activePage = 0;
        BeepediaScreen.currScreenState.setBreedingPage(activePage);
    }

    private void toggleActiveList() {
        toggleActiveList(!BeepediaScreen.currScreenState.isParentBreeding());
    }

    private void nextPage() {
        activePage++;
        if (activePage >= activeList.size()) activePage = 0;
        BeepediaScreen.currScreenState.setBreedingPage(activePage);
    }

    private void prevPage() {
        activePage--;
        if (activePage < 0) activePage = activeList.size() - 1;
        BeepediaScreen.currScreenState.setBreedingPage(activePage);
    }

    @Override
    public void openPage() {
        super.openPage();
        toggleActiveList(BeepediaScreen.currScreenState.isParentBreeding());
    }


    @Override
    public void closePage() {
        super.closePage();
        leftArrow.visible = false;
        rightArrow.visible = false;
        parentsButton.visible = false;
        childrenButton.visible = false;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        showButtons();
        Minecraft.getInstance().textureManager.bindTexture(breedingImage);
        AbstractGui.drawTexture(matrix, xPos, yPos + 22, 0, 0, 128, 64, 128, 64);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = BeepediaScreen.currScreenState.isParentBreeding() ? parentsTitle : childrenTitle;
        int padding = font.getWidth(title) / 2;
        font.draw(matrix, title, xPos + (subPageWidth / 2) - padding, (float) yPos + 8, TextFormatting.WHITE.getColor());
        if (activeList.size() > 1) {
            StringTextComponent page = new StringTextComponent(String.format("%d / %d", activePage + 1, activeList.size()));
            padding = font.getWidth(page) / 2;
            font.draw(matrix, page, xPos + (subPageWidth / 2) - padding, (float) yPos + subPageHeight - 14, TextFormatting.WHITE.getColor());
        }
    }

    private boolean shouldShowButtons() {
        return (!parentBreeding.isEmpty() || !childrenBreeding.isEmpty()) && !baseOnly();
    }

    private void showButtons() {
        parentsButton.visible = shouldShowButtons();
        childrenButton.visible = shouldShowButtons();
        leftArrow.visible = activeList.size() > 1;
        rightArrow.visible = activeList.size() > 1;
    }

    private boolean baseOnly() {
        if (parentBreeding.size() == 1 && childrenBreeding.size() == 1) {
            if (parentBreeding.get(0).isBase && childrenBreeding.get(0).isBase) return true;
        }
        return false;
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        if (activeList == null || activeList.isEmpty()) return;
        if (!activeList.isEmpty()) {
            activeList.get(activePage).draw(matrix, mouseX, mouseY);
        }
    }

    @Override
    public void tick(int ticksActive) {
        if (activeList == null || activeList.isEmpty()) return;
        if (activeList.get(activePage) != null) {
            activeList.get(activePage).tick(ticksActive);
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (activeList == null || activeList.isEmpty()) return;
        activeList.get(activePage).drawTooltips(matrixStack, mouseX, mouseY);
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
        Child child;
        boolean isParent;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        public boolean isBase;

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

        public BreedingObject(Pair<String, String> parents, CustomBeeData child) {
            initParents(parents);
            this.child = new Child(parents, child);
            isBase = parents.getLeft().equals(parents.getRight()) && parents.getLeft().equals(child.getName());
            isParent = false;
        }

        public void drawParent1(MatrixStack matrix) {
            BeepediaScreen.renderEntity(matrix, parent1Entity, beepedia.getMinecraft().world, (float) xPos + 6, (float) yPos + 32, 45, 1);
        }

        public void drawParent2(MatrixStack matrix) {
            BeepediaScreen.renderEntity(matrix, parent2Entity, beepedia.getMinecraft().world, (float) xPos + 60, (float) yPos + 32, -45, 1);
        }

        private void drawChild(MatrixStack matrix) {
            FontRenderer font = beepedia.getMinecraft().fontRenderer;
            BeepediaScreen.renderEntity(matrix, child.entity, beepedia.getMinecraft().world, (float) xPos + 130, (float) yPos + 42, -45, 1);

            StringTextComponent text = new StringTextComponent(decimalFormat.format(child.weight));
            int padding = font.getWidth(text) / 2;
            font.draw(matrix, text, (float) xPos + 103f - (float) padding, (float) yPos + 56, TextFormatting.GRAY.getColor());
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

        public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
            // todo this
        }

        public void draw(MatrixStack matrix, int mouseX, int mouseY) {
            drawParent1(matrix);
            drawParent2(matrix);
            drawChild(matrix);
            drawParent1Item(matrix, mouseX, mouseY);
            drawParent2Item(matrix, mouseX, mouseY);
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
