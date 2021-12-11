package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.inventory.containers.ContainerWithStackMove;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public abstract class CentrifugeContainer<T extends AbstractGUICentrifugeEntity> extends ContainerWithStackMove {

    public static final int INV_X_OFFSET = 144;
    public static final int INV_Y_OFFSET = 124;
    protected final @Nullable T entity;
    protected final Inventory inv;
    protected final Level level;
    protected final CentrifugeTier tier;

    protected CentrifugeContainer(@Nullable MenuType<?> type, int id, Inventory inv, @Nullable T entity) {
        super(type, id);
        this.inv = inv;
        this.level = inv.player.level;
        this.entity = entity;
        this.tier = entity == null ? CentrifugeTier.ERROR : entity.getTier();
        setupSlots();
        if (entity != null && inv.player instanceof ServerPlayer serverPlayer) entity.sendInitGUIPacket(serverPlayer);
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

        for (ContainerListener listener : ((ContainerAccessor) this).getListeners()) {
            entity.sendGUINetworkPacket(listener);
        }
    }

    protected static <T extends BlockEntity> T getTileFromBuf(FriendlyByteBuf buf, Class<T> type) {
        return buf == null ? null : DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> WorldUtils.getTileEntity(type, Minecraft.getInstance().level, buf.readBlockPos()));
    }
}
