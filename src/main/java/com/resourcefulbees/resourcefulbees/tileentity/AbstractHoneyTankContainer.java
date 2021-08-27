package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.block.ColoredHoneyBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractHoneyTankContainer extends AbstractHoneyTank implements ITickableTileEntity, INamedContainerProvider {

    public static final ITag<Fluid> HONEY_FLUID_TAG = BeeInfoUtils.getFluidTag("forge:honey");
    public static final ITag<Item> HONEY_BOTTLE_TAG = BeeInfoUtils.getItemTag("forge:honey_bottle");
    public static final int BOTTLE_INPUT_EMPTY = 0;
    public static final int BOTTLE_OUTPUT_EMPTY = 1;
    public static final int HONEY_FILL_AMOUNT = ModConstants.HONEY_PER_BOTTLE;
    private AutomationSensitiveItemStackHandler tileStackHandler = new TileStackHandler(2, getAcceptor(), getRemover());
    private LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private boolean dirty;
    private int processingEmpty;

    public float getProcessEmptyPercent() {
        if (!canProcessFluid()) return 0;
        if (processingEmpty == Config.HONEY_PROCEESS_TIME.get()) return 1;
        return processingEmpty / (float) Config.HONEY_PROCEESS_TIME.get();
    }

    public void setTileStackHandler(AutomationSensitiveItemStackHandler tileStackHandler) {
        this.tileStackHandler = tileStackHandler;
        lazyOptional = LazyOptional.of(this::getTileStackHandler);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public AbstractHoneyTankContainer(TileEntityType<?> tileEntityType, int level) {
        super(tileEntityType, level);
    }

    public AbstractHoneyTankContainer(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public boolean canStartFluidProcess() {
        ItemStack stack = getTileStackHandler().getStackInSlot(BOTTLE_INPUT_EMPTY);
        ItemStack output = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT_EMPTY);

        boolean stackValid = false;
        boolean isBucket = false;
        boolean isBlock = false;
        boolean outputValid;
        boolean hasRoom;
        if (!stack.isEmpty()) {
            BlockItem blockItem = null;
            if (stack.getItem() instanceof BlockItem){
                blockItem = (BlockItem) stack.getItem();
            }
            if (blockItem != null && blockItem.getBlock() instanceof ColoredHoneyBlock) {
                stackValid = ((ColoredHoneyBlock) blockItem.getBlock()).getData().doGenerateHoneyFluid();
                isBlock = true;
            } else if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                stackValid = bucket.getFluid().is(HONEY_FLUID_TAG);
                isBucket = true;
            } else {
                stackValid = stack.getItem().is(HONEY_BOTTLE_TAG);
            }
        }
        if (!output.isEmpty() && !isBlock) {
            if (isBucket) {
                outputValid = output.getItem() == Items.BUCKET;
            } else {
                outputValid = output.getItem() == Items.GLASS_BOTTLE;
            }
            hasRoom = output.getCount() < output.getMaxStackSize();
        } else {
            outputValid = true;
            hasRoom = true;
        }
        return stackValid && outputValid && hasRoom;
    }

    public void processFluid() {
        if (canProcessFluid()) {
            ItemStack stack = getTileStackHandler().getStackInSlot(BOTTLE_INPUT_EMPTY);
            ItemStack output = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT_EMPTY);
            BlockItem blockItem = null;
            if (stack.getItem() instanceof BlockItem){
                blockItem = (BlockItem) stack.getItem();
            }
            if (blockItem != null && blockItem.getBlock() instanceof ColoredHoneyBlock) {
                stack.shrink(1);
                HoneyBottleData data = ((ColoredHoneyBlock) blockItem.getBlock()).getData();
                getFluidTank().fill(new FluidStack(data.getHoneyStillFluidRegistryObject().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
            } else if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                getFluidTank().fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                stack.shrink(1);
                if (output.isEmpty()) {
                    getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT_EMPTY, new ItemStack(Items.BUCKET));
                } else {
                    output.grow(1);
                }
            } else {
                FluidStack fluidStack = new FluidStack(BeeInfoUtils.getFluidFromBottle(stack), HONEY_FILL_AMOUNT);
                if (!fluidStack.isEmpty()) getFluidTank().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                stack.shrink(1);
                if (output.isEmpty()) {
                    getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT_EMPTY, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    output.grow(1);
                }
            }
            this.dirty = true;
        }
    }

    public boolean canProcessFluid() {
        boolean spaceLeft;
        ItemStack stack = getTileStackHandler().getStackInSlot(BOTTLE_INPUT_EMPTY);
        if (!canStartFluidProcess()) return false;
        Fluid fluid;
        BlockItem blockItem = null;
        if (stack.getItem() instanceof BlockItem){
            blockItem = (BlockItem) stack.getItem();
        }
        if (blockItem != null && blockItem.getBlock() instanceof ColoredHoneyBlock) {
            HoneyBottleData item = ((ColoredHoneyBlock) blockItem.getBlock()).getData();
            spaceLeft = (getFluidTank().getFluidAmount() + 1000) <= getFluidTank().getCapacity();
            fluid = item.getHoneyStillFluidRegistryObject().get();
        } else if (stack.getItem() instanceof BucketItem) {
            BucketItem item = (BucketItem) stack.getItem();
            spaceLeft = (getFluidTank().getFluidAmount() + 1000) <= getFluidTank().getCapacity();
            fluid = item.getFluid();
        } else {
            spaceLeft = (getFluidTank().getFluidAmount() + HONEY_FILL_AMOUNT) <= getFluidTank().getCapacity();
            fluid = BeeInfoUtils.getFluidFromBottle(stack);
        }
        return spaceLeft && (getFluidTank().getFluid().getFluid() == fluid || getFluidTank().isEmpty());
    }

    public @NotNull AutomationSensitiveItemStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    public static boolean isItemValid(ItemStack stack) {
        BlockItem blockItem = null;
        if (stack.getItem() instanceof BlockItem){
            blockItem = (BlockItem) stack.getItem();
        }
        if (blockItem != null && blockItem.getBlock() instanceof ColoredHoneyBlock) {
            ColoredHoneyBlock block = (ColoredHoneyBlock) blockItem.getBlock();
            return block.getData().doGenerateHoneyFluid();
        } else if (stack.getItem() instanceof BucketItem) {
            BucketItem bucket = (BucketItem) stack.getItem();
            return bucket.getFluid().is(HONEY_FLUID_TAG);
        } else {
            return stack.getItem().is(HONEY_BOTTLE_TAG);
        }
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 1;
    }

    @Override
    public void tick() {
        if (canProcessFluid()) {
            if (processingEmpty >= Config.HONEY_PROCEESS_TIME.get()) {
                processFluid();
                processingEmpty = 0;
            }
            processingEmpty++;
        }

        if (dirty) {
            this.dirty = false;
            this.setChanged();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return lazyOptional.cast();
        return super.getCapability(cap, side);
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots, acceptor, remover);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return AbstractHoneyTankContainer.isItemValid(stack);
        }
    }
}
