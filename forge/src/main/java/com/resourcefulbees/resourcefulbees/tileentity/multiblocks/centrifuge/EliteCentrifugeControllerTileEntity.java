package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.EliteCentrifugeCasingBlock;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import com.resourcefulbees.resourcefulbees.container.CentrifugeMultiblockContainer;
import com.resourcefulbees.resourcefulbees.container.EliteCentrifugeMultiblockContainer;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EliteCentrifugeControllerTileEntity extends CentrifugeControllerTileEntity {
    private final ContainerData times = new SimpleContainerData(6) {

        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return EliteCentrifugeControllerTileEntity.this.time[0];
                case 1:
                    return EliteCentrifugeControllerTileEntity.this.time[1];
                case 2:
                    return EliteCentrifugeControllerTileEntity.this.time[2];
                case 3:
                    return EliteCentrifugeControllerTileEntity.this.time[3];
                case 4:
                    return EliteCentrifugeControllerTileEntity.this.time[4];
                case 5:
                    return EliteCentrifugeControllerTileEntity.this.time[5];
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0:
                    EliteCentrifugeControllerTileEntity.this.time[0] = value;
                    break;
                case 1:
                    EliteCentrifugeControllerTileEntity.this.time[1] = value;
                    break;
                case 2:
                    EliteCentrifugeControllerTileEntity.this.time[2] = value;
                    break;
                case 3:
                    EliteCentrifugeControllerTileEntity.this.time[3] = value;
                    break;
                case 4:
                    EliteCentrifugeControllerTileEntity.this.time[4] = value;
                    break;
                case 5:
                    EliteCentrifugeControllerTileEntity.this.time[5] = value;
                    break;
                default: //do nothing
            }

        }

        @Override
        public int getCount() { return 6; }
    };

    public EliteCentrifugeControllerTileEntity(BlockEntityType<?> tileEntityType) { super(tileEntityType); }

    @Override
    public int getNumberOfInputs() { return 6; }

    @Override
    public int getMaxTankCapacity() { return 50000; }

    @Override
    public int getRecipeTime(int i) { return getRecipe(i) != null ? Math.max(5, (int)(getRecipe(i).multiblockTime * 0.5)) : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get(); }

    @Override
    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MAX_CENTRIFUGE_RF.get() * 10, 1000, 0) {
            @Override
            protected void onEnergyChanged() { setChanged(); }
        };
    }

    @Override
    protected Predicate<BlockPos> validBlocks() {
        return blockPos -> {
            assert level != null : "Validating Centrifuge - How is world null??";
            Block block = level.getBlockState(blockPos).getBlock();
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (block instanceof EliteCentrifugeCasingBlock && tileEntity instanceof EliteCentrifugeCasingTileEntity) {
                EliteCentrifugeCasingTileEntity casing = (EliteCentrifugeCasingTileEntity) tileEntity;
                return !casing.isLinked() || (casing.getController() != null && casing.getController().equals(this));
            }
            return false;
        };
    }

    @Nullable
    @Override
    public CentrifugeContainer createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
        assert level != null;
        return new EliteCentrifugeMultiblockContainer(ModContainers.ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id, level, worldPosition, playerInventory, times);
    }
}
