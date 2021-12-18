package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.menus.AbstractModContainerMenu;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class CentrifugeContainer<T extends AbstractGUICentrifugeEntity> extends AbstractModContainerMenu<T> {

    private static final int INV_X_OFFSET = 144;
    private static final int INV_Y_OFFSET = 124;
    protected final CentrifugeTier tier;

    protected CentrifugeContainer(@Nullable MenuType<?> type, int id, Inventory inv, T entity) {
        super(type, id, inv, entity);
        this.tier = entity.getTier();
        addMenuSlots();
        //if (entity != null && inv.player instanceof ServerPlayer serverPlayer) entity.sendInitGUIPacket(serverPlayer);
    }

    public CentrifugeTier getTier() {
        return tier;
    }

    @Override
    public int getPlayerInvXOffset() {
        return INV_X_OFFSET;
    }

    @Override
    public int getPlayerInvYOffset() {
        return INV_Y_OFFSET;
    }

    @Override
    protected void addPlayerInvSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, getPlayerInvXOffset() + j * 17, getPlayerInvYOffset() + i * 17));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, getPlayerInvXOffset() + k * 17, getPlayerInvYOffset() + 55));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        /*for (ContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            entity.sendGUINetworkPacket(listener);
        }*/
    }
}
