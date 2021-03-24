package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractHoneyTankContainer extends AbstractHoneyTank implements ITickableTileEntity, INamedContainerProvider {

    public static final ITag<Fluid> HONEY_FLUID_TAG = BeeInfoUtils.getFluidTag("forge:honey");
    public static final ITag<Item> HONEY_BOTTLE_TAG = BeeInfoUtils.getItemTag("forge:honey_bottle");
    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_FILL_AMOUNT = ModConstants.HONEY_PER_BOTTLE;
    private final AutomationSensitiveItemStackHandler tileStackHandler = new EnderBeeconTileEntity.TileStackHandler(2, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private boolean dirty;
    private int processing;

    public float getProcessPercent() {
        if (processing == Config.HONEY_PROCEESS_TIME.get()) return 1;
        return processing / (float) Config.HONEY_PROCEESS_TIME.get();
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
        ItemStack stack = getTileStackHandler().getStackInSlot(HONEY_BOTTLE_INPUT);
        ItemStack output = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);

        boolean stackValid = false;
        boolean isBucket = false;
        boolean outputValid;
        boolean hasRoom;
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                stackValid = bucket.getFluid().is(HONEY_FLUID_TAG);
                isBucket = true;
            } else {
                stackValid = stack.getItem().is(HONEY_BOTTLE_TAG);
            }
        }
        if (!output.isEmpty()) {
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
            ItemStack stack = getTileStackHandler().getStackInSlot(HONEY_BOTTLE_INPUT);
            ItemStack output = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT);
            if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                getFluidTank().fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                stack.shrink(1);
                if (output.isEmpty()) {
                    getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT, new ItemStack(Items.BUCKET));
                } else {
                    output.grow(1);
                }
            } else {
                FluidStack fluidStack = new FluidStack(BeeInfoUtils.getFluidFromBottle(stack), HONEY_FILL_AMOUNT);
                if (!fluidStack.isEmpty()) getFluidTank().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                stack.shrink(1);
                if (output.isEmpty()) {
                    getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    output.grow(1);
                }
            }
            this.dirty = true;
        }
    }

    public boolean canProcessFluid() {
        boolean spaceLeft;
        ItemStack stack = getTileStackHandler().getStackInSlot(HONEY_BOTTLE_INPUT);
        if (stack.isEmpty()) return false;
        Fluid fluid;
        if (stack.getItem() instanceof BucketItem) {
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
        if (stack.getItem() instanceof BucketItem) {
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
            if (processing >= Config.HONEY_PROCEESS_TIME.get()) {
                processFluid();
                processing = 0;
            }
            processing++;
        }

        if (dirty) {
            this.dirty = false;
            this.setChanged();
        }
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
