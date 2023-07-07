package com.teamresourceful.resourcefulbees.centrifuge.common.entities.base;

import com.teamresourceful.resourcefulbees.centrifuge.common.CentrifugeController;
import com.teamresourceful.resourcefulbees.centrifuge.common.blocks.AbstractCentrifuge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.roguelogix.phosphophyllite.modular.tile.PhosphophylliteTile;
import net.roguelogix.phosphophyllite.multiblock.IMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.common.IPersistentMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.rectangular.IRectangularMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.touching.ITouchingMultiblockTile;
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
