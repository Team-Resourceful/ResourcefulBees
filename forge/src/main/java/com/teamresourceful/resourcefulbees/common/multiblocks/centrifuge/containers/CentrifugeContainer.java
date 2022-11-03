package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.menus.AbstractModContainerMenu;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class CentrifugeContainer<T extends AbstractGUICentrifugeEntity> extends AbstractModContainerMenu<T> {

    private static final int INV_X_OFFSET = 144;
    private static final int INV_Y_OFFSET = 124;
    protected final CentrifugeTier tier;
    protected final CentrifugeState centrifugeState;
    protected boolean displaySlots = true;

    protected CentrifugeContainer(@Nullable MenuType<?> type, int id, Inventory inv, T entity, CentrifugeState centrifugeState) {
        super(type, id, inv, entity);
        this.tier = entity.getTier();
        this.centrifugeState = centrifugeState;
        addCentrifugeSlots();
        addPlayerSlots();
    }

    public CentrifugeState getCentrifugeState() {
        return centrifugeState;
    }

    public CentrifugeTier getTier() {
        return tier;
    }

    public boolean displaySlots() {
        return this.displaySlots;
    }

    //not sure which of these 3 I'll need yet
    public void displaySlots(boolean displaySlots) {
        this.displaySlots = displaySlots;
    }

    public void enableSlots() {
        this.displaySlots = true;
    }

    public void disableSlots() {
        this.displaySlots = false;
    }

    @Override
    public int getPlayerInvXOffset() {
        return INV_X_OFFSET;
    }

    @Override
    public int getPlayerInvYOffset() {
        return INV_Y_OFFSET;
    }

    //TODO come up with a better way to handle below methods - low priority
    protected abstract void addCentrifugeSlots();

    protected final void addMenuSlots() {
        //Need access to Centrifuge Tier
    }

    @Override
    protected final void addPlayerInvSlots() {
        //Need to call after addCentrifugeSlots
    }

    protected void addPlayerSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, getPlayerInvXOffset() + j * 17, getPlayerInvYOffset() + i * 17) {
                    @Override
                    public boolean isActive() {
                        return displaySlots;
                    }
                });
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, getPlayerInvXOffset() + k * 17, getPlayerInvYOffset() + 55) {
                @Override
                public boolean isActive() {
                    return displaySlots;
                }
            });
        }
    }
}
