package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.containers.ContainerWithStackMove;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public abstract class CentrifugeContainer <T extends AbstractTieredCentrifugeEntity> extends ContainerWithStackMove {

    protected final @Nullable T entity;
    protected final PlayerInventory inv;
    protected final World level;
    protected final CentrifugeTier tier;

    protected CentrifugeContainer(@Nullable ContainerType<?> type, int id, PlayerInventory inv, @Nullable T entity) {
        super(type, id);
        this.inv = inv;
        this.level = inv.player.level;
        this.entity = entity;
        this.tier = entity == null ? CentrifugeTier.ERROR : entity.getTier();
        setupSlots();
    }

    public CentrifugeTier getTier() {
        return tier;
    }

    protected abstract void setupSlots();

    protected abstract int getInvOffsetX();

    protected abstract int getInvOffsetY();

    protected void addPlayerInvSlots() {
        int xOffset = getInvOffsetX();
        int yOffset = getInvOffsetY();


        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
            }
        }

        yOffset += 58;

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, xOffset + k * 18, yOffset));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (entity == null) {
            return;
        }

        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            entity.sendGUINetworkPacket(listener);
        }
    }

    protected static <T extends TileEntity> T getTileFromBuf(PacketBuffer buf, Class<T> type) {
        return buf == null ? null : DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> WorldUtils.getTileEntity(type, Minecraft.getInstance().level, buf.readBlockPos()));
    }
}
