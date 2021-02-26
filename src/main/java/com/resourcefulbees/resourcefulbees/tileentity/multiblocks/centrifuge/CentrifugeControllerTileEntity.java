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
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper.buildStructureBounds;
import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper.buildStructureList;

public class CentrifugeControllerTileEntity extends CentrifugeTileEntity {

    protected int validateTime = MathUtils.nextInt(10) + 10;
    protected boolean validStructure;
    protected final List<BlockPos> structureBlocks = new ArrayList<>();

    private final IntArray times = new IntArray(4) {

        @Override
        public int get(int index) {
            switch(index) {
                case 0:
                    return CentrifugeControllerTileEntity.this.time[0];
                case 1:
                    return CentrifugeControllerTileEntity.this.time[1];
                case 2:
                    return CentrifugeControllerTileEntity.this.time[2];
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0:
                    CentrifugeControllerTileEntity.this.time[0] = value;
                    break;
                case 1:
                    CentrifugeControllerTileEntity.this.time[1] = value;
                    break;
                case 2:
                    CentrifugeControllerTileEntity.this.time[2] = value;
                    break;
                default: //do nothing
            }

        }

        @Override
        public int size() { return 3; }
    };


    public CentrifugeControllerTileEntity(TileEntityType<?> tileEntityType) { super(tileEntityType); }

    @Override
    public int getNumberOfInputs() { return 3; }

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
                completedProcess(i);
            }
        }
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote()) {
            if (isValidStructure() && (!requiresRedstone || isPoweredByRedstone)) {
                checkHoneycombSlots();
            }
            validateTime++;
            if (validateTime >= 20) {
                validateStructure(this.world);
            }
            if (dirty) {
                this.dirty = false;
                this.markDirty();
            }
        }
    }

    @Override
    public int getMaxTankCapacity() { return 10000; }

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
    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tag) {
        super.write(tag);
        return saveToNBT(tag);
    }

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
    //endregion

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        assert world != null;
        return new CentrifugeMultiblockContainer(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), id, world, pos, playerInventory, times);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.resourcefulbees.centrifuge"); }

    @Override
    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MAX_CENTRIFUGE_RF.get() * 5, 500, 0) {
            @Override
            protected void onEnergyChanged() { markDirty(); }
        };
    }


    //region STRUCTURE VALIDATION

    protected MutableBoundingBox getBounds() {
        return buildStructureBounds(this.getPos(), 3, 4, 3, -1, -1, -2, this.getBlockState().get(CentrifugeControllerBlock.FACING));
    }

    protected Predicate<BlockPos> validBlocks() {
        return blockPos -> {
            assert world != null : "Validating Centrifuge - How is world null??";
            Block block = world.getBlockState(blockPos).getBlock();
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (block instanceof CentrifugeCasingBlock && tileEntity instanceof CentrifugeCasingTileEntity) {
                CentrifugeCasingTileEntity casing = (CentrifugeCasingTileEntity) tileEntity;
                return !casing.isLinked() || (casing.getController() != null && casing.getController().equals(this));
            }
            return false;
        };
    }

    protected void validateStructure(World world) {
        validateTime = 0;
        buildStructureList(getBounds(), structureBlocks, blockPos -> true, this.getPos());
        validStructure = MultiBlockHelper.validateStructure(structureBlocks, validBlocks(), 35);
        world.setBlockState(pos, getBlockState().with(CentrifugeControllerBlock.PROPERTY_VALID, validStructure));

        if (validStructure) {
            linkCasings(world);
        }
    }

    protected void linkCasings(World world) {
        if (!world.isRemote) {
            structureBlocks.stream()
                    .map(world::getTileEntity)
                    .filter(CentrifugeCasingTileEntity.class::isInstance)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(this.pos));
        }
    }

    protected void unlinkCasings(World world) {
        if (!world.isRemote) {
            structureBlocks.stream()
                    .map(world::getTileEntity)
                    .filter(CentrifugeCasingTileEntity.class::isInstance)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(null));
        }
    }

    public void invalidateStructure() {
        assert world != null;
        this.validStructure = false;
        unlinkCasings(world);
    }

    public boolean isValidStructure() { return this.validStructure; }

    @Override
    public void remove() {
        assert world != null;
        unlinkCasings(world);
        super.remove();
    }

    //endregion
}

