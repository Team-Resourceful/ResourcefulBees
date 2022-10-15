package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.OutputLocationGroup;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;
import net.roguelogix.phosphophyllite.multiblock2.common.ITickablePartsMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.validated.IValidatedMultiblock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

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
    private int processTime; //ticks left to complete processed recipe
    private int processEnergy; //energy needed per tick for processed recipe
    private int processQuantity; //# of inputs being currently being processed
    private ProcessStage processStage = ProcessStage.IDLE;

    public CentrifugeInputEntity(RegistryObject<BlockEntityType<CentrifugeInputEntity>> entityType, CentrifugeTier tier, BlockPos pos, BlockState state) {
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
        return new CentrifugeInputContainer(id, playerInventory, this, centrifugeState);
    }

    public int getRecipeTime() {
        return processRecipe == null ? 0 : Math.max(1, Mth.floor(processRecipe.time() * controller().getRecipeTimeModifier()));
    }

    private void setProcessStage(ProcessStage newStage) {
        //TODO currently this does not get updated in real-time on the client
        // ideally this would not send a full packet of data and would instead only send a packet containing
        // the changed data to players that are actively tracking the block, thus reducing the size, number, and frequency of packets being sent.
        // see read/write nbt regarding amount of data being sent
        processStage = newStage;
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
            if (processStage.isIdle() && canProcess()) startProcess();
            if (processStage.isProcessing()) processRecipe();
            if (processStage.isFinalizing()) depositResults();
            if (processStage.isCompleted()) processCompleted();
        }
    }

    private boolean canProcess() {
        //add check for output location groups to verify all outputs are linked in order to function
        return filterRecipe != null && consumeInputs(true);
    }

    private void startProcess() {
        setProcessStage(ProcessStage.PROCESSING);
        processRecipe = filterRecipe;
        processRecipeID = processRecipe.getId();
        processTime = getRecipeTime();
        processEnergy = processRecipe.energyPerTick() * this.controller().getRecipePowerModifier();
        consumeInputs(false);
        setChanged();
    }

    private boolean consumeInputs(boolean simulate) {
        if (filterRecipe == null) {
            processCompleted();
            return false;
        }

        Ingredient ingredient = filterRecipe.ingredient();
        int needed = simulate ? filterRecipe.getInputAmount() * controller().getMaxInputRecipes() : processQuantity;
        int collected = 0;

        for (int slot = 0; collected < needed && slot < inventoryHandler.getSlots(); slot++) {
            ItemStack stackInSlot = inventoryHandler.getStackInSlot(slot);
            if (stackInSlot == ItemStack.EMPTY || !ingredient.test(stackInSlot)) continue;
            int found = Math.min(needed-collected, stackInSlot.getCount());
            collected += found;
            if (!simulate) stackInSlot.shrink(found);
        }

        if (simulate) processQuantity = collected - collected % filterRecipe.getInputAmount();

        return collected >= filterRecipe.getInputAmount();
    }

    private void processRecipe() {
        if (this.controller().getEnergyStorage().consumeEnergy(processEnergy, false)) {
            processTime--;
            if (processTime == 0) setProcessStage(ProcessStage.FINALIZING);
        }
    }

    //TODO determine an optimal way of rolling outputs based on process quantity vs just multiplying them
    // - low priority and not necessary for release
    private void depositResults() {
        if (processRecipe == null || depositResults(processRecipe.itemOutputs(), itemOutputs) && depositResults(processRecipe.fluidOutputs(), fluidOutputs)) {
            setProcessStage(ProcessStage.COMPLETED);
        }
    }

    // Downside to this method is the result is not cached therefore it is randomized each time this method gets called
    private <T extends AbstractOutput<E>, A extends AbstractCentrifugeOutputEntity<T, E>, E> boolean depositResults(List<CentrifugeRecipe.Output<T, E>> recipeOutputs, OutputLocationGroup<A, T, E> outputLocationGroup) {
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
        processEnergy = 0;
        itemOutputs.resetProcessData();
        fluidOutputs.resetProcessData();
        setProcessStage(ProcessStage.IDLE);
    }

    public void updateRecipe() {
        filterRecipe = CentrifugeUtils.getRecipe(level, filterInventory.getStackInSlot(RECIPE_SLOT)).orElse(null);
        filterRecipeID =  filterRecipe == null ? null : filterRecipe.getId();
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

    //TODO change data syncing to reduce packet bloating if possible
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        inventoryHandler.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_FILTER_INVENTORY));
        processStage = ProcessStage.valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
        processTime = tag.getInt(NBTConstants.NBT_PROCESS_TIME);
        processEnergy = tag.getInt(NBTConstants.NBT_PROCESS_ENERGY);
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
        tag.putInt(NBTConstants.NBT_PROCESS_TIME, processTime);
        tag.putInt(NBTConstants.NBT_PROCESS_ENERGY, processEnergy);
        if (processRecipe != null && processRecipeID != null) tag.putString(NBTConstants.NBT_PROCESS_RECIPE, processRecipeID.toString());
        if (filterRecipe != null && filterRecipeID != null) tag.putString(NBTConstants.NBT_FILTER_RECIPE, filterRecipeID.toString());
        tag.put(NBTConstants.NBT_ITEM_OUTPUTS, itemOutputs.serialize());
        tag.put(NBTConstants.NBT_FLUID_OUTPUTS, fluidOutputs.serialize());
        return tag;
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
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private class FilterInventory extends AbstractFilterItemHandler {

        public FilterInventory(int numSlots) {
            super(numSlots);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return CentrifugeUtils.getRecipe(level, stack).isPresent();
        }
    }

    private class InventoryHandler extends ItemStackHandler {

        protected InventoryHandler(int slots) {
            super(slots);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return filterRecipe != null && filterRecipe.ingredient().test(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
