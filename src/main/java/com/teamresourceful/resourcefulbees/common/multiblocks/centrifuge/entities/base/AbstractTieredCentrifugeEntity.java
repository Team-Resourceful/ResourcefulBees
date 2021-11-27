package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractTieredCentrifugeEntity extends AbstractCentrifugeEntity {

    protected final CentrifugeTier tier;
    private ITextComponent name;

    protected AbstractTieredCentrifugeEntity(TileEntityType<?> tileEntityTypeIn, CentrifugeTier tier) {
        super(tileEntityTypeIn);
        this.tier = tier;
    }

    public CentrifugeTier getTier() {
        return tier;
    }
}
