package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public class BeepediaContainer extends Container{

    public int beesScroll;
    public int honeyScroll;
    public int traitScroll;
    private BeepediaScreen.PageType pageType;
    private String pageID;
    private BeepediaScreen.PageType beeSubPageType;

    private ItemStack itemStack;

    public BeepediaContainer(int id, PlayerInventory inventory) {
        super(ModContainers.BEEPEDIA_CONTAINER.get(), id);
        itemStack = inventory.getCurrentItem();
    }

    @Override
    public boolean canInteractWith(PlayerEntity p_75145_1_) {
        return true;
    }

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

    public BeepediaScreen.PageType getBeeSubPageType() {
        return beeSubPageType;
    }

    public void setBeeSubPageType(BeepediaScreen.PageType beeSubPageType) {
        this.beeSubPageType = beeSubPageType;
    }
}
