package com.teamresourceful.resourcefulbees.centrifuge.common.entities;

import com.teamresourceful.resourcefulbees.centrifuge.common.CentrifugeController;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.OutputLocationGroup;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.ProcessContainerData;
import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.phosphophyllite.multiblock2.common.ITickablePartsMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.validated.IValidatedMultiblock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class CentrifugeInputEntity extends AbstractGUICentrifugeEntity implements ITickablePartsMultiblock.Tickable {

    public static final int RECIPE_SLOT = 0;

    private final OutputLocationGroup<CentrifugeItemOutputEntity, ItemOutput, ItemStack> itemOutputs = new OutputLocationGroup<>();
    private final OutputLocationGroup<CentrifugeFluidOutputEntity, FluidOutput, FluidStack> fluidOutputs = new OutputLocationGroup<>();
    private final FilterInventory filterInventory = new FilterInventory(1);
    private final InventoryHandler inventoryHandler;
    private final LazyOptional<IItemHandler> lazyOptional;
    private CentrifugeRecipe filterRecipe = null; //recipe in the recipe slot
    private ResourceLocation filterRecipeID = null;//needed to load the recipe on world load for client
    private CentrifugeRecipe processRecipe = null; //recipe currently being processed
    private ResourceLocation processRecipeID = null; //needed to load the recipe on world load
    private CentrifugeRecipe tempRecipe = null; //used when filter is not set
    private int processQuantity; //# of inputs being currently being processed
    private ProcessStage processStage = ProcessStage.IDLE;
    private final ProcessContainerData processData = new ProcessContainerData(); //contains synchronized data - time and energy

    public CentrifugeInputEntity(Supplier<BlockEntityType<CentrifugeInputEntity>> entityType, CentrifugeTier tier, BlockPos pos, BlockState state) {
        super(entityType.get(), tier, pos, state);
        this.inventoryHandler = new InventoryHandler(tier.getSlots());
        this.lazyOptional = LazyOptional.of(() -> inventoryHandler);
    }

    public InventoryHandler getInventoryHandler() {
        return this.inventoryHandler;
    }

    public FilterInventory getFilterInventory() {
        return filterInventory;
    }

    @Nullable
    public ResourceLocation getFilterRecipeID() {
        return filterRecipeID;
    }

    @Nullable
    public ResourceLocation getProcessRecipeID() {
        return processRecipeID;
    }

    public ProcessStage getProcessStage() {
        return processStage;
    }

    public ProcessContainerData getProcessData() {
        return processData;
    }

    public OutputLocationGroup<CentrifugeItemOutputEntity, ItemOutput, ItemStack> getItemOutputs() {
        return itemOutputs;
    }

    public OutputLocationGroup<CentrifugeFluidOutputEntity, FluidOutput, FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    //todo figure out how to eliminate this cast
    public <A extends AbstractCentrifugeOutputEntity<T, E>, T extends AbstractOutput<E>, E> OutputLocationGroup<A,T,E> getOutputLocationGroup(CentrifugeOutputType outputType) {
        //noinspection unchecked
        return outputType.isItem() ? (OutputLocationGroup<A, T, E>) itemOutputs : (OutputLocationGroup<A, T, E>) fluidOutputs;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("gui.centrifuge.input.item." + tier.getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        CentrifugeController controller = nullableController();
        if (controller == null) return null;
        return new CentrifugeInputContainer(id, playerInventory, this, centrifugeState, controller.getEnergyStorage());
    }

    public int getRecipeTime() {
        return processRecipe == null ? 0 : Math.max(1, Mth.floor(processRecipe.time() * controller().getRecipeTimeModifier()));
    }

    private void setProcessStage(ProcessStage newStage) {
        if (level == null) return;
        processStage = newStage;
        sendToPlayersTrackingChunk();
        tickProcess();
    }

    @Override
    public void preTick() {
        //TODO Remove this when Rogue changes tick api in phos
    }

    @Override
    public void postTick() {
        var controller = nullableController();
        if (controller == null || controller.assemblyState() != IValidatedMultiblock.AssemblyState.ASSEMBLED) return;
        if (level != null && !level.isClientSide) {
            tickProcess();
        }
    }

    private void tickProcess() {
        switch (processStage) {
            case IDLE -> { if (canProcess()) startProcess(); }
            case PROCESSING -> processRecipe();
            case FINALIZING -> depositResults();
            case COMPLETED -> processCompleted();
        }
    }

    //TODO figure out a clean way to combine filter recipe and temp recipe to reduce clutter
    private boolean canProcess() {
        if (!itemOutputs.allLinked() && !fluidOutputs.allLinked()) return false;
        if (filterRecipe != null) return inventoryHandler.consumeInputs(true, filterRecipe);
        if (tempRecipe == null || !inventoryHandler.consumeInputs(true, tempRecipe)) getTempRecipe();
        return tempRecipe != null && inventoryHandler.consumeInputs(true, tempRecipe);
    }

    private void startProcess() {
        CentrifugeRecipe recipe = pickRecipe();
        if (recipe == null) {
            processCompleted();
            return;
        }
        processRecipe = recipe;
        processRecipeID = processRecipe.getId();
        processData.setTime(getRecipeTime());
        processData.setEnergy((int) (processRecipe.energyPerTick() * this.controller().getRecipePowerModifier()));
        inventoryHandler.consumeInputs(false, processRecipe);
        setChanged();
        //setProcessStage is now called last due to the sync packet being sent and re-ticking the process
        // this prevents certain information from being out of sync or delayed by a tick on the client
        setProcessStage(ProcessStage.PROCESSING);
    }

    private void processRecipe() {
        if (this.controller().getEnergyStorage().consumeEnergy(processData.getEnergy(), false)) {
            processData.decreaseTime();
            if (processData.getTime() == 0) setProcessStage(ProcessStage.FINALIZING);
        }
    }

    //TODO determine an optimal way of rolling outputs based on process quantity vs just multiplying them
    // - low priority and not necessary for release
    private void depositResults() {
        if (processRecipe == null || depositResults(processRecipe.itemOutputs(), itemOutputs) && depositResults(processRecipe.fluidOutputs(), fluidOutputs)) {
            setProcessStage(ProcessStage.COMPLETED);
        }
    }

    /*  Downside to this method is the result is not cached, therefore it is randomized each time this method gets called
     *  to further elaborate since I'm an idiot:
     *  unless the chosen recipe results are cached even if they could not be successfully deposited,
     *  then the recipe will eventually complete. This could take 1 tick, or it could take 100 ticks
     *  it just depends on RNG. This is not intentional and consideration for caching should probably be done
     *  as it theoretically could make the void blocks somewhat useless
     */
    private <T extends AbstractOutput<E>,A extends AbstractCentrifugeOutputEntity<T, E>, E> boolean depositResults(List<CentrifugeRecipe.Output<T, E>> recipeOutputs, OutputLocationGroup<A, T, E> outputLocationGroup) {
        for (int i = 0; i < recipeOutputs.size(); i++) {
            CentrifugeRecipe.Output<T, E> recipeOutput = recipeOutputs.get(i);
            if (recipeOutput.chance() >= MathUtils.RANDOM.nextFloat()
                && !outputLocationGroup.depositResult(i, recipeOutput.getRandomResult(), processQuantity)) {
                return false;
            }
        }
        return true;
    }

    private void processCompleted() {
        processRecipe = null;
        processRecipeID = null;
        processQuantity = 0;
        processData.reset();
        itemOutputs.resetProcessData();
        fluidOutputs.resetProcessData();
        setProcessStage(ProcessStage.IDLE);
    }

    public void updateRecipe() {
        filterRecipe = CentrifugeUtils.getRecipe(level, filterInventory.getStackInSlot(RECIPE_SLOT)).orElse(null);
        filterRecipeID =  filterRecipe == null ? null : filterRecipe.getId();
    }

    private void getTempRecipe() {
        inventoryHandler.findFirst().ifPresent(itemStack -> tempRecipe = CentrifugeUtils.getRecipe(level, itemStack).orElse(null));
    }

    private CentrifugeRecipe pickRecipe() {
        if (filterRecipe != null) return filterRecipe;
        if (tempRecipe != null) return tempRecipe;
        return null;
    }

    //region CAPABILITIES
    @NotNull
    @Override
    public <T> LazyOptional<T> capability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(ForgeCapabilities.ITEM_HANDLER) ? lazyOptional.cast() : super.capability(cap, side);
    }
    //endregion

    //region NBT HANDLING
    @Override
    public void onAdded() {
        assert level != null;
        filterRecipe = (CentrifugeRecipe) level.getRecipeManager().byKey(filterRecipeID).orElse(null);
        processRecipe = (CentrifugeRecipe) level.getRecipeManager().byKey(processRecipeID).orElse(null);
    }

    //TODO change data syncing to reduce packet bloating if possible ->
    // come up with a better system for conditionally reading/writing sync data to be able
    // to have a single packet type that accepts a list of items to be synced
    // This would allow for a singular point of execution while handling a dynamic number of items
    // that need to be synced. For example: if I want to send a packet that syncs only processStage,
    // but later I want to send a packet that syncs itemOutputs, or fluidOutputs, or perhaps I want to
    // sync everything at once.
    // -- alternative option is to have a sync packet that gets sent on an interval. then we could just add to the packet
    // whenever data gets changed and needs to be synced. for example: I update processStage so I add to the packet the change,
    // but before the packet gets sent another data point gets changed and needs to be synced so that gets added, now the packet
    // has two data points that need to be synced and now the packet gets sent, then next packet that gets sent only one data
    // point needed to be updated, etc.
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        inventoryHandler.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_FILTER_INVENTORY));
        processStage = ProcessStage.deserialize(tag);
        processData.setTime(tag.getInt(NBTConstants.NBT_PROCESS_TIME));
        processData.setEnergy(tag.getInt(NBTConstants.NBT_PROCESS_ENERGY));
        if (tag.contains(NBTConstants.NBT_PROCESS_RECIPE)) processRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_PROCESS_RECIPE));
        if (tag.contains(NBTConstants.NBT_FILTER_RECIPE)) filterRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_FILTER_RECIPE));
        itemOutputs.deserialize(tag.getCompound(NBTConstants.NBT_ITEM_OUTPUTS), CentrifugeItemOutputEntity.class, this::getLevel);
        fluidOutputs.deserialize(tag.getCompound(NBTConstants.NBT_FLUID_OUTPUTS), CentrifugeFluidOutputEntity.class, this::getLevel);
        super.readNBT(tag);
    }

    @NotNull
    @Override
    protected CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inventoryHandler.serializeNBT());
        tag.put(NBTConstants.NBT_FILTER_INVENTORY, filterInventory.serializeNBT());
        tag.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString().toLowerCase(Locale.ROOT));
        tag.putInt(NBTConstants.NBT_PROCESS_TIME, processData.getTime());
        tag.putInt(NBTConstants.NBT_PROCESS_ENERGY, processData.getEnergy());
        if (processRecipe != null && processRecipeID != null) tag.putString(NBTConstants.NBT_PROCESS_RECIPE, processRecipeID.toString());
        if (filterRecipe != null && filterRecipeID != null) tag.putString(NBTConstants.NBT_FILTER_RECIPE, filterRecipeID.toString());
        tag.put(NBTConstants.NBT_ITEM_OUTPUTS, itemOutputs.serialize());
        tag.put(NBTConstants.NBT_FLUID_OUTPUTS, fluidOutputs.serialize());
        return tag;
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = super.getSyncData();
        tag.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString().toLowerCase(Locale.ROOT));
        tag.put(NBTConstants.NBT_ITEM_OUTPUTS, itemOutputs.serialize());
        tag.put(NBTConstants.NBT_FLUID_OUTPUTS, fluidOutputs.serialize());
        if (processRecipe != null && processRecipeID != null) tag.putString(NBTConstants.NBT_PROCESS_RECIPE, processRecipeID.toString());
        if (filterRecipe != null && filterRecipeID != null) tag.putString(NBTConstants.NBT_FILTER_RECIPE, filterRecipeID.toString());
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        if (tag.contains(NBTConstants.NBT_PROCESS_STAGE)) processStage = ProcessStage.deserialize(tag);
        itemOutputs.deserialize(tag.getCompound(NBTConstants.NBT_ITEM_OUTPUTS), CentrifugeItemOutputEntity.class, this::getLevel);
        fluidOutputs.deserialize(tag.getCompound(NBTConstants.NBT_FLUID_OUTPUTS), CentrifugeFluidOutputEntity.class, this::getLevel);
        if (tag.contains(NBTConstants.NBT_PROCESS_RECIPE)) processRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_PROCESS_RECIPE));
        if (tag.contains(NBTConstants.NBT_FILTER_RECIPE)) filterRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_FILTER_RECIPE));
    }

    //endregion

    public void linkOutput(CentrifugeOutputType outputType, int recipeOutputSlot, BlockPos outputPos) {
        if (recipeOutputSlot < 0 || recipeOutputSlot > 2) return;
        var controller = nullableController();
        if (controller == null) return;
        var outputMap = controller.getOutputsByType(outputType);
        if (!outputMap.containsKey(outputPos)) return;
        linkOutput(recipeOutputSlot, outputPos, outputMap.get(outputPos), getOutputLocationGroup(outputType));
    }

    private <A extends AbstractCentrifugeOutputEntity<T, E>, T extends AbstractOutput<E>, E> void linkOutput(int recipeOutputSlot, BlockPos outputPos, A output, OutputLocationGroup<A, T, E> outputLocationGroup) {
        outputLocationGroup.set(recipeOutputSlot, output, outputPos);
        if (level != null && !level.isClientSide) {
            this.sendToPlayersTrackingChunk();
        }
    }

    private class FilterInventory extends AbstractFilterItemHandler {

        public FilterInventory(int numSlots) {
            super(numSlots);
        }

        private Optional<CentrifugeRecipe> cachedRecipe = Optional.empty();

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return cachedRecipe.isPresent();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            cachedRecipe = CentrifugeUtils.getRecipe(level, stack);
            if (cachedRecipe.isPresent()) {
                ItemStack itemStack = stack.copy();
                itemStack.setCount(cachedRecipe.get().inputAmount());
                stacks.set(slot, itemStack);
            } else {
                super.setStackInSlot(slot, stack);
            }
        }
    }

    private class InventoryHandler extends ItemStackHandler {

        protected InventoryHandler(int slots) {
            super(slots);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return filterRecipe == null || filterRecipe.ingredient().test(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        private boolean consumeInputs(boolean simulate, @NotNull CentrifugeRecipe recipe) {
            Ingredient ingredient = recipe.ingredient();
            int needed = simulate ? recipe.inputAmount() * controller().getMaxInputRecipes() : processQuantity;
            int collected = 0;
            int remaining = needed;

            for (ItemStack stackInSlot : stacks) {
                if (collected >= needed) break;
                if (stackInSlot.isEmpty() || !ingredient.test(stackInSlot)) continue;
                int found = Math.min(remaining, stackInSlot.getCount());
                collected += found;
                remaining -= found;
                if (!simulate) stackInSlot.shrink(found);
            }

            if (simulate) processQuantity = collected - collected % recipe.inputAmount();

            return collected >= recipe.inputAmount();
        }

        private Optional<ItemStack> findFirst() {
            return stacks.stream().filter(itemStack -> !itemStack.isEmpty()).findFirst();
        }
    }
}
