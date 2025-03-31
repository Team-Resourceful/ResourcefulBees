package com.teamresourceful.resourcefulbees.common.menus;

import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.menus.base.AutomationSensitiveSlot;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HoneyGeneratorMenu extends AbstractModContainerMenu<HoneyGeneratorBlockEntity> {

    public HoneyGeneratorMenu(int id, Inventory inv, Optional<PositionContent> content) {
        this(id, inv, PositionContent.getOrNull(content, inv.player.level(), HoneyGeneratorBlockEntity.class));
    }

    public HoneyGeneratorMenu(int id, Inventory inv, HoneyGeneratorBlockEntity entity) {
        super(ModMenuTypes.HONEY_GENERATOR.get(), id, inv, entity);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public int getContainerInputEnd() {
        return 4;
    }

    @Override
    public int getInventoryStart() {
        return 4;
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
        for (int i = 0; i < 4; i++) {
            this.addSlot(new AutomationSensitiveSlot(getEntity().getContainer(), i, 53 + i * 18, 54));
        }
    }
}
