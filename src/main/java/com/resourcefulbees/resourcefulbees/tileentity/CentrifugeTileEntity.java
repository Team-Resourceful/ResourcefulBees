
package com.resourcefulbees.resourcefulbees.tileentity;

import com.google.gson.JsonElement;
import com.resourcefulbees.resourcefulbees.block.CentrifugeBlock;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.capabilities.MultiFluidTank;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.utils.NBTUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.inventory.container.Container.consideredTheSameItem;

public class CentrifugeTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int BOTTLE_SLOT = 0;
    public static final int HONEYCOMB_SLOT = 1;
    public static final int OUTPUT1 = 2;
    public static final int OUTPUT2 = 3;
    public static final int HONEY_BOTTLE = 4;

    protected int[] honeycombSlots;
    protected int[] outputSlots;

    protected AutomationSensitiveItemStackHandler itemStackHandler;
    protected MultiFluidTank fluidTanks;
    protected CustomEnergyStorage energyStorage = createEnergy();
    protected LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> itemStackHandler);
    protected LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    protected LazyOptional<MultiFluidTank> fluidOptional = LazyOptional.of(() -> fluidTanks);
    protected int[] time;
    protected List<CentrifugeRecipe> recipes;
    protected ItemStack failedMatch = ItemStack.EMPTY;
    protected boolean dirty;
    protected boolean[] isProcessing;
    protected boolean[] processCompleted;
    protected boolean isPoweredByRedstone;
    protected boolean requiresRedstone;

    private final IntArray times = new IntArray(1) {
        @Override
        public int get(int i) {
            return time[0];
        }

        @Override
        public void set(int i, int i1) {
            time[0] = i1;
        }
    };

    public CentrifugeTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        initializeInputsAndOutputs();
        itemStackHandler = new CentrifugeTileEntity.TileStackHandler(getTotalSlots());
        fluidTanks = new MultiFluidTank(getMaxTankCapacity(), getTotalTanks());
        time = new int[getNumberOfInputs()];
        recipes = Arrays.asList(null, null, null, null, null, null, null, null, null);
        isProcessing = new boolean[getNumberOfInputs()];
        processCompleted = new boolean[getNumberOfInputs()];
    }

    protected void initializeInputsAndOutputs() {
        honeycombSlots = new int[getNumberOfInputs()];
        for (int i = 0; i < honeycombSlots.length; i++) {
            honeycombSlots[i] = i + 1;
        }

        outputSlots = new int[getNumberOfInputs() * 6];
        for (int i = 0; i < outputSlots.length; i++) {
            outputSlots[i] = i + honeycombSlots.length + 1;
        }
    }


    public int[] getHoneycombSlots() {
        return this.honeycombSlots;
    }

    public int[] getOutputSlots() {
        return this.outputSlots;
    }

    public AutomationSensitiveItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    @Override
    public void tick() {
        boolean redstoneCheck = requiresRedstone && !isPoweredByRedstone;
        boolean worldCheck = level == null || level.isClientSide();
        if (worldCheck || redstoneCheck) return;
        for (int i = 0; i < honeycombSlots.length; i++) {
            recipes.set(i, getRecipe(i));
            if (canStartCentrifugeProcess(i)) {
                isProcessing[i] = true;
                setPoweredBlockState(true);
            }
            if (isProcessing[i] && !processCompleted[i]) {
                processRecipe(i);
            }
            if (processCompleted[i]) {
                completeProcess(i);
            }
        }
        if (dirty) {
            this.dirty = false;
            this.setChanged();
        }
    }

    protected void resetProcess(int i) {
        processCompleted[i] = false;
        isProcessing[i] = false;
        time[i] = 0;
        recipes.set(i, null);
        setPoweredBlockState(false);
    }

    protected void completeProcess(int i) {
        if (recipes.get(i) == null) {
            resetProcess(i);
            return;
        }
        if (!inventoryHasSpace(recipes.get(i))) {
            return;
        }
        if (!tanksHasSpace(recipes.get(i))) {
            return;
        }
        consumeInput(i);
        ItemStack glass_bottle = itemStackHandler.getStackInSlot(BOTTLE_SLOT);
        List<ItemStack> depositStacks = new ArrayList<>();
        if (level == null) {
            resetProcess(i);
            return;
        }
        CentrifugeRecipe recipe = recipes.get(i);

        for (int j = 0; j < recipe.itemOutputs.size(); j++) {
            float chance = recipe.itemOutputs.get(j).getRight();
            if (chance >= level.random.nextFloat()) {
                depositStacks.add(recipe.itemOutputs.get(j).getLeft().copy());
                if (j == 2 && !recipe.noBottleInput) {
                    glass_bottle.shrink(recipes.get(i).itemOutputs.get(2).getLeft().getCount());
                }
            }
        }
        for (Pair<FluidStack, Float> fluidOutput : recipe.fluidOutput) {
            float chance = fluidOutput.getRight();
            if (chance >= level.random.nextFloat()) {
                FluidStack fluid = fluidOutput.getLeft().copy();
                int tank = getValidTank(fluid);
                fluidTanks.fill(tank, fluid, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        if (!depositStacks.isEmpty()) {
            depositItemStacks(depositStacks);
        }
        resetProcess(i);
    }

    private boolean tanksHasSpace(CentrifugeRecipe centrifugeRecipe) {
        for (Pair<FluidStack, Float> f : centrifugeRecipe.fluidOutput) {
            if (f.getLeft().isEmpty()) continue;
            if (getValidTank(f.getKey()) < 0) {
                return false;
            }
        }
        return true;
    }

    private int getValidTank(FluidStack fluid) {
        for (int i = 0; i < fluidTanks.getTanks(); i++) {
            if (fluidTanks.getFluidInTank(i).getFluid() == fluid.getFluid() || fluidTanks.getFluidInTank(i).isEmpty()) {
                if (fluidTanks.getFluidInTank(i).getAmount() + fluid.getAmount() <= fluidTanks.getTankCapacity(i)) {
                    return i;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    protected void processRecipe(int i) {
        if (canProcess(i)) {
            energyStorage.consumeEnergy(Config.RF_TICK_CENTRIFUGE.get());
            ++time[i];
            processCompleted[i] = time[i] >= getRecipeTime(i);
            this.dirty = true;
        } else {
            resetProcess(i);
        }
    }

    protected void consumeInput(int i) {
        ItemStack combInput = itemStackHandler.getStackInSlot(honeycombSlots[i]);
        JsonElement count = recipes.get(i).ingredient.toJson().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
        int inputAmount = count != null ? count.getAsInt() : 1;
        combInput.shrink(inputAmount);
    }

    protected boolean canStartCentrifugeProcess(int i) {
        if (!isProcessing[i] && !itemStackHandler.getStackInSlot(honeycombSlots[i]).isEmpty() && canProcessRecipe(i)) {
            ItemStack combInput = itemStackHandler.getStackInSlot(honeycombSlots[i]);
            JsonElement count = recipes.get(i).ingredient.toJson().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
            int inputAmount = count != null ? count.getAsInt() : 1;

            return combInput.getCount() >= inputAmount;
        }
        return false;
    }

    protected boolean canProcessRecipe(int i) {
        return recipes.get(i) != null && !recipes.get(i).multiblock;
    }

    protected boolean canProcess(int i) {
        return !itemStackHandler.getStackInSlot(honeycombSlots[i]).isEmpty() && canProcessFluid(i) && canProcessEnergy();
    }

    //TODO Should MainOutput Fluids not be voided?
    protected boolean canProcessFluid(int i) {
        return tanksHasSpace(recipes.get(i));
    }

    protected boolean canProcessEnergy() {
        return energyStorage.getEnergyStored() >= Config.RF_TICK_CENTRIFUGE.get();
    }

    public CentrifugeRecipe getRecipe(int i) {
        ItemStack input = itemStackHandler.getStackInSlot(honeycombSlots[i]);
        Inventory recipeInv = new Inventory(input, itemStackHandler.getStackInSlot(BOTTLE_SLOT));
        if (input.isEmpty() || input == failedMatch) return null;
        if (level != null) {
            boolean matches = this.recipes.get(i) == null;
            if (!matches) matches = !this.recipes.get(i).matches(recipeInv, level);
            if (matches) {
                CentrifugeRecipe rec = level.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, recipeInv, this.level).orElse(null);
                if (rec == null) failedMatch = input;
                else failedMatch = ItemStack.EMPTY;
                this.recipes.set(i, rec);
            }
            return this.recipes.get(i);
        }
        return null;
    }

    public int getProcessTime(int i) {
        return time[i];
    }

    public int getTotalSlots() {
        return 1 + honeycombSlots.length + outputSlots.length;
    }

    //Override this for subclasses
    public int getNumberOfInputs() {
        return 1;
    }

    public int getTotalTanks() {
        return 1 + honeycombSlots.length;
    }

    public int getMaxTankCapacity() {
        return 5000;
    }

    public int getRecipeTime(int i) {
        return getRecipe(i) != null ? Math.max(5, getRecipe(i).time) : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get();
    }
    //endregion

    protected void depositItemStacks(List<ItemStack> itemStacks) {
        itemStacks.forEach(itemStack -> {
            int slotIndex = outputSlots[0];
            while (!itemStack.isEmpty() && slotIndex < itemStackHandler.getSlots()) {
                ItemStack slotStack = itemStackHandler.getStackInSlot(slotIndex);

                int itemMaxStackSize = itemStack.getMaxStackSize();

                if (slotStack.isEmpty()) {
                    itemStackHandler.setStackInSlot(slotIndex, itemStack.split(itemMaxStackSize));
                } else if (consideredTheSameItem(itemStack, slotStack) && slotStack.getCount() != itemMaxStackSize) {
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

    protected boolean inventoryHasSpace(CentrifugeRecipe recipe) {
        int emptySlots = 0;

        for (int i = outputSlots[0]; i < itemStackHandler.getSlots(); ++i) {
            if (itemStackHandler.getStackInSlot(i).isEmpty()) {
                emptySlots++;
            }
        }

        boolean hasSpace = true;
        int i = 0;
        while (recipe != null && hasSpace && i < recipe.itemOutputs.size()) {
            ItemStack output = recipe.itemOutputs.get(i).getLeft();
            if (!output.isEmpty() && !(i == 2 && itemStackHandler.getStackInSlot(BOTTLE_SLOT).isEmpty())) {
                int count = output.getCount();
                int j = outputSlots[0];

                while (count > 0 && j < itemStackHandler.getSlots()) {
                    ItemStack slotStack = itemStackHandler.getStackInSlot(j);

                    if (slotStack.isEmpty() && emptySlots != 0) {
                        count -= Math.min(count, output.getMaxStackSize());
                        emptySlots--;
                    } else if (consideredTheSameItem(output, slotStack) && slotStack.getCount() != output.getMaxStackSize()) {
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

    public void drainFluidInTank(int tank) {
        fluidTanks.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE, tank);
    }

    public void updateRequiresRedstone() {
        this.requiresRedstone = !requiresRedstone;
    }

    public boolean getRequiresRedstone() {
        return requiresRedstone;
    }

    public void setRequiresRedstone(boolean requiresRedstone) {
        this.requiresRedstone = requiresRedstone;
    }

    public void setIsPoweredByRedstone(boolean isPoweredByRedstone) {
        this.isPoweredByRedstone = isPoweredByRedstone;
    }

    protected void setPoweredBlockState(boolean powered) {
        if (level != null) level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CentrifugeBlock.PROPERTY_ON, powered));
    }

    //region NBT
    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);
        return saveToNBT(tag);
    }

    protected CompoundNBT saveToNBT(CompoundNBT tag) {
        tag.put(NBTConstants.NBT_INVENTORY, itemStackHandler.serializeNBT());
        tag.putIntArray("time", time);
        tag.put("energy", energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_TANKS, fluidTanks.writeToNBT());
        tag.put("isProcessing", NBTUtils.writeBooleans(isProcessing));
        tag.put("processCompleted", NBTUtils.writeBooleans(processCompleted));
        tag.putBoolean("requiresRedstone", requiresRedstone);
        tag.putBoolean("isPoweredByRedstone", isPoweredByRedstone);
        return tag;
    }

    protected void loadFromNBT(CompoundNBT tag) {
        itemStackHandler.deserializeNBTWithoutCheckingSize(tag.getCompound(NBTConstants.NBT_INVENTORY));
        time = NBTUtils.getFallbackIntArray("time", tag, getNumberOfInputs());
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        fluidTanks.readFromNBT(tag);
        isProcessing = NBTUtils.loadBooleans(honeycombSlots.length, tag.getCompound("isProcessing"));
        processCompleted = NBTUtils.loadBooleans(honeycombSlots.length, tag.getCompound("processCompleted"));
        requiresRedstone = tag.getBoolean("requiresRedstone");
        isPoweredByRedstone = tag.getBoolean("isPoweredByRedstone");
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        this.loadFromNBT(tag);
        super.load(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, saveToNBT(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
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
        return (slot, stack, automation) -> {
            boolean isInputSlot = slot <= getNumberOfInputs();
            boolean isBottleSlot = slot == BOTTLE_SLOT;
            return !automation || (isInputSlot && !isBottleSlot && !stack.getItem().equals(Items.GLASS_BOTTLE)) || (isBottleSlot && stack.getItem().equals(Items.GLASS_BOTTLE));
        };
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot > getNumberOfInputs();
    }
    //endregion

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        assert level != null;
        return new CentrifugeContainer(ModContainers.CENTRIFUGE_CONTAINER.get(), id, level, worldPosition, playerInventory, times);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.centrifuge");
    }

    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MAX_CENTRIFUGE_RF.get(), Config.MAX_CENTRIFUGE_RECEIVE_RATE.get(), 0) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

            for (int i = 0; i < fluidTanks.getTanks(); i++) {
                buffer.writeFluidStack(fluidTanks.getFluidInTank(i));
            }

            buffer.writeInt(energyStorage.getEnergyStored());

            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        for (int i = 0; i < fluidTanks.getTanks(); i++) {
            fluidTanks.setFluidInTank(i, buffer.readFluidStack());
        }
        energyStorage.setEnergy(buffer.readInt());
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return CentrifugeTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return CentrifugeTileEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
