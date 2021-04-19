package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.BlockMutationPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.EntityMutationPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.ItemMutationPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations.MutationsPage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class MutationListPage extends BeeDataPage {

    List<Pair<MutationTypes, List<MutationsPage>>> mutations = new ArrayList<>();

    private List<MutationsPage> activeList = null;
    int tab;
    private int page;
    private MutationsPage activePage = null;

    Button prevTab;
    Button nextTab;
    Button leftArrow;
    Button rightArrow;

    public MutationListPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 6, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevTab());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 6, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextTab());
        leftArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 28, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
        rightArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 20, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
        beepedia.addButton(prevTab);
        beepedia.addButton(nextTab);
        beepedia.addButton(leftArrow);
        beepedia.addButton(rightArrow);
        prevTab.visible = false;
        nextTab.visible = false;
        leftArrow.visible = false;
        rightArrow.visible = false;
        List<MutationsPage> blockMutations = new ArrayList<>();
        List<MutationsPage> itemMutations = new ArrayList<>();
        List<MutationsPage> entityMutations = new ArrayList<>();
        if (beeData.getMutationData().hasBlockMutations()) {
            beeData.getMutationData().getJeiBlockMutations().forEach((b, m) -> blockMutations.add(new BlockMutationPage(parent.getBee(), b, m, MutationTypes.BLOCK, beeData.getMutationData().getMutationCount(), beepedia)));
            beeData.getMutationData().getJeiBlockTagMutations().forEach((b, m) -> blockMutations.add(new BlockMutationPage(parent.getBee(), b, m, MutationTypes.BLOCK, beeData.getMutationData().getMutationCount(), beepedia)));
        }
        if (beeData.getMutationData().hasItemMutations()) {
            beeData.getMutationData().getJeiItemMutations().forEach((b, m) -> itemMutations.add(new ItemMutationPage(parent.getBee(), b, m, MutationTypes.ITEM, beeData.getMutationData().getMutationCount(), beepedia)));
            beeData.getMutationData().getJeiBlockTagItemMutations().forEach((b, m) -> itemMutations.add(new ItemMutationPage(parent.getBee(), b, m, MutationTypes.ITEM, beeData.getMutationData().getMutationCount(), beepedia)));
        }
        if (beeData.getMutationData().hasEntityMutations()) {
            beeData.getMutationData().getEntityMutations().forEach((b, m) -> entityMutations.add(new EntityMutationPage(parent.getBee(), b, m, MutationTypes.ENTITY, beeData.getMutationData().getMutationCount(), beepedia)));
        }
        if (!blockMutations.isEmpty()) {
            mutations.add(Pair.of(MutationTypes.BLOCK, blockMutations));
        }
        if (!itemMutations.isEmpty()) {
            mutations.add(Pair.of(MutationTypes.ITEM, itemMutations));
        }
        if (!entityMutations.isEmpty()) {
            mutations.add(Pair.of(MutationTypes.ENTITY, entityMutations));
        }
    }

    private void nextPage() {
        if (activeList == null) return;
        page++;
        if (page >= activeList.size()) page = 0;
        activePage = activeList.get(page);
        BeepediaScreen.currScreenState.setMutationsPage(page);
    }

    private void prevPage() {
        if (activeList == null) return;
        page--;
        if (page < 0) page = activeList.size() - 1;
        activePage = activeList.get(page);
        BeepediaScreen.currScreenState.setMutationsPage(page);
    }

    private void nextTab() {
        tab++;
        if (tab >= mutations.size()) tab = 0;
        BeepediaScreen.currScreenState.setCurrentMutationTab(tab);
        activeList = mutations.get(tab).getRight();
        selectPage(activeList);
    }

    private void prevTab() {
        tab--;
        if (tab < 0) tab = mutations.size() - 1;
        BeepediaScreen.currScreenState.setCurrentMutationTab(tab);
        activeList = mutations.get(tab).getRight();
        selectPage(activeList);
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title;
        switch (mutations.get(BeepediaScreen.currScreenState.getCurrentMutationTab()).getLeft()) {
            case BLOCK:
                title = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.block");
                break;
            case ENTITY:
                title = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.entity");
                break;
            case ITEM:
                title = new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.item");
                break;
            default:
                throw new UnsupportedOperationException(String.format("found a legacy mutation. %s", BeepediaScreen.currScreenState.getCurrentMutationTab()));
        }

        if (activePage != null) {
            int padding = font.width(title) / 2;
            font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
            activePage.draw(matrix, xPos, yPos + 22);
            if (activeList.size() > 1) {
                TextComponent pageInfo = new TextComponent(String.format("%d / %d", this.page + 1, activeList.size()));
                padding = font.width(pageInfo) / 2;
                font.draw(matrix, pageInfo.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
            }
        }
    }

    @Override
    public String getSearch() {
        String search = "";
        for (Pair<MutationTypes, List<MutationsPage>> mutation : mutations) {
            search = String.format("%s %s", search, mutation.getLeft());
            for (MutationsPage mutationsPage : mutation.getRight()) {
                search = String.format("%s %s", search, mutationsPage.getSearch());
            }
        }
        return search;
    }

    @Override
    public void openPage() {
        super.openPage();
        // get current Tab
        tab = BeepediaScreen.currScreenState.getCurrentMutationTab();
        if (tab >= mutations.size()) tab = 0;
        BeepediaScreen.currScreenState.setCurrentMutationTab(tab);
        activeList = mutations.get(tab).getRight();

        // get current page
        selectPage(activeList);
        prevTab.visible = mutations.size() > 1;
        nextTab.visible = mutations.size() > 1;
    }

    private void selectPage(List<MutationsPage> activeList) {
        if (activeList != null) {
            page = BeepediaScreen.currScreenState.getMutationsPage();
            if (page >= activeList.size()) page = 0;
            if (activeList.isEmpty()) activePage = null;
            else activePage = activeList.get(page);
            BeepediaScreen.currScreenState.setMutationsPage(page);
            leftArrow.visible = activeList.size() > 1;
            rightArrow.visible = activeList.size() > 1;
        }
    }

    @Override
    public void closePage() {
        super.closePage();
        prevTab.visible = false;
        nextTab.visible = false;
        leftArrow.visible = false;
        rightArrow.visible = false;
    }

    @Override
    public void tick(int ticksActive) {
        if (activePage != null) activePage.tick(ticksActive);
    }

    @Override
    public void drawTooltips(PoseStack matrix, int mouseX, int mouseY) {
        if (activePage != null) activePage.drawTooltips(matrix, xPos, yPos + 22, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (activePage != null) return activePage.mouseClick(xPos, yPos + 22, (int) mouseX, (int) mouseY);
        return false;
    }
}
