package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.EliteCentrifugeCasingBlock;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import com.resourcefulbees.resourcefulbees.container.EliteCentrifugeMultiblockContainer;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class EliteCentrifugeControllerTileEntity extends CentrifugeControllerTileEntity {
    private static final int INPUTS = 6;
    private static final int TANK_CAPACITY = 50000;
    private final ContainerData times = new TimesArray(6);

    public EliteCentrifugeControllerTileEntity(BlockEntityType<?> tileEntityType) { super(tileEntityType); }

    @Override
    public int getNumberOfInputs() { return INPUTS; }

    @Override
    public int getMaxTankCapacity() { return TANK_CAPACITY; }

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
    public CentrifugeContainer createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        assert level != null;
        return new EliteCentrifugeMultiblockContainer(ModContainers.ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id, level, worldPosition, playerInventory, times);
    }
}
