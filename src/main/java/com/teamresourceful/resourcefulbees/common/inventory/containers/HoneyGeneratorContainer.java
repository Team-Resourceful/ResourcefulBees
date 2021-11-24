package com.teamresourceful.resourcefulbees.common.inventory.containers;

import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.ContainerAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyGeneratorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class HoneyGeneratorContainer extends ContainerWithStackMove {

    private final HoneyGeneratorTileEntity honeyGeneratorTileEntity;
    private final PlayerEntity player;

    public HoneyGeneratorContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        super(ModContainers.HONEY_GENERATOR_CONTAINER.get(), id);

        this.player = inv.player;

        honeyGeneratorTileEntity = ((HoneyGeneratorTileEntity) world.getBlockEntity(pos));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }
    }

    public FluidStack getFluid() {
        return getHoneyGeneratorTileEntity().getTank().getFluid();
    }

    public CustomEnergyStorage getEnergy() { return getHoneyGeneratorTileEntity().getEnergyStorage(); }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
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
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getHoneyGeneratorTileEntity() == null) return;
        for (IContainerListener listener : ((ContainerAccessor) this).getListeners()) getHoneyGeneratorTileEntity().sendGUINetworkPacket(listener);
    }

    public HoneyGeneratorTileEntity getHoneyGeneratorTileEntity() {
        return honeyGeneratorTileEntity;
    }

    public PlayerEntity getPlayer() {
        return player;
    }
}
