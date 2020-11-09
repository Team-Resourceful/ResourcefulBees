package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import net.minecraft.tileentity.TileEntityType;

public class SuperCentrifugeControllerTileEntity extends CentrifugeControllerTileEntity {

    protected final int[] HONEYCOMB_SLOTS = {1,2,3,4,5,6,7,8,9};

    public SuperCentrifugeControllerTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }
}
