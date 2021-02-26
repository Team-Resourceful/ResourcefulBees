
package com.resourcefulbees.resourcefulbees.tileentity;

import com.google.gson.JsonElement;
import com.resourcefulbees.resourcefulbees.block.CentrifugeBlock;
import com.resourcefulbees.resourcefulbees.capabilities.CustomEnergyStorage;
import com.resourcefulbees.resourcefulbees.capabilities.MultiFluidTank;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.CentrifugeContainer;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
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
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.inventory.container.Container.areItemsAndTagsEqual;

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
        public int get(int i) { return time[0]; }

        @Override
        public void set(int i, int i1) { time[0] = i1; }
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


    public int[] getHoneycombSlots() { return this.honeycombSlots; }

    public int[] getOutputSlots() { return this.outputSlots; }

    public AutomationSensitiveItemStackHandler getItemStackHandler() { return this.itemStackHandler; }

    @Override
    public void tick() {
        if (world != null && !world.isRemote()) {
            if (!requiresRedstone || isPoweredByRedstone) {
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
                        completedProcess(i);
                    }
                }
                if (dirty) {
                    this.dirty = false;
                    this.markDirty();
                }
            }
        }
    }

    protected void resetProcess(int i) {
        processCompleted[i] = false;
        isProcessing[i] = false;
        time[i] = 0;
        recipes.set(i, null);
        setPoweredBlockState(false);
    }

    //TODO see if this can be shortened and optimized further
    protected void completedProcess(int i) {
        if (recipes.get(i) != null) {
            if (inventoryHasSpace(recipes.get(i))) {
                consumeInput(i);
                ItemStack glassBottle = itemStackHandler.getStackInSlot(BOTTLE_SLOT);
                List<ItemStack> depositStacks = new ArrayList<>();
                if (world != null) {
                    for (int j = 0; j < 3; j++) {
                        float nextFloat = world.rand.nextFloat();
                        float chance;
                        switch (j) {
                            case 0:
                                if (recipes.get(i).hasFluidOutput) {
                                    chance = recipes.get(i).fluidOutput.get(0).getRight();
                                    if (chance >= nextFloat) {
                                        fluidTanks.fill(i + 1, recipes.get(i).fluidOutput.get(0).getLeft().copy(), MultiFluidTank.FluidAction.EXECUTE);
                                    }
                                    depositStacks.add(ItemStack.EMPTY);
                                } else {
                                    chance = recipes.get(i).itemOutputs.get(j).getRight();
                                    if (chance >= nextFloat) {
                                        depositStacks.add(recipes.get(i).itemOutputs.get(j).getLeft().copy());
                                    } else {
                                        depositStacks.add(ItemStack.EMPTY);
                                    }
                                }
                                break;
                            case 1:
                                chance = recipes.get(i).itemOutputs.get(j).getRight();
                                if (chance >= nextFloat) {
                                    depositStacks.add(recipes.get(i).itemOutputs.get(j).getLeft().copy());
                                } else {
                                    depositStacks.add(ItemStack.EMPTY);
                                }
                                break;
                            case 2:
                                if (glassBottle.isEmpty() || glassBottle.getCount() < recipes.get(i).itemOutputs.get(j).getLeft().getCount()) {
                                    fluidTanks.fill(0, new FluidStack(ModFluids.HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE), MultiFluidTank.FluidAction.EXECUTE);
                                    depositStacks.add(ItemStack.EMPTY);
                                } else {
                                    chance = recipes.get(i).itemOutputs.get(j).getRight();
                                    if (chance >= nextFloat) {
                                        glassBottle.shrink(recipes.get(i).itemOutputs.get(j).getLeft().getCount());
                                        depositStacks.add(recipes.get(i).itemOutputs.get(j).getLeft().copy());
                                    } else {
                                        depositStacks.add(ItemStack.EMPTY);
                                    }
                                }
                                break;
                            default: //do nothing
                        }
                    }
                    if (!depositStacks.isEmpty()) {
                        depositItemStacks(depositStacks);
                    }
                }
                resetProcess(i);
            }
        } else {
            resetProcess(i);
        }
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
        JsonElement count = recipes.get(i).ingredient.serialize().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
        int inputAmount = count != null ? count.getAsInt() : 1;
        combInput.shrink(inputAmount);
    }

    protected boolean canStartCentrifugeProcess(int i) {
        if (!isProcessing[i] && !itemStackHandler.getStackInSlot(honeycombSlots[i]).isEmpty() && canProcessRecipe(i)) {
            ItemStack combInput = itemStackHandler.getStackInSlot(honeycombSlots[i]);
            JsonElement count = recipes.get(i).ingredient.serialize().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
            int inputAmount = count != null ? count.getAsInt() : 1;

            return combInput.getCount() >= inputAmount ;
        }
        return false;
    }

    protected boolean canProcessRecipe(int i) { return recipes.get(i) != null && !recipes.get(i).multiblock; }

    protected boolean canProcess(int i) { return !itemStackHandler.getStackInSlot(honeycombSlots[i]).isEmpty() && canProcessFluid(i) && canProcessEnergy(); }

    //TODO Should MainOutput Fluids not be voided?
    protected boolean canProcessFluid(int i) { return true; }

    protected boolean canProcessEnergy(){ return energyStorage.getEnergyStored() >= Config.RF_TICK_CENTRIFUGE.get(); }

    public CentrifugeRecipe getRecipe(int i) {
        ItemStack input = itemStackHandler.getStackInSlot(honeycombSlots[i]);
        if (input.isEmpty() || input == failedMatch || world == null) {
            return null;
        }
        if (this.recipes.get(i) == null || !this.recipes.get(i).matches(new RecipeWrapper(itemStackHandler), world)) {
            Inventory recipeInv = new Inventory(input);
            CentrifugeRecipe rec = world.getRecipeManager().getRecipe(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, recipeInv, this.world).orElse(null);
            if (rec == null) failedMatch = input;
            else failedMatch = ItemStack.EMPTY;
            this.recipes.set(i, rec);
        }
        return this.recipes.get(i);
    }

    public int getProcessTime(int i) { return time[i]; }

    public int getTotalSlots() { return 1 + honeycombSlots.length + outputSlots.length;}

    //Override this for subclasses
    public int getNumberOfInputs() { return 1; }

    public int getTotalTanks() { return 1 + honeycombSlots.length; }

    public int getMaxTankCapacity() { return 5000; }

    public int getRecipeTime(int i) { return getRecipe(i) != null ? Math.max(5, getRecipe(i).time) : Config.GLOBAL_CENTRIFUGE_RECIPE_TIME.get(); }
    //endregion

    protected void depositItemStacks(List<ItemStack> itemStacks) {
        itemStacks.forEach(itemStack -> {
            int slotIndex = outputSlots[0];
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

    public void drainFluidInTank(int tank) { fluidTanks.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE, tank); }

    public void updateRequiresRedstone() { this.requiresRedstone = !requiresRedstone; }

    public boolean getRequiresRedstone() { return requiresRedstone; }

    public void setRequiresRedstone(boolean requiresRedstone) { this.requiresRedstone = requiresRedstone; }

    public void setIsPoweredByRedstone(boolean isPoweredByRedstone) { this.isPoweredByRedstone = isPoweredByRedstone; }

    protected void setPoweredBlockState(boolean powered) { if (world != null) world.setBlockState(pos, getBlockState().with(CentrifugeBlock.PROPERTY_ON, powered)); }

    //region NBT
    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tag) {
        super.write(tag);
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
        assert world != null;
        return new CentrifugeContainer(ModContainers.CENTRIFUGE_CONTAINER.get(), id, world, pos, playerInventory, times);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() { return new TranslationTextComponent("gui.resourcefulbees.centrifuge"); }

    protected CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.MAX_CENTRIFUGE_RF.get(), Config.MAX_CENTRIFUGE_RECEIVE_RATE.get(), 0) {
            @Override
            protected void onEnergyChanged() { markDirty(); }
        };
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

            for (int i = 0; i < fluidTanks.getTanks(); i++) {
                buffer.writeFluidStack(fluidTanks.getFluidInTank(i));
            }

            buffer.writeInt(energyStorage.getEnergyStored());

            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.pos, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        for (int i = 0; i < fluidTanks.getTanks(); i++) {
            fluidTanks.setFluidInTank(i, buffer.readFluidStack());
        }
        energyStorage.setEnergy(buffer.readInt());
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots) { super(slots); }

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
            markDirty();
        }
    }
}

