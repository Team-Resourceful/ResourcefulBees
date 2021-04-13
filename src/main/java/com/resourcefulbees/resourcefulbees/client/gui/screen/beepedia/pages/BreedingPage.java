package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.EntityMutationPage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BreedingPage extends BeeDataPage {

    Map<Pair<String, String>, RandomCollection<CustomBeeData>> children;
    Map<Pair<String, String>, CustomBeeData> parents;
    List<BreedingObject> parentBreeding = new LinkedList<>();
    List<BreedingObject> childrenBreeding = new LinkedList<>();
    List<EntityMutationPage> mutationBreeding = new LinkedList<>();

    List<BreedingPageType> subPages = new LinkedList<>();

    BreedingPageType activeSubPage;

    Button leftArrow;
    Button rightArrow;
    Button prevTab;
    Button nextTab;

    private final ResourceLocation breedingImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/breeding.png");
    private final ResourceLocation mutationsImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/mutate.png");
    private final ResourceLocation infoIcon = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    private final TranslationTextComponent parentsTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");
    private final TranslationTextComponent childrenTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");
    private final TranslationTextComponent mutationsTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.mutations_title");
    private int activePage = 0;

    public BreedingPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, List<EntityMutation> mutations, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        children = BeeRegistry.getRegistry().getChildren(beeData);
        parents = BeeRegistry.getRegistry().getParents(beeData);
        children.forEach((p, l) -> l.getMap().forEach((w, b) -> childrenBreeding.add(new BreedingObject(p, b))));
        parents.forEach((p, b) -> parentBreeding.add(new BreedingObject(p, b)));
        mutations.forEach(b -> mutationBreeding.add(new EntityMutationPage(b.getParent(), b.getInput(), b.getOutputs(), MutationTypes.ITEM, beeData, beepedia)));
        leftArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 28, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
        rightArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 20, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 6, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevTab());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 6, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextTab());
        leftArrow.visible = false;
        rightArrow.visible = false;
        prevTab.visible = false;
        nextTab.visible = false;
        beepedia.addButton(leftArrow);
        beepedia.addButton(rightArrow);
        beepedia.addButton(prevTab);
        beepedia.addButton(nextTab);

        if (!notBaseBreed()) childrenBreeding = new LinkedList<>();

        if (!parentBreeding.isEmpty()) subPages.add(BreedingPageType.PARENTS);
        if (!childrenBreeding.isEmpty()) subPages.add(BreedingPageType.CHILDREN);
        if (!mutationBreeding.isEmpty()) subPages.add(BreedingPageType.MUTATIONS);

        if (BeepediaScreen.currScreenState.getBreedingTab() >= subPages.size())
            BeepediaScreen.currScreenState.setBreedingTab(subPages.size() - 1);

        activeSubPage = subPages.get(BeepediaScreen.currScreenState.getBreedingTab());

        parentBreeding.sort((o1, o2) -> {
            if (o1.isBase) return 1;
            else return -1;
        });
        childrenBreeding.sort((o1, o2) -> {
            if (o1.isBase) return 1;
            else return -1;
        });
    }

    private void nextTab() {
        int tab = BeepediaScreen.currScreenState.getBreedingTab();
        tab++;
        if (tab >= subPages.size()) tab = 0;
        BeepediaScreen.currScreenState.setBreedingTab(tab);
        updatePagePosition();
    }

    private void prevTab() {
        int tab = BeepediaScreen.currScreenState.getBreedingTab();
        tab--;
        if (tab < 0) tab = subPages.size() - 1;
        BeepediaScreen.currScreenState.setBreedingTab(tab);
        updatePagePosition();
    }

    private void updatePagePosition() {
        switch (subPages.get(BeepediaScreen.currScreenState.getBreedingTab())) {
            case PARENTS:
                if (activePage < 0) activePage = parentBreeding.size() - 1;
                else if (activePage >= parentBreeding.size()) activePage = 0;
                activeSubPage = BreedingPageType.PARENTS;
                break;
            case CHILDREN:
                if (activePage < 0) activePage = childrenBreeding.size() - 1;
                else if (activePage >= childrenBreeding.size()) activePage = 0;
                activeSubPage = BreedingPageType.CHILDREN;
                break;
            case MUTATIONS:
                if (activePage < 0) activePage = mutationBreeding.size() - 1;
                else if (activePage >= mutationBreeding.size()) activePage = 0;
                activeSubPage = BreedingPageType.MUTATIONS;
                break;
            default:
                activePage = 0;
        }
    }

    private void nextPage() {
        activePage++;
        updatePagePosition();
        BeepediaScreen.currScreenState.setBreedingPage(activePage);
    }

    private void prevPage() {
        activePage--;
        updatePagePosition();
        BeepediaScreen.currScreenState.setBreedingPage(activePage);
    }

    @Override
    public void openPage() {
        super.openPage();
        int tab = BeepediaScreen.currScreenState.getBreedingTab();
        if (tab >= subPages.size()) tab = subPages.size() - 1;
        BeepediaScreen.currScreenState.setBreedingTab(tab);
        activeSubPage = subPages.get(tab);
    }


    @Override
    public void closePage() {
        super.closePage();
        leftArrow.visible = false;
        rightArrow.visible = false;
        prevTab.visible = false;
        nextTab.visible = false;
    }

    private int getCurrentListSize() {
        switch (activeSubPage) {
            case MUTATIONS:
                return mutationBreeding.size();
            case CHILDREN:
                return childrenBreeding.size();
            case PARENTS:
                return parentBreeding.size();
            default:
                return 0;
        }
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        showButtons();
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent title;
        switch (activeSubPage) {
            case CHILDREN:
                Minecraft.getInstance().textureManager.bind(breedingImage);
                AbstractGui.blit(matrix, xPos, yPos + 22, 0, 0, 128, 64, 128, 64);
                title = childrenTitle;
                break;
            case MUTATIONS:
                Minecraft.getInstance().getTextureManager().bind(mutationsImage);
                AbstractGui.blit(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
                title = mutationsTitle;
                break;
            default:
                Minecraft.getInstance().textureManager.bind(breedingImage);
                AbstractGui.blit(matrix, xPos, yPos + 22, 0, 0, 128, 64, 128, 64);
                title = parentsTitle;
                break;
        }
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        if (getCurrentListSize() > 1) {
            StringTextComponent page = new StringTextComponent(String.format("%d / %d", activePage + 1, getCurrentListSize()));
            padding = font.width(page) / 2;
            font.draw(matrix, page.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
        }
    }

    private boolean shouldShowButtons() {
        return subPages.size() > 1;
    }

    private void showButtons() {
        prevTab.visible = shouldShowButtons();
        nextTab.visible = shouldShowButtons();
        leftArrow.visible = getCurrentListSize() > 1;
        rightArrow.visible = getCurrentListSize() > 1;
    }

    private boolean notBaseBreed() {
        return (parentBreeding.size() != 1 || childrenBreeding.size() != 1 || !parentBreeding.get(0).isBase || !childrenBreeding.get(0).isBase);
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        switch (activeSubPage) {
            case CHILDREN:
                childrenBreeding.get(activePage).draw(matrix);
                break;
            case MUTATIONS:
                mutationBreeding.get(activePage).draw(matrix, xPos, yPos + 22);
                break;
            case PARENTS:
                parentBreeding.get(activePage).draw(matrix);
                break;
            default:
                // do nothing this should never happen
                break;
        }
    }

    @Override
    public String getSearch() {
        String search = "";
        for (BreedingObject breedingObject : childrenBreeding) {
            search = String.format("%s %s %s %s",
                    search,
                    breedingObject.child.name.getString(),
                    breedingObject.parent1Name.getString(),
                    breedingObject.parent2Name.getString());
        }
        for (BreedingObject breedingObject : parentBreeding) {
            search = String.format("%s %s %s %s",
                    search,
                    breedingObject.child.name.getString(),
                    breedingObject.parent1Name.getString(),
                    breedingObject.parent2Name.getString());
        }
        return search;
    }

    @Override
    public void tick(int ticksActive) {
        switch (activeSubPage) {
            case CHILDREN:
                childrenBreeding.get(activePage).tick(ticksActive);
                break;
            case MUTATIONS:
                mutationBreeding.get(activePage).tick(ticksActive);
                break;
            case PARENTS:
                parentBreeding.get(activePage).tick(ticksActive);
                break;
            default:
                // do nothing this should never happen
                break;
        }
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        switch (activeSubPage) {
            case CHILDREN:
                childrenBreeding.get(activePage).drawTooltips(matrixStack, mouseX, mouseY);
                break;
            case MUTATIONS:
                mutationBreeding.get(activePage).drawTooltips(matrixStack, xPos, yPos + 22, mouseX, mouseY);
                break;
            case PARENTS:
                parentBreeding.get(activePage).drawTooltips(matrixStack, mouseX, mouseY);
                break;
            default:
                // do nothing this should never happen
                break;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        switch (activeSubPage) {
            case CHILDREN:
                return childrenBreeding.get(activePage).mouseClicked(mouseX, mouseY);
            case MUTATIONS:
                return mutationBreeding.get(activePage).mouseClick(xPos, yPos + 22, (int) mouseX, (int) mouseY);
            case PARENTS:
                return parentBreeding.get(activePage).mouseClicked(mouseX, mouseY);
            default:
                return false;
        }
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
        Vector2f parent1Pos;
        Vector2f parent2Pos;
        Vector2f childPos;
        Vector2f chancePos;
        int parent1Counter = 0;
        int parent2Counter = 0;
        Child child;
        boolean isParent;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        public boolean isBase;

        private void initParents(Pair<String, String> parents) {
            parent1Data = BeeRegistry.getRegistry().getBeeData(parents.getLeft());
            parent2Data = BeeRegistry.getRegistry().getBeeData(parents.getRight());
            parent1Entity = ForgeRegistries.ENTITIES.getValue(parent1Data.getEntityTypeRegistryID()).create(beepedia.getMinecraft().level);
            parent1Name = parent1Data.getTranslation();
            parent2Entity = ForgeRegistries.ENTITIES.getValue(parent2Data.getEntityTypeRegistryID()).create(beepedia.getMinecraft().level);
            parent2Name = parent2Data.getTranslation();
            parent1Items = BeeInfoUtils.getBreedItems(parent1Data);
            parent2Items = BeeInfoUtils.getBreedItems(parent2Data);
            parent1Pos = new Vector2f((float) xPos + 6, (float) yPos + 22);
            parent2Pos = new Vector2f((float) xPos + 60, (float) yPos + 22);
            childPos = new Vector2f((float) xPos + 130, (float) yPos + 32);
            chancePos = new Vector2f((float) xPos + SUB_PAGE_WIDTH - 17, (float) yPos + 20);
        }

        public BreedingObject(Pair<String, String> parents, CustomBeeData child) {
            initParents(parents);
            this.child = new Child(parents, child);
            isBase = parents.getLeft().equals(parents.getRight()) && parents.getLeft().equals(child.getName());
            isParent = false;
        }

        public void drawParent1(MatrixStack matrix) {
            RenderUtils.renderEntity(matrix, parent1Entity, beepedia.getMinecraft().level, parent1Pos.x, parent1Pos.y, 45, 1);
        }

        public void drawParent2(MatrixStack matrix) {
            RenderUtils.renderEntity(matrix, parent2Entity, beepedia.getMinecraft().level, parent2Pos.x, parent2Pos.y, -45, 1);
        }

        private void drawChild(MatrixStack matrix) {
            FontRenderer font = beepedia.getMinecraft().font;
            RenderUtils.renderEntity(matrix, child.entity, beepedia.getMinecraft().level, childPos.x, childPos.y, -45, 1);

            if (child.chance < 1 && !isBase) {
                StringTextComponent text = new StringTextComponent(decimalFormat.format(child.chance));
                int padding = font.width(text) / 2;
                Minecraft.getInstance().textureManager.bind(infoIcon);
                beepedia.blit(matrix, (int) chancePos.x, (int) chancePos.y, 16, 0, 9, 9);
                font.draw(matrix, text.withStyle(TextFormatting.GRAY), (float) xPos + 140 - (float) padding, (float) yPos + 21, -1);
            }
            StringTextComponent text = new StringTextComponent(decimalFormat.format(child.weight));
            int padding = font.width(text) / 2;
            font.draw(matrix, text.withStyle(TextFormatting.GRAY), (float) xPos + 103f - (float) padding, (float) yPos + 56, -1);
        }

        public void drawParent1Item(MatrixStack matrix) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent1Items.get(parent1Counter), xPos + 5, yPos + 53);
        }

        public void drawParent2Item(MatrixStack matrix) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent2Items.get(parent2Counter), xPos + 59, yPos + 53);
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
            if (BeepediaScreen.mouseHovering(parent1Pos.x, parent1Pos.y, 20, 20, mouseX, mouseY)) {
                drawTooltip(matrixStack, parent1Data, mouseX, mouseY);
            }
            if (BeepediaScreen.mouseHovering(parent2Pos.x, parent2Pos.y, 20, 20, mouseX, mouseY)) {
                drawTooltip(matrixStack, parent2Data, mouseX, mouseY);
            }
            if (BeepediaScreen.mouseHovering(childPos.x, childPos.y, 20, 20, mouseX, mouseY)) {
                drawTooltip(matrixStack, child.beeData, mouseX, mouseY);
            }
            if (BeepediaScreen.mouseHovering(chancePos.x, chancePos.y, 9, 9, mouseX, mouseY) && child.chance < 1) {
                beepedia.renderTooltip(matrixStack, new TranslationTextComponent("gui.resourcefulbees.jei.category.breed_chance.info"), mouseX, mouseY);
            }
        }

        private void drawTooltip(MatrixStack matrixStack, CustomBeeData beeData, int mouseX, int mouseY) {
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(beeData.getTranslation());
            tooltip.add(new StringTextComponent(beeData.getEntityTypeRegistryID().toString()).withStyle(TextFormatting.DARK_GRAY));
            beepedia.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }

        public void draw(MatrixStack matrix) {
            drawParent1(matrix);
            drawParent2(matrix);
            drawChild(matrix);
            drawParent1Item(matrix);
            drawParent2Item(matrix);
        }

        public boolean mouseClicked(double mouseX, double mouseY) {
            if (BeepediaScreen.mouseHovering(parent1Pos.x, parent1Pos.y, 20, 20, (int) mouseX, (int) mouseY)) {
                return openBeePage(parent1Data);
            } else if (BeepediaScreen.mouseHovering(parent2Pos.x, parent2Pos.y, 20, 20, (int) mouseX, (int) mouseY)) {
                return openBeePage(parent2Data);
            } else if (BeepediaScreen.mouseHovering(childPos.x, childPos.y, 20, 20, (int) mouseX, (int) mouseY)) {
                return openBeePage(child.beeData);
            } else {
                return false;
            }
        }

        private boolean openBeePage(CustomBeeData beeData) {
            if (beeData.getName().equals(id)) return false;
            BeepediaScreen.saveScreenState();
            beepedia.setActive(BeepediaScreen.PageType.BEE, beeData.getName());
            return true;
        }

        public class Child {
            Entity entity;
            TranslationTextComponent name;
            double weight;
            double chance;
            CustomBeeData beeData;

            public Child(Pair<String, String> parents, CustomBeeData beeData) {
                entity = ForgeRegistries.ENTITIES.getValue(beeData.getEntityTypeRegistryID()).create(beepedia.getMinecraft().level);
                name = beeData.getTranslation();
                weight = BeeRegistry.getRegistry().getAdjustedWeightForChild(beeData, parents.getLeft(), parents.getRight());
                chance = beeData.getBreedData().getBreedChance();
                this.beeData = beeData;
            }
        }
    }

    private enum BreedingPageType {
        PARENTS,
        CHILDREN,
        MUTATIONS;
    }
}
