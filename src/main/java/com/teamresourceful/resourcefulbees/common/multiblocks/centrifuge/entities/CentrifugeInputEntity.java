package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.OutputLocations;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.RegistryObject;
import net.roguelogix.phosphophyllite.multiblock.IOnAssemblyTile;
import net.roguelogix.phosphophyllite.multiblock.ITickableMultiblockTile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class CentrifugeInputEntity extends AbstractGUICentrifugeEntity implements ITickableMultiblockTile, IOnAssemblyTile {

    public static final int RECIPE_SLOT = 0;

    private final OutputLocations<CentrifugeItemOutputEntity> itemOutputs = new OutputLocations<>();
    private final OutputLocations<CentrifugeFluidOutputEntity> fluidOutputs = new OutputLocations<>();
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

    public OutputLocations<CentrifugeItemOutputEntity> getItemOutputs() {
        return itemOutputs;
    }

    public OutputLocations<CentrifugeFluidOutputEntity> getFluidOutputs() {
        return fluidOutputs;
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("gui.centrifuge.input.item." + tier.getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        controller().updateCentrifugeState(centrifugeState);
        return new CentrifugeInputContainer(id, playerInventory, this);
    }

    @Override
    protected void getOpenGUIPacket(FriendlyByteBuf buffer) {
        super.getOpenGUIPacket(buffer);
        buffer.writeInt(1);
        buffer.writeInt(tier.getSlots());
    }

    public int getRecipeTime() {
        return processRecipe == null ? 0 : Math.max(1, Mth.floor(processRecipe.getTime() * controller().getRecipeTimeModifier()));
    }

    private void setProcessStage(ProcessStage newStage) {
        processStage = newStage;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            if (processStage.equals(ProcessStage.IDLE) && canProcess()) startProcess();
            if (processStage.equals(ProcessStage.PROCESSING)) processRecipe();
            if (processStage.equals(ProcessStage.FINALIZING)) depositResults();
            if (processStage.equals(ProcessStage.COMPLETED)) processCompleted();
        }
    }

    private boolean canProcess() {
        return filterRecipe != null && consumeInputs(true);
    }

    private void startProcess() {
        setProcessStage(ProcessStage.PROCESSING);
        processRecipe = filterRecipe;
        processRecipeID = processRecipe.getId();
        processTime = getRecipeTime();
        processEnergy = processRecipe.getEnergyPerTick() * this.controller().getRecipePowerModifier();
        consumeInputs(false);
        setChanged();
    }

    private boolean consumeInputs(boolean simulate) {
        if (filterRecipe == null) {
            processCompleted();
            return false;
        }

        Ingredient ingredient = filterRecipe.getIngredient();
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
        if (processRecipe == null || depositResults(processRecipe.getItemOutputs(), itemOutputs) && depositResults(processRecipe.getFluidOutputs(), fluidOutputs)) {
            setProcessStage(ProcessStage.COMPLETED);
        }
    }

    private <T extends AbstractOutput, A extends BlockEntity & ICentrifugeOutput<T>> boolean depositResults(List<CentrifugeRecipe.Output<T>> recipeOutputs, OutputLocations<A> outputLocations) {
        for (int i = 0; i < recipeOutputs.size(); i++) {
            CentrifugeRecipe.Output<T> recipeOutput = recipeOutputs.get(i);
            if (recipeOutput.getChance() >= MathUtils.RANDOM.nextFloat()) {
                A location = outputLocations.get(i).getTile();
                if (location == null) return false;
                if (!outputLocations.isDeposited(i)) outputLocations.setDeposited(i, location.depositResult(recipeOutput, processQuantity));
                if (!outputLocations.isDeposited(i)) return false;
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
        return cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ? lazyOptional.cast() : super.capability(cap, side);
    }
    //endregion

    //region NBT HANDLING
    @Override
    public void onAdded() {
        if (level != null) {
            filterRecipe = (CentrifugeRecipe) ((RecipeManagerAccessorInvoker) level.getRecipeManager()).callByType(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE).get(filterRecipeID);
            processRecipe = (CentrifugeRecipe) ((RecipeManagerAccessorInvoker) level.getRecipeManager()).callByType(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE).get(processRecipeID);
            itemOutputs.onLoad(CentrifugeItemOutputEntity.class, level); //TODO remove this in 1.18
            fluidOutputs.onLoad(CentrifugeFluidOutputEntity.class, level); //TODO remove this in 1.18
        }
    }

    //TODO see below!
    //REQUIRED:: STOP BLOATING NETWORK PACKETS!! Client side containers should be populated with dummy ItemStackHandlers
    // . as vanilla will handle syncing of the data. therefore not every single datapoint needs to be written to nbt
    // . UpdatePackets and UpdateTags dont always need to read/write the same data!
    // . this means the container classes will need to be tweaked appropriately

    //Note about my attempt to solve the above issue: I need access to level and filter recipe, so I can perform
    //the proper testing against the itemstacks
    @Override
    protected void readNBT(@NotNull CompoundTag tag) {
        inventoryHandler.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_FILTER_INVENTORY));
        processStage = ProcessStage.valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
        processTime = tag.getInt(NBTConstants.NBT_PROCESS_TIME);
        processEnergy = tag.getInt(NBTConstants.NBT_PROCESS_ENERGY);
        if (tag.contains(NBTConstants.NBT_PROCESS_RECIPE)) processRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_PROCESS_RECIPE));
        if (tag.contains(NBTConstants.NBT_FILTER_RECIPE)) filterRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_FILTER_RECIPE));
        itemOutputs.deserialize(tag.getCompound(NBTConstants.NBT_ITEM_OUTPUTS));
        fluidOutputs.deserialize(tag.getCompound(NBTConstants.NBT_FLUID_OUTPUTS));
        //TODO remove this block in 1.18 when onLoad is called properly
        if (level != null && level.isClientSide) {
            filterRecipe = (CentrifugeRecipe) ((RecipeManagerAccessorInvoker) level.getRecipeManager()).callByType(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE).get(filterRecipeID);
            processRecipe = (CentrifugeRecipe) ((RecipeManagerAccessorInvoker) level.getRecipeManager()).callByType(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE).get(processRecipeID);
            itemOutputs.onLoad(CentrifugeItemOutputEntity.class, level);
            fluidOutputs.onLoad(CentrifugeFluidOutputEntity.class, level);
        }
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

    //TODO see the line below
    // REQUIRED remove this method and logic after implementing linking code in gui - this is only for dev/testing
    @Override
    public void onAssembly() {
        List<CentrifugeItemOutputEntity> iout = this.controller().getItemOutputs();
        itemOutputs.setFirst(iout.get(0), iout.get(0).getBlockPos());
        itemOutputs.setSecond(iout.get(1), iout.get(1).getBlockPos());
        itemOutputs.setThird(iout.get(2), iout.get(2).getBlockPos());
        List<CentrifugeFluidOutputEntity> fout = this.controller().getFluidOutputs();
        fluidOutputs.setFirst(fout.get(0), fout.get(0).getBlockPos());
        fluidOutputs.setSecond(fout.get(1), fout.get(1).getBlockPos());
        fluidOutputs.setThird(fout.get(2), fout.get(2).getBlockPos());
    }



    @Override
    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeInt(processTime);
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), serverPlayer);
        }
    }

    @Override
    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        processTime = buffer.readInt();
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
            return filterRecipe != null && filterRecipe.getIngredient().test(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
