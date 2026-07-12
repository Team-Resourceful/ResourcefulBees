package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BlockBee;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.LockBeePacket;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ApiaryMenu extends AbstractModContainerMenu<ApiaryBlockEntity> {

    public ApiaryMenu(int id, Inventory inv, Optional<PositionContent> content) {
        this(id, inv, PositionContent.getOrNull(content, inv.player.level(), ApiaryBlockEntity.class));
    }

    public ApiaryMenu(int id, Inventory inv, ApiaryBlockEntity entity) {
        super(ModMenuTypes.APIARY.get(), id, inv, entity);
    }

    @Override
    protected void addMenuSlots() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new ResourceHandlerSlot(entity.resourceHandler(), entity.resourceHandler()::set, j + i * 9, 56 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean mayPlace(@NonNull ItemStack stack) {
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }

    @Override
    public int getContainerInputEnd() {
        return 27;
    }

    @Override
    public int getInventoryStart() {
        return 27;
    }

    @Override
    public int startIndex() {
        return 27;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 56;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 86;
    }

    public void lockOrUnlockBee(int id) {
        if (id >= 0 && id < entity.beeCount()) {
            NetworkHandler.NETWORK.sendToServer(new LockBeePacket(entity.getBlockPos(), id));
        }
    }

    public BlockBee.Occupant getApiaryBee(int i) {
        return entity.getBees().get(i);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) entity.sendToPlayer(serverPlayer);
    }
}
