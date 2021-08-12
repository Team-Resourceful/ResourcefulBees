package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.widget.SubScreenArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class BeepediaState {


    /***
     *
     *  State Tree
     *
     *  <ListPage>
     *   - <HomePage>
     *   - <HelpPage>
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

    @NotNull
    private BeepediaPage page;
    @Nullable
    private final BeepediaState parent;
    @Nullable
    private BeepediaState child = null;
    @Nullable
    private Map<String, BeepediaPage> subStates;
    @NotNull
    private final SubScreenArea screenArea;
    @Nullable
    private final String defaultState;
    private int scrollHeight = 0;

    public BeepediaState(@Nullable BeepediaState parent, @NotNull BeepediaPage page, @Nullable Map<String, BeepediaPage> subStates, @Nullable String defaultState, @NotNull SubScreenArea screenArea) {
        this.parent = parent;
        this.page = page;
        this.subStates = subStates;
        this.screenArea = screenArea;
        this.defaultState = defaultState;
        initState();
        if (subStates != null && defaultState != null) {
            if (!subStates.containsKey(defaultState)) throw new IllegalStateException("could not find default state in subStates List");
            child = subStates.get(defaultState).createState();
            child.initState();
        }
        postInitState();
        if (child != null) child.postInitState();
    }

    public void switchState(StateEnum newState) {
        if (subStates == null || defaultState == null) return;
        if (!subStates.containsKey(newState)) return;
        child = subStates.get(newState).createState();
        if (child == null) return;
        child.initState();
        child.postInitState();
    }

    public void drawState(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        page.renderBackground(matrix, partialTick, mouseX, mouseY);
        page.renderForeground(matrix, mouseX, mouseY);
        if (child != null) {
            child.drawState(matrix, partialTick, mouseX, mouseY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (child != null && child.screenArea.isHovered(mouseX, mouseY)) return child.mouseClicked(mouseX, mouseY, mouseButton);
        return page.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount){
        if (child != null && child.screenArea.isHovered(mouseX, mouseY)) return child.mouseScrolled(mouseX, mouseY, scrollAmount);
        return page.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    protected abstract void initState();

    protected abstract void postInitState();

    public BeepediaState getParent() {
        return parent;
    }

    public BeepediaState getChild() {
        return child;
    }
}
