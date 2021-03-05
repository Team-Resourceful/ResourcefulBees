package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;

public class BeepediaScreenState {

    private BeepediaScreen.PageType pageType = BeepediaScreen.PageType.BEE;
    private BeepediaScreen.PageType lastType = null;
    private String pageID = null;
    private BeePage.SubPageType beeSubPage = BeePage.SubPageType.INFO;
    private int spawningScroll = 0;
    private int traitsScroll = 0;
    private int breedingPage = 0;
    private boolean biomesOpen = false;
    private boolean parentBreeding = true;
    private boolean centrifugeOpen = false;
    private int centrifugePage = 0;
    private int currentMutationType = 0;
    private int mutationsPage = 0;
    private boolean honeyEffectsActive = true;
    private boolean traitsEffectsActive = true;
    private int honeyBeeListPos = 0;
    private int honeyEffectsListPos = 0;

    public BeepediaScreen.PageType getPageType() {
        return pageType;
    }

    public void setPageType(BeepediaScreen.PageType pageType) {
        this.lastType = this.pageType;
        this.pageType = pageType;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public BeePage.SubPageType getBeeSubPage() {
        return beeSubPage;
    }

    public void setBeeSubPage(BeePage.SubPageType beeSubPage) {
        this.beeSubPage = beeSubPage;
    }

    public boolean isBiomesOpen() {
        return biomesOpen;
    }

    public void setBiomesOpen(boolean b) {
        this.biomesOpen = b;
    }

    public int getSpawningScroll() {
        return spawningScroll;
    }

    public void setSpawningScroll(int spawningScroll) {
        this.spawningScroll = spawningScroll;
    }

    public int getTraitsScroll() {
        return traitsScroll;
    }

    public void setTraitsScroll(int traitsScroll) {
        this.traitsScroll = traitsScroll;
    }

    public boolean isParentBreeding() {
        return parentBreeding;
    }

    public void setParentBreeding(boolean parentBreeding) {
        this.parentBreeding = parentBreeding;
    }

    public int getBreedingPage() {
        return breedingPage;
    }

    public void setBreedingPage(int breedingPage) {
        this.breedingPage = breedingPage;
    }

    public BeepediaScreen.PageType getLastType() {
        return lastType;
    }

    public boolean pageChanged() {
        return lastType == null || !lastType.equals(pageType);
    }

    public void setCentrifugeOpen(boolean b) {
        centrifugeOpen = b;
    }

    public boolean isCentrifugeOpen() {
        return centrifugeOpen;
    }

    public void setCentrifugePage(int activePage) {
        centrifugePage = activePage;
    }

    public int getCentrifugePage() {
        return centrifugePage;
    }

    public void setMutationsPage(int page) {
        this.mutationsPage = page;
    }

    public int getCurrentMutationTab() {
        return currentMutationType;
    }

    public void setCurrentMutationTab(int currentMutationType) {
        this.currentMutationType = currentMutationType;
    }

    public int getMutationsPage() {
        return mutationsPage;
    }

    public boolean isHoneyEffectsActive() {
        return honeyEffectsActive;
    }

    public void setHoneyEffectsActive(boolean honeyEffectsActive) {
        this.honeyEffectsActive = honeyEffectsActive;
    }

    public boolean isTraitsEffectsActive() {
        return traitsEffectsActive;
    }

    public void setTraitsEffectsActive(boolean traitsEffectsActive) {
        this.traitsEffectsActive = traitsEffectsActive;
    }

    public void setHoneyBeeListPos(int scrollPos) {
        this.honeyBeeListPos = scrollPos;
    }

    public int getHoneyBeeListPos() {
        return honeyBeeListPos;
    }

    public void setHoneyEffectsListPos(int scrollPos) {
        this.honeyEffectsListPos = scrollPos;
    }

    public int getHoneyEffectsListPos() {
        return honeyEffectsListPos;
    }
}
