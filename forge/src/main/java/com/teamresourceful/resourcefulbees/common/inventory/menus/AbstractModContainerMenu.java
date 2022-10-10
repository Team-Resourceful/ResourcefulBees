package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModContainerMenu<T extends BlockEntity> extends AbstractContainerMenu {

    protected final T entity;
    protected final Inventory inv;
    protected final Player player;
    protected final Level level;

    protected AbstractModContainerMenu(@Nullable MenuType<?> type, int id, Inventory inv, T entity) {
        super(type, id);
        this.entity = entity;
        this.inv = inv;
        this.player = inv.player;
        this.level = player.level;
        if (entity == null) return;
        addMenuSlots();
        addPlayerInvSlots();
    }

    public @NotNull T getEntity() {
        return entity;
    }

    protected abstract int getContainerInputEnd();

    protected abstract int getInventoryStart();

    protected abstract int getContainerInputStart();

    public abstract int getPlayerInvXOffset();

    public abstract int getPlayerInvYOffset();

    protected abstract void addMenuSlots();

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();

            if (index < getInventoryStart()) {
                if (!this.moveItemStackTo(slotItem, getInventoryStart(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, getContainerInputEnd(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    // Centrifuge should be the only one overriding due to slot dimensions of 17 vs 18
    protected void addPlayerInvSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, getPlayerInvXOffset() + j * 18, getPlayerInvYOffset() + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, getPlayerInvXOffset() + k * 18, getPlayerInvYOffset() + 58));
        }
    }

    protected static <T extends BlockEntity> T getTileFromBuf(Level level, FriendlyByteBuf buf, Class<T> type) {
        return buf == null ? null : DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> WorldUtils.getTileEntity(type, level, buf.readBlockPos()));
    }
}
