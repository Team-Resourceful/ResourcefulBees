package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.ItemMutation;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations.EntityMutationPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations.ItemMutationPage;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.MutationTypes;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.utils.CycledArray;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BreedingPage extends BeeDataPage {

    // REQUIRED: Feed Item Tags not rendering properly for non-vanilla tags - however working in JEI - must fix
    private final List<BreedingObject> parents = new LinkedList<>();

    private final List<BreedingObject> children = new LinkedList<>();

    private final List<EntityMutationPage> entityMutationBreeding = new LinkedList<>();
    private final List<ItemMutationPage> itemMutationBreeding = new LinkedList<>();

    final List<BreedingPageType> subPages = new LinkedList<>();

    BreedingPageType activeSubPage;

    private Button leftArrow;
    private Button rightArrow;
    private Button prevTab;
    private Button nextTab;

    private final ResourceLocation breedingImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/breeding.png");
    private final ResourceLocation infoIcon = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    private final TranslatableComponent parentsTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");
    private final TranslatableComponent childrenTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");
    private final TranslatableComponent entityMutationsTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.entity_mutations_title");
    private final TranslatableComponent itemMutationsTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.item_mutations_title");
    private final TranslatableComponent errorTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.error_title");
    private int activePage = 0;

    public BreedingPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, List<EntityMutation> mutations, List<ItemMutation> itemBreedMutation, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        registerData(mutations, itemBreedMutation);
        registerArrows();


        if (!parents.isEmpty()) subPages.add(BreedingPageType.PARENTS);
        if (!children.isEmpty()) subPages.add(BreedingPageType.CHILDREN);
        if (!entityMutationBreeding.isEmpty()) subPages.add(BreedingPageType.ENTITY_MUTATIONS);
        if (!itemMutationBreeding.isEmpty()) subPages.add(BreedingPageType.ITEM_MUTATIONS);

        if (subPages.isEmpty()) return;
        if (BeepediaScreen.currScreenState.getBreedingTab() >= subPages.size())
            BeepediaScreen.currScreenState.setBreedingTab(subPages.size() - 1);

        activeSubPage = subPages.get(BeepediaScreen.currScreenState.getBreedingTab());

        parents.sort((o1, o2) -> {
            if (o1.isSelf) return 1;
            else if(o1.equals(o2)) return 0;
            else return -1;
        });
    }

    private void registerArrows() {
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
    }

    private void registerData(List<EntityMutation> mutations, List<ItemMutation> itemBreedMutation) {
        BeeRegistry.getRegistry().getChildren(beeData).forEach(beeFamily -> children.add(new BreedingObject(beeFamily)));
        beeData.getBreedData().getParents().forEach(beeFamily ->  parents.add(new BreedingObject(beeFamily)));
        children.removeIf(breedingObject -> breedingObject.isSelf);
        mutations.forEach(b -> entityMutationBreeding.add(new EntityMutationPage(b.getParent(), parent, b.getInput(), b.getOutputs(), MutationTypes.ENTITY, b.getMutationCount(), beepedia)));
        itemBreedMutation.forEach(b -> itemMutationBreeding.add(new ItemMutationPage(b.getParent(), parent, b.getInputs(), b.getOutputs(), MutationTypes.ITEM, b.getMutationCount(), beepedia)));
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
                if (activePage < 0) activePage = parents.size() - 1;
                else if (activePage >= parents.size()) activePage = 0;
                activeSubPage = BreedingPageType.PARENTS;
                break;
            case CHILDREN:
                if (activePage < 0) activePage = children.size() - 1;
                else if (activePage >= children.size()) activePage = 0;
                activeSubPage = BreedingPageType.CHILDREN;
                break;
            case ITEM_MUTATIONS:
                if (activePage < 0) activePage = itemMutationBreeding.size() - 1;
                else if (activePage >= itemMutationBreeding.size()) activePage = 0;
                activeSubPage = BreedingPageType.ITEM_MUTATIONS;
                break;
            case ENTITY_MUTATIONS:
                if (activePage < 0) activePage = entityMutationBreeding.size() - 1;
                else if (activePage >= entityMutationBreeding.size()) activePage = 0;
                activeSubPage = BreedingPageType.ENTITY_MUTATIONS;
                break;
            default:
                activeSubPage = BreedingPageType.ERROR;
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
        if (subPages.isEmpty()) return;
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
            case ENTITY_MUTATIONS:
                return entityMutationBreeding.size();
            case ITEM_MUTATIONS:
                return itemMutationBreeding.size();
            case CHILDREN:
                return children.size();
            case PARENTS:
                return parents.size();
            default:
                return 0;
        }
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        showButtons();
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title;

        switch (activeSubPage) {
            case CHILDREN:
                title = childrenTitle;
                break;
            case ITEM_MUTATIONS:
                title = itemMutationsTitle;
                break;
            case ENTITY_MUTATIONS:
                title = entityMutationsTitle;
                break;
            case PARENTS:
                title = parentsTitle;
                break;
            default:
                title = errorTitle;
                // show error page
                break;
        }
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        if (getCurrentListSize() > 1) {
            TextComponent page = new TextComponent(String.format("%d / %d", activePage + 1, getCurrentListSize()));
            padding = font.width(page) / 2;
            font.draw(matrix, page.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
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

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        switch (activeSubPage) {
            case CHILDREN:
                children.get(activePage).draw(matrix);
                break;
            case ENTITY_MUTATIONS:
                entityMutationBreeding.get(activePage).draw(matrix, xPos, yPos + 22);
                break;
            case ITEM_MUTATIONS:
                itemMutationBreeding.get(activePage).draw(matrix, xPos, yPos + 22);
                break;
            case PARENTS:
                parents.get(activePage).draw(matrix);
                break;
            default:
                // do nothing this should never happen
                break;
        }
    }

    @Override
    public void addSearch() {
        for (BreedingObject breedingObject : children) {
            addBreedSearch(breedingObject);
        }
        for (BreedingObject breedingObject : parents) {
            addBreedSearch(breedingObject);
        }
        itemMutationBreeding.forEach(i -> i.addSearch(parent));
        entityMutationBreeding.forEach(e -> e.addSearch(parent));
    }

    public void addBreedSearch(BreedingObject breedingObject) {
        parent.addSearchBee(breedingObject.childEntity, ((CustomBeeEntity) breedingObject.childEntity).getBeeType());
        parent.addSearchBee(breedingObject.parent1Entity, ((CustomBeeEntity) breedingObject.parent1Entity).getBeeType());
        parent.addSearchBee(breedingObject.parent2Entity, ((CustomBeeEntity) breedingObject.parent2Entity).getBeeType());
    }

    @Override
    public void tick(int ticksActive) {
        switch (activeSubPage) {
            case CHILDREN:
                children.get(activePage).tick(ticksActive);
                break;
            case ENTITY_MUTATIONS:
                entityMutationBreeding.get(activePage).tick(ticksActive);
                break;
            case ITEM_MUTATIONS:
                itemMutationBreeding.get(activePage).tick(ticksActive);
                break;
            case PARENTS:
                parents.get(activePage).tick(ticksActive);
                break;
            default:
                // do nothing this should never happen
                break;
        }
    }

    @Override
    public void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
        switch (activeSubPage) {
            case CHILDREN:
                children.get(activePage).drawTooltips(matrixStack, mouseX, mouseY);
                break;
            case ENTITY_MUTATIONS:
                entityMutationBreeding.get(activePage).drawTooltips(matrixStack, xPos, yPos + 31, mouseX, mouseY);
                break;
            case ITEM_MUTATIONS:
                itemMutationBreeding.get(activePage).drawTooltips(matrixStack, xPos, yPos + 31, mouseX, mouseY);
                break;
            case PARENTS:
                parents.get(activePage).drawTooltips(matrixStack, mouseX, mouseY);
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
                return children.get(activePage).mouseClicked(mouseX, mouseY);
            case ENTITY_MUTATIONS:
                return entityMutationBreeding.get(activePage).mouseClick(xPos, yPos + 22, (int) mouseX, (int) mouseY);
            case ITEM_MUTATIONS:
                return itemMutationBreeding.get(activePage).mouseClick(xPos, yPos + 22, (int) mouseX, (int) mouseY);
            case PARENTS:
                return parents.get(activePage).mouseClicked(mouseX, mouseY);
            default:
                return false;
        }
    }

    public class BreedingObject {
        private final Entity parent1Entity;
        private final Entity parent2Entity;
        private final CycledArray<ItemStack> parent1Items;
        private final CycledArray<ItemStack> parent2Items;
        private final Vec2 parent1Pos;
        private final Vec2 parent2Pos;
        private final Vec2 childPos;
        private final Vec2 chancePos;
        private final DecimalFormat decimalFormat = new DecimalFormat("##%");
        public final boolean isSelf;
        private final Entity childEntity;
        private final BeeFamily beeFamily;
        private final double adjustedWeight;

        public BreedingObject(BeeFamily beeFamily) {
            this.beeFamily = beeFamily;
            parent1Entity = beeFamily.createParent1DisplayEntity(beepedia.getMinecraft().level);
            parent2Entity = beeFamily.createParent2DisplayEntity(beepedia.getMinecraft().level);
            childEntity = beeFamily.createChildDisplayEntity(beepedia.getMinecraft().level);
            parent1Items = new CycledArray<>(beeFamily.getParent1FeedItemStacks());
            parent2Items = new CycledArray<>(beeFamily.getParent2FeedItemStacks());
            parent1Pos = new Vec2((float) xPos + 6, (float) yPos + 34);
            parent2Pos = new Vec2((float) xPos + 60, (float) yPos + 34);
            childPos = new Vec2((float) xPos + 130, (float) yPos + 44);
            chancePos = new Vec2((float) xPos + SUB_PAGE_WIDTH - 17, (float) yPos + 32);
            adjustedWeight = BeeRegistry.getRegistry().getAdjustedWeightForChild(beeFamily);
            isSelf = beeFamily.getParent1().equals(beeFamily.getChild()) && beeFamily.getParent2().equals(beeFamily.getChild());
        }

        public void drawParent1(PoseStack matrix) {
            RenderUtils.renderEntity(matrix, parent1Entity, beepedia.getMinecraft().level, parent1Pos.x, parent1Pos.y, 45, 1);
        }

        public void drawParent2(PoseStack matrix) {
            RenderUtils.renderEntity(matrix, parent2Entity, beepedia.getMinecraft().level, parent2Pos.x, parent2Pos.y, -45, 1);
        }

        private void drawChild(PoseStack matrix) {
            Font font = beepedia.getMinecraft().font;
            RenderUtils.renderEntity(matrix, childEntity, beepedia.getMinecraft().level, childPos.x, childPos.y, -45, 1);

            if (beeFamily.getChance() < 1 && !isSelf) {
                TextComponent text = new TextComponent(decimalFormat.format(beeFamily.getChance()));
                int padding = font.width(text) / 2;
                Minecraft.getInstance().textureManager.bind(infoIcon);
                beepedia.blit(matrix, (int) chancePos.x, (int) chancePos.y, 16, 0, 9, 9);
                font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos + 140 - (float) padding, (float) yPos + 33, -1);
            }
            TextComponent text = new TextComponent(decimalFormat.format(adjustedWeight));
            int padding = font.width(text) / 2;
            font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos + 103f - (float) padding, (float) yPos + 68, -1);
        }

        public void drawParent1Item(PoseStack matrix) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent1Items.get(), xPos + 5, yPos + 65);
        }

        public void drawParent2Item(PoseStack matrix) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent2Items.get(), xPos + 59, yPos + 65);
        }

        public void tick(int ticksActive) {
            if (ticksActive % 20 == 0 && !Screen.hasShiftDown()) {
                if (!parent1Items.isEmpty()) parent1Items.cycle();
                if (!parent2Items.isEmpty()) parent2Items.cycle();
            }
        }

        public void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
            if (BeepediaScreen.mouseHovering(parent1Pos.x, parent1Pos.y, 20, 20, mouseX, mouseY)) {
                drawTooltip(matrixStack, beeFamily.getParent1Data(), mouseX, mouseY);
            }
            if (BeepediaScreen.mouseHovering(parent2Pos.x, parent2Pos.y, 20, 20, mouseX, mouseY)) {
                drawTooltip(matrixStack, beeFamily.getParent2Data(), mouseX, mouseY);
            }
            if (BeepediaScreen.mouseHovering(childPos.x, childPos.y, 20, 20, mouseX, mouseY)) {
                drawTooltip(matrixStack, beeFamily.getChildData(), mouseX, mouseY);
            }
            if (BeepediaScreen.mouseHovering(chancePos.x, chancePos.y, 9, 9, mouseX, mouseY) && beeFamily.getChance() < 1) {
                beepedia.renderTooltip(matrixStack, new TranslatableComponent("gui.resourcefulbees.jei.category.breed_chance.info"), mouseX, mouseY);
            }
        }

        private void drawTooltip(PoseStack matrixStack, CustomBeeData beeData, int mouseX, int mouseY) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(beeData.getDisplayName());
            tooltip.add(new TextComponent(beeData.getRegistryID().toString()).withStyle(ChatFormatting.DARK_GRAY));
            beepedia.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }

        public void draw(PoseStack matrix) {
            Minecraft.getInstance().textureManager.bind(breedingImage);
            GuiComponent.blit(matrix, xPos, yPos + 34, 0, 0, 128, 64, 128, 64);
            drawParent1(matrix);
            drawParent2(matrix);
            drawChild(matrix);
            drawParent1Item(matrix);
            drawParent2Item(matrix);
        }

        public boolean mouseClicked(double mouseX, double mouseY) {
            if (BeepediaScreen.mouseHovering(parent1Pos.x, parent1Pos.y, 20, 20, (int) mouseX, (int) mouseY)) {
                return openBeePage(beeFamily.getParent1Data());
            } else if (BeepediaScreen.mouseHovering(parent2Pos.x, parent2Pos.y, 20, 20, (int) mouseX, (int) mouseY)) {
                return openBeePage(beeFamily.getParent2Data());
            } else if (BeepediaScreen.mouseHovering(childPos.x, childPos.y, 20, 20, (int) mouseX, (int) mouseY)) {
                return openBeePage(beeFamily.getChildData());
            } else {
                return false;
            }
        }

        private boolean openBeePage(CustomBeeData beeData) {
            if (beeData.getCoreData().getName().equals(id)) return false;
            BeepediaScreen.saveScreenState();
            beepedia.setActive(BeepediaScreen.PageType.BEE, beeData.getCoreData().getName());
            return true;
        }
    }

    private enum BreedingPageType {
        PARENTS,
        CHILDREN,
        ENTITY_MUTATIONS,
        ITEM_MUTATIONS,
        ERROR
    }
}
