package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
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

    Button leftArrow;
    Button rightArrow;
    Button prevTab;
    Button nextTab;

    private final ResourceLocation breedingImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/breeding.png");
    private final ResourceLocation infoIcon = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");

    private final TranslatableComponent parentsTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.parents_title");
    private final TranslatableComponent childrenTitle = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding.children_title");
    private List<BreedingObject> activeList = null;
    private int activePage = 0;

    public BreedingPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        children = BeeRegistry.getRegistry().getChildren(beeData);
        parents = BeeRegistry.getRegistry().getParents(beeData);
        children.forEach((p, l) -> l.getMap().forEach((w, b) -> childrenBreeding.add(new BreedingObject(p, b))));
        parents.forEach((p, b) -> parentBreeding.add(new BreedingObject(p, b)));
        leftArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 28, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
        rightArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 20, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 6, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> toggleActiveList());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 6, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> toggleActiveList());
        leftArrow.visible = false;
        rightArrow.visible = false;
        prevTab.visible = false;
        nextTab.visible = false;
        beepedia.addButton(leftArrow);
        beepedia.addButton(rightArrow);
        beepedia.addButton(prevTab);
        beepedia.addButton(nextTab);

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
        if (activeList == null || activePage >= activeList.size()) activePage = 0;
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
        prevTab.visible = false;
        nextTab.visible = false;
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        showButtons();
        Minecraft.getInstance().textureManager.bind(breedingImage);
        GuiComponent.blit(matrix, xPos, yPos + 22, 0, 0, 128, 64, 128, 64);
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title = BeepediaScreen.currScreenState.isParentBreeding() && !baseOnly() ? parentsTitle : childrenTitle;
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        if (activeList.size() > 1) {
            TextComponent page = new TextComponent(String.format("%d / %d", activePage + 1, activeList.size()));
            padding = font.width(page) / 2;
            font.draw(matrix, page.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
        }
    }

    private boolean shouldShowButtons() {
        return (!parentBreeding.isEmpty() || !childrenBreeding.isEmpty()) && !baseOnly();
    }

    private void showButtons() {
        prevTab.visible = shouldShowButtons();
        nextTab.visible = shouldShowButtons();
        leftArrow.visible = activeList.size() > 1;
        rightArrow.visible = activeList.size() > 1;
    }

    private boolean baseOnly() {
        return (parentBreeding.size() == 1 && childrenBreeding.size() == 1 && parentBreeding.get(0).isBase && childrenBreeding.get(0).isBase);
    }

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        if (activeList == null || activeList.isEmpty()) return;
        activeList.get(activePage).draw(matrix);
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
        if (activeList == null || activeList.isEmpty()) return;
        if (activeList.get(activePage) != null) {
            activeList.get(activePage).tick(ticksActive);
        }
    }

    @Override
    public void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
        if (activeList == null || activeList.isEmpty()) return;
        activeList.get(activePage).drawTooltips(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (activeList == null || activeList.isEmpty()) return false;
        return activeList.get(activePage).mouseClicked(mouseX, mouseY);
    }

    public class BreedingObject {
        Entity parent1Entity;
        Entity parent2Entity;
        CustomBeeData parent1Data;
        CustomBeeData parent2Data;
        List<ItemStack> parent1Items = new LinkedList<>();
        List<ItemStack> parent2Items = new LinkedList<>();
        TranslatableComponent parent1Name;
        TranslatableComponent parent2Name;
        Vec2 parent1Pos;
        Vec2 parent2Pos;
        Vec2 childPos;
        Vec2 chancePos;
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
            parent1Pos = new Vec2((float) xPos + 6, (float) yPos + 22);
            parent2Pos = new Vec2((float) xPos + 60, (float) yPos + 22);
            childPos = new Vec2((float) xPos + 130, (float) yPos + 32);
            chancePos = new Vec2((float) xPos + SUB_PAGE_WIDTH - 17, (float) yPos + 20);
        }

        public BreedingObject(Pair<String, String> parents, CustomBeeData child) {
            initParents(parents);
            this.child = new Child(parents, child);
            isBase = parents.getLeft().equals(parents.getRight()) && parents.getLeft().equals(child.getName());
            isParent = false;
        }

        public void drawParent1(PoseStack matrix) {
            RenderUtils.renderEntity(matrix, parent1Entity, beepedia.getMinecraft().level, parent1Pos.x, parent1Pos.y, 45, 1);
        }

        public void drawParent2(PoseStack matrix) {
            RenderUtils.renderEntity(matrix, parent2Entity, beepedia.getMinecraft().level, parent2Pos.x, parent2Pos.y, -45, 1);
        }

        private void drawChild(PoseStack matrix) {
            Font font = beepedia.getMinecraft().font;
            RenderUtils.renderEntity(matrix, child.entity, beepedia.getMinecraft().level, childPos.x, childPos.y, -45, 1);

            if (child.chance < 1 && !isBase) {
                TextComponent text = new TextComponent(decimalFormat.format(child.chance));
                int padding = font.width(text) / 2;
                Minecraft.getInstance().textureManager.bind(infoIcon);
                beepedia.blit(matrix, (int) chancePos.x, (int) chancePos.y, 16, 0, 9, 9);
                font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos + 140 - (float) padding, (float) yPos + 21, -1);
            }
            TextComponent text = new TextComponent(decimalFormat.format(child.weight));
            int padding = font.width(text) / 2;
            font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos + 103f - (float) padding, (float) yPos + 56, -1);
        }

        public void drawParent1Item(PoseStack matrix) {
            if (parent1Items.isEmpty()) return;
            beepedia.drawSlot(matrix, parent1Items.get(parent1Counter), xPos + 5, yPos + 53);
        }

        public void drawParent2Item(PoseStack matrix) {
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

        public void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
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
                beepedia.renderTooltip(matrixStack, new TranslatableComponent("gui.resourcefulbees.jei.category.breed_chance.info"), mouseX, mouseY);
            }
        }

        private void drawTooltip(PoseStack matrixStack, CustomBeeData beeData, int mouseX, int mouseY) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(beeData.getTranslation());
            tooltip.add(new TextComponent(beeData.getEntityTypeRegistryID().toString()).withStyle(ChatFormatting.DARK_GRAY));
            beepedia.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }

        public void draw(PoseStack matrix) {
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
            TranslatableComponent name;
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
}
