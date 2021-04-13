package com.resourcefulbees.resourcefulbees.container;

import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EliteCentrifugeMultiblockContainer extends CentrifugeContainer {

    public EliteCentrifugeMultiblockContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
        this(ModContainers.ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id, world, pos, inv, new IntArray(6));
    }

    public EliteCentrifugeMultiblockContainer(ContainerType<?> containerType, int id, World world, BlockPos pos, PlayerInventory inv, IIntArray times) {
        super(containerType, id, world, pos, inv, times);
    }

    @Override
    protected void initialize() {
        inputXPos = 53;
        outputXPos = 44;
    }
}

