package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class SolidificationChamberMenu extends AbstractModContainerMenu<SolidificationChamberBlockEntity> {

    public SolidificationChamberMenu(int id, Inventory inv,  Optional<PositionContent> content) {
        this(id, inv, PositionContent.getOrNull(content, inv.player.level(), SolidificationChamberBlockEntity.class));
    }

    public SolidificationChamberMenu(int id, Inventory inv, SolidificationChamberBlockEntity entity) {
        super(ModMenuTypes.SOLIDIFICATION_CHAMBER.get(), id, inv, entity);
    }

    @Override
    public int getContainerInputEnd() {
        return 1;
    }

    @Override
    public int getInventoryStart() {
        return 1;
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
        this.addSlot(new ResourceHandlerSlot(entity.itemHandler(), entity.itemHandler()::set, SolidificationChamberBlockEntity.BLOCK_OUTPUT, 93, 54) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return false;
            }
        });
        this.addDataSlot(entity.getProcessTime());
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
