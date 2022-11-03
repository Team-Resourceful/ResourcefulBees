package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeTerminal;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.jetbrains.annotations.NotNull;

public class CentrifugeTerminalContainer extends CentrifugeContainer<CentrifugeTerminalEntity> {

    public CentrifugeTerminalContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, getTileFromBuf(inv.player.level, buffer, CentrifugeTerminalEntity.class), new CentrifugeState().deserializeBytes(buffer));
    }

    public CentrifugeTerminalContainer(int id, Inventory inv, CentrifugeTerminalEntity entity, CentrifugeState state) {
        super(ModMenus.CENTRIFUGE_TERMINAL_CONTAINER.get(), id, inv, entity, state);
        this.disableSlots();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return entity != null && ContainerLevelAccess.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeTerminal && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public int getContainerInputEnd() {
        return 0;
    }

    @Override
    public int getInventoryStart() {
        return 0;
    }

    @Override
    protected void addCentrifugeSlots() {
        //TERMINAL HAS NO SLOTS
    }

    @Override
    protected void addPlayerSlots() {
        //TERMINAL HAS NO SLOTS
    }
}
