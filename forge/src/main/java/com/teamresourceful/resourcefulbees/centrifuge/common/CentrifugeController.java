package com.teamresourceful.resourcefulbees.centrifuge.common;

import com.teamresourceful.resourcefulbees.centrifuge.common.blocks.AbstractCentrifuge;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeActivity;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.config.CentrifugeConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.AbstractOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.phosphophyllite.multiblock2.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock2.ValidationException;
import net.roguelogix.phosphophyllite.multiblock2.common.IEventMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.common.IPersistentMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.common.ITickablePartsMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.rectangular.IRectangularMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.touching.ITouchingMultiblock;
import net.roguelogix.phosphophyllite.multiblock2.validated.IValidatedMultiblock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;
import java.util.*;

public class CentrifugeController extends MultiblockController<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>
        implements IPersistentMultiblock<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        IRectangularMultiblock<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        ITouchingMultiblock<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        ITickablePartsMultiblock<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        IEventMultiblock<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController>,
        IValidatedMultiblock<AbstractCentrifugeEntity, AbstractCentrifuge, CentrifugeController> {

    private static final Vector3i MIN_SIZE = new Vector3i(3);
    private static final Vector3i MAX_SIZE = new Vector3i(7,8,7);

    private CentrifugeActivity centrifugeActivity = CentrifugeActivity.INACTIVE;

    private final Set<CentrifugeProcessorEntity> processors = new HashSet<>();
    private final Set<CentrifugeGearboxEntity> gearboxes = new HashSet<>();
    private final Set<CentrifugeEnergyPortEntity> energyPorts = new HashSet<>();
    private final Set<CentrifugeTerminalEntity> terminals = new HashSet<>();
    private final Map<BlockPos, CentrifugeInputEntity> inputs = new HashMap<>();
    private final Map<BlockPos, CentrifugeItemOutputEntity> itemOutputs = new HashMap<>();
    private final Map<BlockPos, CentrifugeFluidOutputEntity> fluidOutputs = new HashMap<>();
    private final Map<BlockPos, CentrifugeVoidEntity> filters = new HashMap<>();
    private final CentrifugeEnergyStorage energyStorage = new CentrifugeEnergyStorage();

    private CentrifugeTerminalEntity terminal;

    //region structure building and validation

    //TODO enforce only 1 redstone control block
    public CentrifugeController(@NotNull Level world) {
        super(world, AbstractCentrifugeEntity.class, AbstractCentrifuge.class);
    }

    @Override
    public void validateStage1() throws ValidationException {
        //checking size 26 instead of 27 bc air doesn't get added to blocks map but is allowed in centrifuge
        if (blocks.size() < 26) {
            throwValidationException(CentrifugeTranslations.MIN_BLOCKS);
        }
        checkRequiredBlocksExist(terminals, CentrifugeTranslations.NO_TERMINAL);
        checkRequiredBlocksExist(inputs.values(), CentrifugeTranslations.NO_INPUTS);
        checkRequiredBlocksExist(itemOutputs.values(), CentrifugeTranslations.NO_ITEM_OUTPUTS);
        checkRequiredBlocksExist(fluidOutputs.values(), CentrifugeTranslations.NO_FLUID_OUTPUTS);
        checkRequiredBlocksExist(energyPorts, CentrifugeTranslations.NO_ENERGY_PORTS);
    }

    @Override
    public void validateStage2() throws ValidationException {
        this.terminal = terminals.iterator().next();
        CentrifugeTier tier = this.terminal.getTier();
        checkBlockExceedsTier(filters.values(), tier, CentrifugeTranslations.VOID_EXCEEDS_TIER);
        checkBlockExceedsTier(inputs.values(), tier, CentrifugeTranslations.INPUT_EXCEEDS_TIER);
        checkBlockExceedsTier(itemOutputs.values(), tier, CentrifugeTranslations.ITEM_OUTPUT_EXCEEDS_TIER);
        checkBlockExceedsTier(fluidOutputs.values(), tier, CentrifugeTranslations.FLUID_OUTPUT_EXCEEDS_TIER);
        checkBlockExceedsTier(energyPorts, tier, CentrifugeTranslations.ENERGY_PORT_EXCEEDS_TIER);
        if (terminals.size() != 1) throwValidationException(CentrifugeTranslations.TOO_MANY_TERMINALS);
        checkHasTooManyBlocks(processors, 63, CentrifugeTranslations.TOO_MANY_CPUS);
        checkHasTooManyBlocks(gearboxes, 64, CentrifugeTranslations.TOO_MANY_GEARBOXES);
    }

    @Override
    public void validateStage3() throws ValidationException {
        validateBlockLocations();
    }

    @Override
    public boolean allowedInteriorBlock(@NotNull Block block) {
        return block instanceof AirBlock;
    }

    @Nullable
    @Override
    public Vector3ic minSize() {
        return MIN_SIZE;
    }

    @Nullable
    @Override
    public Vector3ic maxSize() {
        return MAX_SIZE;
    }

    public Map<BlockPos, CentrifugeItemOutputEntity> getItemOutputs() {
        return itemOutputs;
    }

    public Map<BlockPos, CentrifugeFluidOutputEntity> getFluidOutputs() {
        return fluidOutputs;
    }

    //todo figure out how to get rid of this cast
    public <A extends AbstractCentrifugeOutputEntity<T, E>, T extends AbstractOutput<E>, E> Map<BlockPos, A> getOutputsByType(CentrifugeOutputType outputType) {
        //noinspection unchecked
        return outputType.isItem() ? (Map<BlockPos, A>) itemOutputs : (Map<BlockPos, A>) fluidOutputs;
    }

    private <T extends AbstractCentrifugeEntity> void checkRequiredBlocksExist(Collection<T> blockSet, Component error) throws ValidationException {
        if (blockSet.isEmpty()) throwValidationException(error);
    }

    private <T extends AbstractCentrifugeEntity> void checkHasTooManyBlocks(Collection<T> blockSet, int allowed, Component error) throws ValidationException {
        if (blockSet.size() > allowed) throwValidationException(error);
    }

    private <T extends AbstractTieredCentrifugeEntity> void checkBlockExceedsTier(Collection<T> blockSet, CentrifugeTier tier, Component error) throws ValidationException {
        for (T block : blockSet) {
            if (block.getTier().ordinal() > tier.ordinal()) throwValidationException(error);
        }
    }

    private void throwValidationException(BlockPos pos) throws ValidationException {
        throwValidationException(Component.translatable(CentrifugeTranslations.WRONG_OUTPUT_LOC, pos));
    }

    private void throwValidationException(Component error) throws ValidationException {
        throw new ValidationException(error);
    }

    private void validateBlockLocations() throws ValidationException {
        for (CentrifugeInputEntity input : inputs.values()) {
            if (input.getBlockPos().getY() != max().y()) throwValidationException(CentrifugeTranslations.INPUT_NOT_ON_TOP);
        }
        validateSidesOnly(itemOutputs.values());
        validateSidesOnly(fluidOutputs.values());
        validateSidesOnly(filters.values());
        validateSidesOnly(energyPorts);
    }

    private void validateSidesOnly(Collection<? extends AbstractTieredCentrifugeEntity> entities) throws ValidationException {
        for (AbstractTieredCentrifugeEntity entity : entities) {
            if (entity.getBlockPos().getY() == max().y()) throwValidationException(entity.getBlockPos());
        }
    }

    @Override
    protected void onPartAdded(@NotNull AbstractCentrifugeEntity tile) {
        if (tile instanceof CentrifugeInputEntity input) {
            inputs.put(input.getBlockPos(), input);
        }
        if (tile instanceof CentrifugeProcessorEntity processor) {
            processors.add(processor);
        }
        if (tile instanceof CentrifugeGearboxEntity gearbox) {
            gearboxes.add(gearbox);
        }
        if (tile instanceof CentrifugeItemOutputEntity itemOutput) {
            itemOutputs.put(itemOutput.getBlockPos(), itemOutput);
        }
        if (tile instanceof CentrifugeFluidOutputEntity fluidOutput) {
            fluidOutputs.put(fluidOutput.getBlockPos(), fluidOutput);
        }
        if (tile instanceof CentrifugeVoidEntity voidEntity) {
            filters.put(voidEntity.getBlockPos(), voidEntity);
        }
        if (tile instanceof CentrifugeEnergyPortEntity energyPort) {
            energyPorts.add(energyPort);
            energyStorage.increaseCapacity(energyPort.getTier().getEnergyCapacity());
        }
        if (tile instanceof CentrifugeTerminalEntity terminalEntity) {
            terminals.add(terminalEntity);
        }
    }

    @Override
    protected void onPartRemoved(@NotNull AbstractCentrifugeEntity tile) {
        if (tile instanceof CentrifugeInputEntity) {
            inputs.remove(tile.getBlockPos());
        }
        if (tile instanceof CentrifugeProcessorEntity) {
            processors.remove(tile);
        }
        if (tile instanceof CentrifugeGearboxEntity) {
            gearboxes.remove(tile);
        }
        if (tile instanceof CentrifugeItemOutputEntity) {
            itemOutputs.remove(tile.getBlockPos());
        }
        if (tile instanceof CentrifugeFluidOutputEntity) {
            fluidOutputs.remove(tile.getBlockPos());
        }
        if (tile instanceof CentrifugeVoidEntity) {
            filters.remove(tile.getBlockPos());
        }
        if (tile instanceof CentrifugeEnergyPortEntity energyPort) {
            energyPorts.remove(tile);
            energyStorage.decreaseCapacity(energyPort.getTier().getEnergyCapacity());
        }
        if (tile instanceof CentrifugeTerminalEntity terminalEntity) {
            terminals.remove(terminalEntity);
        }
    }
    //endregion

    public double getRecipeTimeModifier() {
        return gearboxes.isEmpty() ? 1 : Math.pow(1- CentrifugeConfig.recipeTimeExponent /inputs.size(), gearboxes.size());
        //consider making the divide by inputs a configurable option
    }

    public double getRecipePowerModifier() {
        double cpuModifier = processors.isEmpty() ? 1 : 1 + (CentrifugeConfig.cpuPowerExponent * Math.pow(1.1, processors.size()));
        double gbxModifier = gearboxes.isEmpty() ? 1 : 1 + (CentrifugeConfig.gearboxPowerExponent * Math.pow(1.1, gearboxes.size()));
        return gbxModifier * cpuModifier;
    }

    public int getMaxInputRecipes() {
        return 1 + processors.size();
    }

    public synchronized boolean filtersContainItem(ItemStack stack) {
        for (CentrifugeVoidEntity filter : filters.values()) {
            if (filter.containsItem(stack)) return true;
        }
        return false;
    }

    public synchronized boolean filtersContainFluid(FluidStack stack) {
        for (CentrifugeVoidEntity filter : filters.values()) {
            if (filter.containsFluid(stack)) return true;
        }
        return false;
    }

/*    boolean updateBlockStates = false;

*//*    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockAndUpdate(terminal.getBlockPos(), terminal.getBlockState().setValue(CentrifugeActivity.PROPERTY, centrifugeActivity));
            terminal.setChanged();
        });
    }*//*

    public synchronized void setActive(@Nonnull CentrifugeActivity newState) {
        if (centrifugeActivity != newState) {
            centrifugeActivity = newState;
            updateBlockStates = true;
        }
        //simulation.setActive(centrifugeActivity == CentrifugeActivity.ACTIVE); simulation stuff appears to be reactor specific and not phos specific
    }

    //TODO consider removing Centrifuge Activity
    public synchronized void toggleActive() {
        setActive(centrifugeActivity == CentrifugeActivity.ACTIVE ? CentrifugeActivity.INACTIVE : CentrifugeActivity.ACTIVE);
    }*/

    public synchronized boolean isActive() {
        return centrifugeActivity == CentrifugeActivity.ACTIVE;
    }

    public CentrifugeEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    //region NBT HANDLING
    @Override
    public void read(@Nonnull CompoundTag compound) {
        if (compound.contains("centrifugeState")) centrifugeActivity = CentrifugeActivity.fromByte(compound.getByte("centrifugeState"));
        energyStorage.deserializeNBT(compound);
    }

    @Override
    @Nonnull
    public CompoundTag write() {
        CompoundTag compound = new CompoundTag();
        compound.putByte("centrifugeState", centrifugeActivity.getByte());
        energyStorage.serializeNBT(compound);
        return compound;
    }
    //endregion

    /*  note for self:
     *  for handling the situations where the controllers aren't "aware" of their NBT yet
     *  ie: merged while loading
     *  if both have all blocks loaded, then its assumed they have their NBT, and can handle merging the data themselves
     *  via merge
     */
    @Override
    public @NotNull CompoundTag mergeNBTs(@NotNull CompoundTag nbtA, @NotNull CompoundTag nbtB) {
        CompoundTag newTag = new CompoundTag();
        int energyStoredA = nbtA.getInt("storedEnergy");
        int energyStoredB = nbtB.getInt("storedEnergy");
        newTag.putInt("storedEnergy", energyStoredA+energyStoredB);
        return newTag;
    }

    @Override
    protected void merge(@NotNull CentrifugeController other) {
        energyStorage.storeEnergy(other.energyStorage.getStored());
    }

    @Override
    protected void split(@NotNull List<CentrifugeController> others) {
        int capacity = energyStorage.getCapacity();
        if (capacity == 0) return;
        int stored = energyStorage.getStored();
        for (CentrifugeController other : others) {
            CentrifugeEnergyStorage otherEnergyStorage = other.energyStorage;
            int otherCapacity = otherEnergyStorage.getCapacity();
            if (otherCapacity == 0) continue;
            double percent = (double) otherCapacity / capacity;
            otherEnergyStorage.storeEnergy((int) (stored * percent));
        }
    }

    public void updateCentrifugeState(CentrifugeState centrifugeState) {
        centrifugeState.setMaxCentrifugeTier(terminal.getTier());
        centrifugeState.setTerminal(terminal.getBlockPos().asLong());
        centrifugeState.setInputs(inputs.keySet());
        centrifugeState.setItemOutputs(itemOutputs.keySet().stream().toList());
        centrifugeState.setFluidOutputs(fluidOutputs.keySet().stream().toList());
        centrifugeState.setFilters(filters.keySet());
        centrifugeState.setEnergyPorts(energyPorts.size());
        centrifugeState.setGearboxes(gearboxes.size());
        centrifugeState.setProcessors(processors.size());
        centrifugeState.setRecipePowerModifier(getRecipePowerModifier());
        centrifugeState.setRecipeTimeModifier(getRecipeTimeModifier());
    }

    @Override
    public void tick() {
        dirty();
    }
}