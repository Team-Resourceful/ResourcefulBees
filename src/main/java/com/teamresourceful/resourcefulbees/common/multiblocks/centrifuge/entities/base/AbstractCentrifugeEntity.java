package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.AbstractCentrifuge;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import net.minecraft.tileentity.TileEntityType;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockTile;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCentrifugeEntity extends RectangularMultiblockTile<CentrifugeController, AbstractCentrifugeEntity, AbstractCentrifuge> {
    protected AbstractCentrifugeEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @NotNull
    @Override
    public CentrifugeController createController() {
        if (level == null) {
            throw new IllegalStateException("Attempt to create controller with null world");
        }
        return new CentrifugeController(level);
    }

    public CentrifugeController getController() {
        return controller;
    }
}
