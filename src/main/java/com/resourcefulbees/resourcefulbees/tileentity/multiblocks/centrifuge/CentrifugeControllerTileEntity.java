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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

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
    protected final List<BlockPos> STRUCTURE_BLOCKS = new ArrayList<>();

    private final IntArray times = new IntArray(4) {
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
            }

        }

        public int size() { return 3; }
    };


    public CentrifugeControllerTileEntity(TileEntityType<?> tileEntityType) { super(tileEntityType); }

    @Override
    protected void initializeInputsAndOutputs() {
        honeycombSlots = new int[] {1, 2, 3};
        outputSlots = new int[] {4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21};
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote()) {
            if (isValidStructure() && (!requiresRedstone || isPoweredByRedstone)) {
                for (int i = 0; i < honeycombSlots.length; i++) {
                    recipes.set(i, getRecipe(i));
                    if (canStartCentrifugeProcess(i)) {
                        isProcessing[i] = true;
                    }
                    if (isProcessing[i] && !processCompleted[i]) {
                        processRecipe(i);
                    }
                    if (processCompleted[i]) {
                        processCompleted(i);
                    }
                }
            }
            validateTime++;
            if (validateTime >= 20) {
                validateStructure(this.world, null);
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
    public int getRecipeTime(int i) { return getRecipe(i) != null ? Math.max(5, getRecipe(i).time - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get()) : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get(); }

    @Override
    protected boolean canProcessRecipe(int i) { return recipes.get(i) != null && (!Config.MULTIBLOCK_RECIPES_ONLY.get() || recipes.get(i).multiblock); }

    //endregion

    //TODO this is not the right way to sync the fluids but using it for now until I can find a better way
    // Powered centrifuge is only syncing to client properly bc of the BlockState changes
    @Override
    protected void setPoweredBlockState(boolean powered) {
        assert world != null;
        world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
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

    protected final Predicate<BlockPos> validBlocks = blockPos -> {
        assert world != null : "Validating Centrifuge - How is world null??";
        Block block  = world.getBlockState(blockPos).getBlock();
        TileEntity tileEntity = world.getTileEntity(blockPos);
        if (block instanceof CentrifugeCasingBlock && tileEntity instanceof CentrifugeCasingTileEntity) {
            CentrifugeCasingTileEntity casing = (CentrifugeCasingTileEntity) tileEntity;
            return !casing.isLinked() || (casing.getController() != null && casing.getController().equals(this));
        }
        return false;
    };

    protected int numberOfCasingsRequired() { return 35; }

    protected void validateStructure(World world, @Nullable ServerPlayerEntity player) {
        validateTime = 0;
        buildStructureList(getBounds(), STRUCTURE_BLOCKS, blockPos -> true, this.getPos());
        validStructure = MultiBlockHelper.validateStructure(STRUCTURE_BLOCKS, validBlocks, numberOfCasingsRequired());
        world.setBlockState(pos, getBlockState().with(CentrifugeControllerBlock.PROPERTY_VALID, validStructure));

        if (validStructure) {
            linkCasings(world);
        } else if (player != null) {
            player.sendStatusMessage(new StringTextComponent("Centrifuge MultiBlock is Invalid"), false);
        } //TODO make a translation text component for this
    }

    protected void linkCasings(World world) {
        if (!world.isRemote) {
            STRUCTURE_BLOCKS.stream()
                    .map(world::getTileEntity)
                    .filter(tileEntity -> tileEntity instanceof CentrifugeCasingTileEntity)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(this.pos));
        }
    }

    protected void unlinkCasings(World world) {
        if (!world.isRemote) {
            STRUCTURE_BLOCKS.stream()
                    .map(world::getTileEntity)
                    .filter(tileEntity -> tileEntity instanceof CentrifugeCasingTileEntity)
                    .forEach(tileEntity -> ((CentrifugeCasingTileEntity) tileEntity).setControllerPos(null));
        }
    }

    public void invalidateStructure() {
        assert world != null;
        this.validStructure = false;
        unlinkCasings(world);
    }

    public boolean isValidStructure() {
        return this.validStructure;
    }

    @Override
    public void remove() {
        assert world != null;
        unlinkCasings(world);
        super.remove();
    }

    //endregion
}

