package com.teamresourceful.resourcefulbees.common.inventory.menus;

import com.teamresourceful.resourcefulbees.common.blockentity.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class HoneyGeneratorMenu extends AbstractModContainerMenu<HoneyGeneratorBlockEntity> {

    public HoneyGeneratorMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getTileFromBuf(inv.player.level, buf, HoneyGeneratorBlockEntity.class));
    }

    public HoneyGeneratorMenu(int id, Inventory inv, HoneyGeneratorBlockEntity entity) {
        super(ModMenus.HONEY_GENERATOR_CONTAINER.get(), id, inv, entity);
    }

    public FluidStack getFluid() {
        return entity.getTank().getFluid();
    }

    public CustomEnergyStorage getEnergy() { return entity.getEnergyStorage(); }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
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
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    public int getPlayerInvXOffset() {
        return 84;
    }

    @Override
    public int getPlayerInvYOffset() {
        return 142;
    }

    @Override
    protected void addMenuSlots() {
        //nothing
    }
}
