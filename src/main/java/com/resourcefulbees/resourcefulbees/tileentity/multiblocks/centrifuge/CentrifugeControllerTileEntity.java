package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.centrifuge;

import com.google.gson.JsonElement;
import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.CentrifugeCasingBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.centrifuge.CentrifugeControllerBlock;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.capabilities.MultiFluidTank;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.CentrifugeMultiblockContainer;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper;
import com.resourcefulbees.resourcefulbees.utils.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
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
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper.buildStructureBounds;
import static com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper.buildStructureList;
import static net.minecraft.inventory.container.Container.areItemsAndTagsEqual;

public class CentrifugeControllerTileEntity extends CentrifugeTileEntity {

    public static final int BOTTLE_SLOT = 0;
    protected int validateTime = MathUtils.nextInt(10) + 10;
    protected boolean validStructure;
    protected final List<BlockPos> STRUCTURE_BLOCKS = new ArrayList<>();

    private final IIntArray times = new IIntArray() {
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
    protected void initialize() {
        honeycombSlots = new int[] {1, 2, 3};
        outputSlots = new int[] {4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21};
        itemStackHandler = new CentrifugeControllerTileEntity.TileStackHandler(22);
        fluidTanks = new MultiFluidTank(10000, 4);
        time = new int[honeycombSlots.length];
        recipes = Arrays.asList(null, null, null);
        isProcessing = new boolean[honeycombSlots.length];
        processCompleted = new boolean[honeycombSlots.length];
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote()) {
            if (isValidStructure()) {
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
    protected void processRecipe(int i) {
        if (canProcess(recipes.get(i), i)) {
            energyStorage.consumeEnergy(Config.RF_TICK_CENTRIFUGE.get());
            ++time[i];
            processCompleted[i] = time[i] >= Math.max(10, recipes.get(i).time - Config.MULTIBLOCK_RECIPE_TIME_REDUCTION.get());
            this.dirty = true;
        } else {
            resetProcess(i);
        }
    }

    @Override
    protected boolean canStartCentrifugeProcess(int i) {
        if (!isProcessing[i] && !itemStackHandler.getStackInSlot(honeycombSlots[i]).isEmpty()) {
            if (recipes.get(i) != null && (!Config.MULTIBLOCK_RECIPES_ONLY.get() || recipes.get(i).multiblock)) {
                ItemStack combInput = itemStackHandler.getStackInSlot(honeycombSlots[i]);
                JsonElement count = recipes.get(i).ingredient.serialize().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
                int inputAmount = count != null ? count.getAsInt() : 1;

                return combInput.getCount() >= inputAmount ;
            }
        }
        return false;
    }

    //endregion

    @Override
    protected void depositItemStacks(List<ItemStack> itemStacks) {
        itemStacks.forEach(itemStack -> {
            int slotIndex = 4;
            while (!itemStack.isEmpty() && slotIndex < itemStackHandler.getSlots()){
                ItemStack slotStack = itemStackHandler.getStackInSlot(slotIndex);

                int itemMaxStackSize = itemStack.getMaxStackSize();

                if(slotStack.isEmpty()) {
                    itemStackHandler.setStackInSlot(slotIndex, itemStack.split(itemMaxStackSize));
                } else if (areItemsAndTagsEqual(itemStack, slotStack) && slotStack.getCount() != itemMaxStackSize) {
                    int combinedCount = itemStack.getCount() + slotStack.getCount();
                    if (combinedCount <= itemMaxStackSize) {
                        itemStack.setCount(0);
                        slotStack.setCount(combinedCount);
                    } else {
                        itemStack.shrink(itemMaxStackSize - slotStack.getCount());
                        slotStack.setCount(itemMaxStackSize);
                    }
                    itemStackHandler.setStackInSlot(slotIndex, slotStack);
                }

                ++slotIndex;
            }
        });
    }

    @Override
    protected boolean inventoryHasSpace(CentrifugeRecipe recipe) {
        int emptySlots = 0;

        for (int i = outputSlots[0]; i < itemStackHandler.getSlots(); ++i) {
            if (itemStackHandler.getStackInSlot(i).isEmpty()) {
                emptySlots++;
            }
        }

        boolean hasSpace = true;
        int i = 0;
        while (hasSpace && i < recipe.itemOutputs.size()) {
            ItemStack output = recipe.itemOutputs.get(i).getLeft();
            if (!output.isEmpty() && !(i == 2 && itemStackHandler.getStackInSlot(BOTTLE_SLOT).isEmpty())) {
                int count = output.getCount();
                int j = outputSlots[0];

                while (count > 0 && j < itemStackHandler.getSlots()) {
                    ItemStack slotStack = itemStackHandler.getStackInSlot(j);

                    if (slotStack.isEmpty() && emptySlots != 0) {
                        count -= Math.min(count, output.getMaxStackSize());
                        emptySlots--;
                    } else if (areItemsAndTagsEqual(output, slotStack) && slotStack.getCount() != output.getMaxStackSize()) {
                        count -= Math.min(count, output.getMaxStackSize() - slotStack.getCount());
                    }

                    j++;
                }

                hasSpace = count <= 0;
            }
            i++;
        }

        return hasSpace;
    }

    @Override
    protected void setPoweredBlockState(boolean powered) {}

    //region NBT
    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tag) {
        super.write(tag);
        return saveToNBT(tag);
    }

    protected CompoundNBT saveToNBT(CompoundNBT tag) {
        tag.putBoolean("valid", validStructure);
        return tag;
    }

    protected void loadFromNBT(CompoundNBT tag) {
        validStructure = tag.getBoolean("valid");
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        this.loadFromNBT(tag);
        super.fromTag(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) { this.fromTag(state, tag); }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos,0,saveToNBT(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        loadFromNBT(nbt);
    }
    //endregion

    //region Capabilities
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.lazyOptional.invalidate();
        this.energyOptional.invalidate();
        this.fluidOptional.invalidate();
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) ->
            !automation ||
            (
                (slot == honeycombSlots[0] || slot == honeycombSlots[1] || slot == honeycombSlots[2]) &&
                !stack.getItem().equals(Items.GLASS_BOTTLE)
            )||(
                slot == BOTTLE_SLOT &&
                stack.getItem().equals(Items.GLASS_BOTTLE)
            );
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot > 3;
    }
    //endregion

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new CentrifugeMultiblockContainer(id, world, pos, playerInventory, times);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.resourcefulbees.centrifuge"); }

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

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots) { super(slots); }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return CentrifugeControllerTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return CentrifugeControllerTileEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }
}

