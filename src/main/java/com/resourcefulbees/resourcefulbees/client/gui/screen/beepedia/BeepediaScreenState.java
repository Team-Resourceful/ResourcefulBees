package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia;

import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;

public class BeepediaScreenState {

    private BeepediaScreen.PageType pageType = BeepediaScreen.PageType.BEE;
    private String pageID = null;
    private BeePage.SubPageType beeSubPage = BeePage.SubPageType.INFO;
    private int spawningScroll = 0;
    private int traitsScroll = 0;
    private int breedingPage = 0;
    private boolean biomesOpen = false;
    private boolean parentBreeding = true;

    public BeepediaScreen.PageType getPageType() {
        return pageType;
    }

    public void setPageType(BeepediaScreen.PageType pageType) {
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
}
