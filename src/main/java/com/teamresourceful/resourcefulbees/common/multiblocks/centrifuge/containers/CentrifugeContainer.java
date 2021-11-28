package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.containers.ContainerWithStackMove;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public abstract class CentrifugeContainer<T extends AbstractGUICentrifugeEntity> extends ContainerWithStackMove {

    public static final int INV_X_OFFSET = 144;
    public static final int INV_Y_OFFSET = 124;
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
        if (entity != null && inv.player instanceof ServerPlayerEntity) entity.sendInitGUIPacket((ServerPlayerEntity) inv.player);
    }

    public @Nullable T getEntity() {
        return entity;
    }

    public CentrifugeTier getTier() {
        return tier;
    }

    protected abstract void setupSlots();

    protected void addPlayerInvSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, INV_X_OFFSET + j * 17, INV_Y_OFFSET + i * 17));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, INV_X_OFFSET + k * 17, INV_Y_OFFSET + 55));
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
