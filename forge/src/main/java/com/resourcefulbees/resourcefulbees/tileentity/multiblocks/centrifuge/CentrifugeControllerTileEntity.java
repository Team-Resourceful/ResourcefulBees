package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.CentrifugeCasingBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.CentrifugeControllerBlock;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.CentrifugeMultiblockContainer;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper.buildStructureBounds;
import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper.buildStructureList;

public class CentrifugeControllerTileEntity extends CentrifugeTileEntity {

    private static final int TANK_CAPACITY = 10000;
    private static final int INPUTS = 3;
    protected int validateTime = MathUtils.nextInt(10) + 10;
    protected boolean validStructure;
    protected final List<BlockPos> structureBlocks = new ArrayList<>();

    private final ContainerData times = new TimesArray(3);

    public CentrifugeControllerTileEntity(BlockEntityType<?> tileEntityType) { super(tileEntityType); }

    @Override
    public int getNumberOfInputs() { return INPUTS; }

    public void checkHoneycombSlots(){
        for (int i = 0; i < honeycombSlots.length; i++) {
            recipes.set(i, getRecipe(i));
            if (canStartCentrifugeProcess(i)) {
                isProcessing[i] = true;
            }
            if (isProcessing[i] && !processCompleted[i]) {
                processRecipe(i);
            }
            if (processCompleted[i]) {
                completeProcess(i);
            }
        }
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (isValidStructure() && (!requiresRedstone || isPoweredByRedstone)) {
                checkHoneycombSlots();
            }
            validateTime++;
            if (validateTime >= 20) {
                validateStructure(this.level);
            }
            if (dirty) {
                this.dirty = false;
                this.setChanged();
            }
        }
    }

    @Override
    public int getMaxTankCapacity() { return TANK_CAPACITY; }

    @Override
    public int getRecipeTime(int i) { return getRecipe(i) != null ? Math.max(5, getRecipe(i).multiblockTime) : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get(); }

    @Override
    protected boolean canProcessRecipe(int i) { return recipes.get(i) != null && (!Config.MULTIBLOCK_RECIPES_ONLY.get() || recipes.get(i).multiblock); }

    //endregion

    @Override
    protected void setPoweredBlockState(boolean powered) {
        //multiblock doesn't have a powered block state
    }

    //region NBT
/*    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag tag) {
        super.save(tag);
        return saveToNBT(tag);
    }*/

    @Override
    protected CompoundTag saveToNBT(CompoundTag tag) {
        tag.putBoolean("valid", validStructure);
        return super.saveToNBT(tag);
    }

    @Override
    protected void loadFromNBT(CompoundTag tag) {
        validStructure = tag.getBoolean("valid");
        super.loadFromNBT(tag);
    }

    @Override
    public void dropInventory(Level world, @NotNull BlockPos pos) {
        //do nothing
    }

    //endregion

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        assert level != null;
        return new CentrifugeMultiblockContainer(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id, level, worldPosition, playerInventory, times);
    }

    @NotNull
    @Override
    public Component getDisplayName() { return new TranslatableComponent("gui.resourcefulbees.centrifuge"); }

    @Override
    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MAX_CENTRIFUGE_RF.get() * 5, 500, 0) {
            @Override
            protected void onEnergyChanged() { setChanged(); }
        };
    }


    //region STRUCTURE VALIDATION

    protected BoundingBox getBounds() {
        return buildStructureBounds(this.getBlockPos(), 3, 4, 3, -1, -1, -2, this.getBlockState().getValue(CentrifugeControllerBlock.FACING));
    }

    protected Predicate<BlockPos> validBlocks() {
        return blockPos -> {
            assert level != null : "Validating Centrifuge - How is world null??";
            Block block = level.getBlockState(blockPos).getBlock();
            BlockEntity tileEntity = level.getBlockEntity(blockPos);
            if (block instanceof CentrifugeCasingBlock && tileEntity instanceof CentrifugeCasingTileEntity) {
                CentrifugeCasingTileEntity casing = (CentrifugeCasingTileEntity) tileEntity;
                return !casing.isLinked() || (casing.getController() != null && casing.getController().equals(this));
            }
            return false;
        };
    }

    protected void validateStructure(Level world) {
        validateTime = 0;
        buildStructureList(getBounds(), structureBlocks, blockPos -> true, this.getBlockPos());
        validStructure = MultiBlockHelper.validateStructure(structureBlocks, validBlocks(), 35);
        world.setBlockAndUpdate(worldPosition, getBlockState().setValue(CentrifugeControllerBlock.PROPERTY_VALID, validStructure));

        if (validStructure) {
            linkCasings(world);
        }
    }

    protected void linkCasings(Level world) {
        if (!world.isClientSide) {
            structureBlocks.stream()
                    .map(world::getBlockEntity)
                    .filter(CentrifugeCasingTileEntity.class::isInstance)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(this.worldPosition));
        }
    }

    protected void unlinkCasings(Level world) {
        if (!world.isClientSide) {
            structureBlocks.stream()
                    .map(world::getBlockEntity)
                    .filter(CentrifugeCasingTileEntity.class::isInstance)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(null));
        }
    }

    public void invalidateStructure() {
        assert level != null;
        this.validStructure = false;
        unlinkCasings(level);
    }

    public boolean isValidStructure() { return this.validStructure; }

    @Override
    public void setRemoved() {
        assert level != null;
        unlinkCasings(level);
        super.setRemoved();
    }

    //endregion
}

