package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.CentrifugeController;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.AbstractCentrifuge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.phosphophyllite.modular.tile.PhosphophylliteTile;
import net.roguelogix.phosphophyllite.multiblock2.IMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock2.common.IPersistentMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock2.rectangular.IRectangularMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock2.touching.ITouchingMultiblockTile;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCentrifugeEntity extends PhosphophylliteTile
        implements IMultiblockTile<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        IPersistentMultiblockTile<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        IRectangularMultiblockTile<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        ITouchingMultiblockTile<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController> {

    protected AbstractCentrifugeEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @NotNull
    @Override
    public CentrifugeController createController() {
        if (level == null) {
            throw new IllegalStateException("Attempt to create controller with null world");
        }
        return new CentrifugeController(level);
    }
}
