package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations.MutationsPage;
import com.teamresourceful.resourcefulbees.client.gui.widget.ScreenArea;
import com.teamresourceful.resourcefulbees.common.lib.enums.MutationType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeeMutationListPage extends BeeDataPage {

    final List<Pair<MutationType, List<MutationsPage>>> mutations = new ArrayList<>();

    private List<MutationsPage> activeList = null;
    int tab;
    private int page;
    private MutationsPage activePage = null;

//    final Button prevTab;
//    final Button nextTab;
//    final Button leftArrow;
//    final Button rightArrow;

    public BeeMutationListPage(ScreenArea screenArea) {
        super(screenArea);
    }


//    public BeeMutationListPage() {
//        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 6, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevTab());
//        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 6, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextTab());
//        leftArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 28, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> prevPage());
//        rightArrow = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 20, yPos + SUB_PAGE_HEIGHT - 16, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> nextPage());
//        beepedia.addButton(prevTab);
//        beepedia.addButton(nextTab);
//        beepedia.addButton(leftArrow);
//        beepedia.addButton(rightArrow);
//        prevTab.visible = false;
//        nextTab.visible = false;
//        leftArrow.visible = false;
//        rightArrow.visible = false;
//        List<MutationsPage> blockMutations = new ArrayList<>();
//        List<MutationsPage> itemMutations = new ArrayList<>();
//        List<MutationsPage> entityMutations = new ArrayList<>();
//        if (!beeData.getMutationData().getBlockMutations().isEmpty()) {
//            beeData.getMutationData().getBlockMutations().forEach((block, collection) ->  blockMutations.add(new BlockMutationPage(parent.getBee(), parent, block, collection, MutationType.BLOCK, beeData.getMutationData().getMutationCount(), beepedia)));
//        }
//        if (!beeData.getMutationData().getItemMutations().isEmpty()) {
//            beeData.getMutationData().getItemMutations().forEach((b, m) -> itemMutations.add(new ItemMutationPage(parent.getBee(), parent, b, m, MutationType.ITEM, beeData.getMutationData().getMutationCount(), beepedia)));
//        }
//        if (!beeData.getMutationData().getEntityMutations().isEmpty()) {
//            beeData.getMutationData().getEntityMutations().forEach((b, m) -> entityMutations.add(new EntityMutationPage(parent.getBee(), parent, b, m, MutationType.ENTITY, beeData.getMutationData().getMutationCount(), beepedia)));
//        }
//        if (!blockMutations.isEmpty()) {
//            mutations.add(Pair.of(MutationType.BLOCK, blockMutations));
//        }
//        if (!itemMutations.isEmpty()) {
//            mutations.add(Pair.of(MutationType.ITEM, itemMutations));
//        }
//        if (!entityMutations.isEmpty()) {
//            mutations.add(Pair.of(MutationType.ENTITY, entityMutations));
//        }
//    }

    private void nextPage() {
//        if (activeList == null) return;
//        page++;
//        if (page >= activeList.size()) page = 0;
//        activePage = activeList.get(page);
//        BeepediaScreen.currScreenState.setMutationsPage(page);
    }

    private void prevPage() {
//        if (activeList == null) return;
//        page--;
//        if (page < 0) page = activeList.size() - 1;
//        activePage = activeList.get(page);
//        BeepediaScreen.currScreenState.setMutationsPage(page);
    }

    private void nextTab() {
//        tab++;
//        if (tab >= mutations.size()) tab = 0;
//        BeepediaScreen.currScreenState.setCurrentMutationTab(tab);
//        activeList = mutations.get(tab).getRight();
//        selectPage(activeList);
    }

    private void prevTab() {
//        tab--;
//        if (tab < 0) tab = mutations.size() - 1;
//        BeepediaScreen.currScreenState.setCurrentMutationTab(tab);
//        activeList = mutations.get(tab).getRight();
//        selectPage(activeList);
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
//        FontRenderer font = Minecraft.getInstance().font;
//        TranslationTextComponent title;
//        switch (mutations.get(BeepediaScreen.currScreenState.getCurrentMutationTab()).getLeft()) {
//            case BLOCK:
//                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.block");
//                break;
//            case ENTITY:
//                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.entity");
//                break;
//            case ITEM:
//                title = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.item");
//                break;
//            default:
//                throw new UnsupportedOperationException(String.format("found a legacy mutation. %s", BeepediaScreen.currScreenState.getCurrentMutationTab()));
//        }
//
//        if (activePage != null) {
//            int padding = font.width(title) / 2;
//            font.draw(matrix, title.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
//            activePage.draw(matrix, xPos, yPos + 31);
//            if (activeList.size() > 1) {
//                StringTextComponent pageInfo = new StringTextComponent(String.format("%d / %d", this.page + 1, activeList.size()));
//                padding = font.width(pageInfo) / 2;
//                font.draw(matrix, pageInfo.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + SUB_PAGE_HEIGHT - 14, -1);
//            }
//        }
    }

    public void addSearch() {
//        String search = "";
//        for (Pair<MutationType, List<MutationsPage>> mutation : mutations) {
//            search = String.format("%s %s", search, mutation.getLeft());
//            for (MutationsPage mutationsPage : mutation.getRight()) {
//                mutationsPage.addSearch();
//            }
//        }
    }

    @Override
    public void registerScreen(BeepediaScreen beepedia) {

    }

    private void selectPage(List<MutationsPage> activeList) {
//        if (activeList != null) {
//            page = BeepediaScreen.currScreenState.getMutationsPage();
//            if (page >= activeList.size()) page = 0;
//            if (activeList.isEmpty()) activePage = null;
//            else activePage = activeList.get(page);
//            BeepediaScreen.currScreenState.setMutationsPage(page);
//            leftArrow.visible = activeList.size() > 1;
//            rightArrow.visible = activeList.size() > 1;
//        }
    }

    @Override
    public void tick(int ticksActive) {
        if (activePage != null) activePage.tick(ticksActive);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
//        if (activePage != null) return activePage.mouseClick(xPos, yPos + 22, (int) mouseX, (int) mouseY);
        return false;
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
