package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.CentrifugeBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class CentrifugeMenu extends AbstractModContainerMenu<CentrifugeBlockEntity> {

    public CentrifugeMenu(int id, Inventory inv, Optional<PositionContent> content) {
        this(id, inv, PositionContent.getOrNull(content, inv.player.level(), CentrifugeBlockEntity.class));
    }

    public CentrifugeMenu(int id, Inventory inv, CentrifugeBlockEntity entity) {
        super(ModMenuTypes.CENTRIFUGE.get(), id, inv, entity);

        if (player instanceof ServerPlayer serverPlayer) {
            entity.addListeningPlayer(serverPlayer);
            entity.sendToPlayer(serverPlayer);
        }
    }

    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            entity.removeListeningPlayer(serverPlayer);
        }
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 13;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 8;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 84;
    }

    @Override
    protected void addMenuSlots() {
        this.addSlot(new ResourceHandlerSlot(entity.itemResourceHandler(), entity.itemResourceHandler()::set, 0, 26, 11));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlot(new ResourceHandlerSlot(entity.itemResourceHandler(), entity.itemResourceHandler()::set, 1 + j + i * 4, 62 + j * 18, 11 + i * 18) {
                    @Override
                    public boolean mayPlace(@NonNull ItemStack stack) {
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
