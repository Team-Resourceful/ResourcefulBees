package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.block.multiblocks.centrifuge.CentrifugeCasingBlock;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.centrifuge.CentrifugeControllerBlock;
import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.config.Config;
import com.teamresourceful.resourcefulbees.common.container.CentrifugeMultiblockContainer;
import com.teamresourceful.resourcefulbees.common.registry.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.CentrifugeTileEntity;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.MultiBlockHelper;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.MultiBlockHelper.buildStructureBounds;
import static com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.MultiBlockHelper.buildStructureList;

public class CentrifugeControllerTileEntity extends CentrifugeTileEntity {

    private static final int TANK_CAPACITY = 10000;
    private static final int INPUTS = 3;
    protected int validateTime = MathUtils.nextInt(10) + 10;
    protected boolean validStructure;
    protected final List<BlockPos> structureBlocks = new ArrayList<>();

    private final IIntArray times = new TimesArray(3);

    public CentrifugeControllerTileEntity(TileEntityType<?> tileEntityType) { super(tileEntityType); }

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
    public int getRecipeTime(int i) { return getRecipe(i) != null ? Math.max(5, getRecipe(i).getMultiblockTime()) : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get(); }

    @Override
    protected boolean canProcessRecipe(int i) { return recipes.get(i) != null && (!Config.MULTIBLOCK_RECIPES_ONLY.get() || recipes.get(i).isMultiblock()); }

    //endregion

    @Override
    protected void setPoweredBlockState(boolean powered) {
        //multiblock doesn't have a powered block state
    }

    //region NBT

    @Override
    protected CompoundNBT saveToNBT(CompoundNBT tag) {
        tag.putBoolean("valid", validStructure);
        return super.saveToNBT(tag);
    }

    @Override
    protected void loadFromNBT(CompoundNBT tag) {
        validStructure = tag.getBoolean("valid");
        super.loadFromNBT(tag);
    }

    @Override
    public void dropInventory(World world, @NotNull BlockPos pos) {
        //do nothing
    }

    //endregion

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        assert level != null;
        return new CentrifugeMultiblockContainer(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id, level, worldPosition, playerInventory, times);
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.resourcefulbees.centrifuge"); }

    @Override
    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MAX_CENTRIFUGE_RF.get() * 5, 500, 0) {
            @Override
            protected void onEnergyChanged() { setChanged(); }
        };
    }


    //region STRUCTURE VALIDATION

    protected MutableBoundingBox getBounds() {
        return buildStructureBounds(this.getBlockPos(), 3, 4, 3, -1, -1, -2, this.getBlockState().getValue(CentrifugeControllerBlock.FACING));
    }

    protected Predicate<BlockPos> validBlocks() {
        return blockPos -> {
            assert level != null : "Validating Centrifuge - How is world null??";
            Block block = level.getBlockState(blockPos).getBlock();
            TileEntity tileEntity = level.getBlockEntity(blockPos);
            if (block instanceof CentrifugeCasingBlock && tileEntity instanceof CentrifugeCasingTileEntity) {
                CentrifugeCasingTileEntity casing = (CentrifugeCasingTileEntity) tileEntity;
                return !casing.isLinked() || (casing.getController() != null && casing.getController().equals(this));
            }
            return false;
        };
    }

    protected void validateStructure(World world) {
        validateTime = 0;
        buildStructureList(getBounds(), structureBlocks, blockPos -> true, this.getBlockPos());
        validStructure = MultiBlockHelper.validateStructure(structureBlocks, validBlocks(), 35);
        world.setBlockAndUpdate(worldPosition, getBlockState().setValue(CentrifugeControllerBlock.PROPERTY_VALID, validStructure));

        if (validStructure) {
            linkCasings(world);
        }
    }

    protected void linkCasings(World world) {
        if (!world.isClientSide) {
            structureBlocks.stream()
                    .map(world::getBlockEntity)
                    .filter(CentrifugeCasingTileEntity.class::isInstance)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(this.worldPosition));
        }
    }

    protected void unlinkCasings(World world) {
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

