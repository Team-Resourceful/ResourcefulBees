package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities;

import com.teamresourceful.resourcefulbees.api.beedata.outputs.AbstractOutput;
import com.teamresourceful.resourcefulbees.common.inventory.AbstractFilterItemHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.mixin.RecipeManagerAccessorInvoker;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeInputContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.OutputLocations;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.roguelogix.phosphophyllite.multiblock.generic.IOnAssemblyTile;
import net.roguelogix.phosphophyllite.multiblock.generic.ITickableMultiblockTile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CentrifugeInputEntity extends AbstractTieredCentrifugeEntity implements INamedContainerProvider, ITickableMultiblockTile, IOnAssemblyTile {

    //TODO need to handle condition where slots contain items and recipe has changed - *somewhere*
    // *MAYBE* only accept and process if recipe slot has recipe, if not then do all recipes.

    public static final int RECIPE_SLOT = 0;

    private final OutputLocations<CentrifugeItemOutputEntity> itemOutputs = new OutputLocations<>();
    private final OutputLocations<CentrifugeFluidOutputEntity> fluidOutputs = new OutputLocations<>();
    private final FilterInventory filterInventory = new FilterInventory(1);
    private final InventoryHandler inventoryHandler;
    private final LazyOptional<IItemHandler> lazyOptional;
    private CentrifugeRecipe filterRecipe = null; //recipe in the recipe slot
    private CentrifugeRecipe processRecipe = null; //recipe currently being processed
    private ResourceLocation processRecipeID = null; //needed to load the recipe on world load
    private int processTime; //ticks left to complete processed recipe
    private int processEnergy; //energy needed per tick for processed recipe
    private ProcessStage processStage = ProcessStage.IDLE;

    public CentrifugeInputEntity(RegistryObject<TileEntityType<CentrifugeInputEntity>> entityType, CentrifugeTier tier) {
        super(entityType.get(), tier);
        this.inventoryHandler = new InventoryHandler(tier.getSlots());
        this.lazyOptional = LazyOptional.of(() -> inventoryHandler);
    }

    public InventoryHandler getInventoryHandler() {
        return this.inventoryHandler;
    }

    public FilterInventory getFilterInventory() {
        return filterInventory;
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.centrifuge_input");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        return new CentrifugeInputContainer(id, playerInventory, this);
    }

    public int getRecipeTime() {
        return processRecipe == null ? 0 : Math.max(1, MathHelper.floor(processRecipe.getTime() * controller.getRecipeTimeModifier()));
    }

    private void setProcessStage(ProcessStage newStage) {
        processStage = newStage;
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            if (processStage == ProcessStage.IDLE && canProcess()) startProcess();
            if (processStage == ProcessStage.PROCESSING) processRecipe();
            if (processStage == ProcessStage.FINALIZING) depositResults();
            if (processStage == ProcessStage.COMPLETED) processCompleted();
        }
    }

    private boolean canProcess() {
        return filterRecipe != null && consumeInputs(true);
    }

    private void startProcess() {
        setProcessStage(ProcessStage.PROCESSING);
        processRecipe = filterRecipe;
        processTime = getRecipeTime();
        processEnergy = processRecipe.getEnergyPerTick() * this.controller.getRecipePowerModifier();
        consumeInputs(false);
    }

    private boolean consumeInputs(boolean simulate) {
        Ingredient ingredient = filterRecipe.getIngredient();
        int needed = ingredient.getItems()[0].getCount();

        for (int slot = 0; needed > 0 && slot < inventoryHandler.getSlots(); slot++) {
            ItemStack stackInSlot = inventoryHandler.getStackInSlot(slot);
            if (stackInSlot == ItemStack.EMPTY || !ingredient.test(stackInSlot)) continue;
            int found = stackInSlot.getCount();
            if (!simulate) stackInSlot.shrink(needed);
            needed -= Math.min(found, needed);
        }

        return needed == 0;
    }

    private void processRecipe() {
        if (this.controller.getEnergyStorage().consumeEnergy(processEnergy, false)) {
            processTime--;
            if (processTime == 0) setProcessStage(ProcessStage.FINALIZING);
        }
    }

    //TODO need to handle multiple recipes via CPUs either simply multiply outputs (easier) or roll outputs X times (harder)
    private void depositResults() {
        if (processRecipe == null || depositResults(processRecipe.getItemOutputs(), itemOutputs) && depositResults(processRecipe.getFluidOutputs(), fluidOutputs)) {
            setProcessStage(ProcessStage.COMPLETED);
        }
    }



    private <T extends AbstractOutput, A extends TileEntity & ICentrifugeOutput<T>> boolean depositResults(List<CentrifugeRecipe.Output<T>> recipeOutputs, OutputLocations<A> outputLocations) {
        for (int i = 0; i < recipeOutputs.size(); i++) {
            CentrifugeRecipe.Output<T> recipeOutput = recipeOutputs.get(i);
            if (recipeOutput.getChance() >= MathUtils.RANDOM.nextFloat()) {
                A location = outputLocations.get(i).getTile();
                if (location == null) return false;
                if (!outputLocations.isDeposited(i)) outputLocations.setDeposited(i, location.depositResult(recipeOutput));
                if (!outputLocations.isDeposited(i)) return false;
            }
        }
        return true;
    }

    private void processCompleted() {
        processRecipe = null;
        itemOutputs.resetDeposited();
        fluidOutputs.resetDeposited();
        setProcessStage(ProcessStage.IDLE);
    }

    public void updateRecipe() {
        ItemStack recipeStack = filterInventory.getStackInSlot(RECIPE_SLOT);
        Inventory inventory = new Inventory(recipeStack);
        if (level != null) filterRecipe = level.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, inventory, level).orElse(null);
    }

    //region CAPABILITIES
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return lazyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.lazyOptional.invalidate();
    }

    //endregion

    //region NBT HANDLING
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(NBTConstants.NBT_PROCESS_TIME, processTime);
        return new SUpdateTileEntityPacket(worldPosition, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();
        processTime = tag.getInt(NBTConstants.NBT_PROCESS_TIME);
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    //TODO Load recipe here after caching resource location
    @Override
    public void onLoad() {
        updateRecipe();
        if (level != null) processRecipe = (CentrifugeRecipe) ((RecipeManagerAccessorInvoker) level.getRecipeManager()).callByType(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE).get(processRecipeID);
        super.onLoad();
    }

    @Override
    protected void readNBT(@NotNull CompoundNBT tag) {
        inventoryHandler.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        filterInventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_FILTER_INVENTORY));
        processStage = ProcessStage.valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
        processTime = tag.getInt(NBTConstants.NBT_PROCESS_TIME);
        processEnergy = tag.getInt(NBTConstants.NBT_PROCESS_ENERGY);
        if (tag.contains(NBTConstants.NBT_PROCESS_RECIPE)) processRecipeID = ResourceLocation.tryParse(tag.getString(NBTConstants.NBT_PROCESS_RECIPE));
        itemOutputs.deserialize(tag.getCompound(NBTConstants.NBT_ITEM_OUTPUTS), CentrifugeItemOutputEntity.class, level);
        fluidOutputs.deserialize(tag.getCompound(NBTConstants.NBT_FLUID_OUTPUTS), CentrifugeFluidOutputEntity.class, level);
        super.readNBT(tag);
    }
    // TODO Recipes aren't being updated correctly on world load!! figure out how to access the recipes
    //  server level is null when reading nbt and client level isn't but client doesn't have the tag to parse

    @NotNull
    @Override
    protected CompoundNBT writeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inventoryHandler.serializeNBT());
        tag.put(NBTConstants.NBT_FILTER_INVENTORY, filterInventory.serializeNBT());
        tag.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString().toLowerCase(Locale.ROOT));
        tag.putInt(NBTConstants.NBT_PROCESS_TIME, processTime);
        tag.putInt(NBTConstants.NBT_PROCESS_ENERGY, processEnergy);
        if (processRecipe != null) tag.putString(NBTConstants.NBT_PROCESS_RECIPE, processRecipe.getId().toString());
        tag.put(NBTConstants.NBT_ITEM_OUTPUTS, itemOutputs.serialize());
        tag.put(NBTConstants.NBT_FLUID_OUTPUTS, fluidOutputs.serialize());
        return tag;
    }
    //endregion

    @Override
    public void onAssembly() {
        ArrayList<CentrifugeItemOutputEntity> iout = new ArrayList<>(this.controller.getItemOutputs());
        itemOutputs.setFirst(iout.get(0), iout.get(0).getBlockPos());
        itemOutputs.setSecond(iout.get(1), iout.get(1).getBlockPos());
        itemOutputs.setThird(iout.get(2), iout.get(2).getBlockPos());
        ArrayList<CentrifugeFluidOutputEntity> fout = new ArrayList<>(this.controller.getFluidOutputs());
        fluidOutputs.setFirst(fout.get(0), iout.get(0).getBlockPos());
        fluidOutputs.setSecond(fout.get(0), iout.get(0).getBlockPos());
        fluidOutputs.setThird(fout.get(0), iout.get(0).getBlockPos());
    }

    @Override
    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeInt(processTime);
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    @Override
    public void handleGUINetworkPacket(PacketBuffer buffer) {
        processTime = buffer.readInt();
    }

    private class FilterInventory extends AbstractFilterItemHandler {

        public FilterInventory(int numSlots) {
            super(numSlots);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            assert level != null;
            return level.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, new Inventory(stack), level).isPresent();
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
