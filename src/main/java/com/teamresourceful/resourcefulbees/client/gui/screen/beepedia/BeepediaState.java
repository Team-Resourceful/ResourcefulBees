package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.BeepediaListTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.PageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.SubPageTab;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.SubPageTypes;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class BeepediaState {
    /***
     *
     *  State Tree
     *
     *  BEES/INFO/
     *  <ListPage>
     *   - <HomePage>
     *       - <INFO>
     *   - <HelpPage>
     *       - <INFO>
     *       - <SearchHelpPage>
     *       - <BreedingHelpPage>
     *       - <MutationHelpPage>
     *   - <BeePage>
     *       - <BeeInfoPage>
     *       - <BeeCombatPage>
     *       - <BeeMutationListPage>
     *           - <BlockMutationPage>
     *           - <EntityMutationPage>
     *           - <ItemMutationPage>
     *       - <BeeTraitListPage>
     *       - <BeeCombPage>
     *           - <BeeCombDropPage>
     *           - <BeeCentrifugePage>
     *       - <BeeSpawningPage>
     *           - <BeeSpawnDataPage>
     *           - <BeeBiomeListPage>
     *       - <BeeBreedingPage>
     *           - <BeeParentBreedPage>
     *           - <BeeChildBreedPage>
     *           - <EntityMutationPage>
     *           - <ItemMutationPage>
     *           - <BeeExtraBreedPage>
     *   - <TraitPage>
     *       - <TraitInfoPage>
     *       - <TraitBeesPage>
     *   - <HoneyPage>
     *       - <HoneyInfoPage>
     *       - <HoneyBeesPage>
     *   - <CombPage>
     *       - <CombInfoPage>
     *       - <CombBeesPage>
     */
    public static BeepediaState currentState = new BeepediaState();
    public static Stack<BeepediaState> savedStates = new Stack<>();

    public BeepediaListTypes selectedList;
    public PageTypes page;
    public String listItem;
    public SubPageTypes subPage;
    public SubPageTab subPageTab;

    public BeepediaState(BeepediaListTypes selectedList, PageTypes page, String listItem, SubPageTypes subPage, SubPageTab subPageTab) {
        this.selectedList = selectedList == null ? BeepediaListTypes.BEES : selectedList;
        this.page = page == null ? PageTypes.HOME : page;
        this.listItem = listItem;
        this.subPage = subPage == null ? SubPageTypes.INFO : subPage;
        this.subPageTab = subPageTab == null ? SubPageTab.NONE : subPageTab;
    }

    public BeepediaState() {
        this(null, null, null, null, null);
    }

    public BeepediaState(BeepediaState currentState) {
        this(currentState.selectedList, currentState.page, currentState.listItem, currentState.subPage, currentState.subPageTab);
    }

    public static void goBackState() {
        if (savedStates.isEmpty()) return;
        BeepediaHandler.closeState();
        currentState = savedStates.pop();
        BeepediaHandler.openState();
    }

    public static boolean isHomeState() {
        return currentState.page == PageTypes.HOME;
    }

    public static boolean hasPastStates() {
        return !savedStates.isEmpty();
    }

    public void newState(@Nullable BeepediaListTypes selectedList, @Nullable PageTypes page, @Nullable String listItem, @Nullable SubPageTypes subPage, @Nullable SubPageTab subPageTab) {
        savedStates.add(new BeepediaState(currentState));
        updateState(selectedList, page, listItem, subPage, subPageTab);
    }

    public void updateState(@Nullable BeepediaListTypes selectedList, @Nullable PageTypes page, @Nullable String listItem, @Nullable SubPageTypes subPage, @Nullable SubPageTab subPageTab) {
        BeepediaHandler.closeState();
        if (selectedList != null) this.selectedList = selectedList;
        if (page != null) this.page = page;
        this.listItem = listItem;
        if (subPage != null) this.subPage = subPage;
        if (subPageTab != null) this.subPageTab = subPageTab;
        BeepediaHandler.openState();
    }
}
