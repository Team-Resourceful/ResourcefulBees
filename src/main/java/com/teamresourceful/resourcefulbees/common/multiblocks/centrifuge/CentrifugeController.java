package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.AbstractCentrifuge;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.*;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeActivity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.phosphophyllite.multiblock.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class CentrifugeController extends RectangularMultiblockController<AbstractCentrifugeEntity, CentrifugeController> {

    private CentrifugeActivity centrifugeActivity = CentrifugeActivity.INACTIVE;

    private final Set<CentrifugeProcessorEntity> processors = new HashSet<>();
    private final Set<CentrifugeGearboxEntity> gearboxes = new HashSet<>();
    private final Set<CentrifugeEnergyPortEntity> energyPorts = new HashSet<>();
    private final Set<CentrifugeTerminalEntity> terminals = new HashSet<>();
    private final Map<BlockPos, CentrifugeInputEntity> inputs = new HashMap<>();
    private final Map<BlockPos, CentrifugeItemOutputEntity> itemOutputs = new HashMap<>();
    private final Map<BlockPos, CentrifugeFluidOutputEntity> fluidOutputs = new HashMap<>();
    private final Map<BlockPos, CentrifugeVoidEntity> dumps = new HashMap<>();
    private final CentrifugeEnergyStorage energyStorage = new CentrifugeEnergyStorage();

    private final HashMap<BlockPos, ArrayList<BlockPos>> inputOutputMap = new HashMap<>();

    private CentrifugeTerminalEntity terminal;

    //region structure building and validation

    //TODO enforce only 1 redstone control block
    public CentrifugeController(@NotNull Level world) {
        super(world, AbstractCentrifugeEntity.class::isInstance , AbstractCentrifuge.class::isInstance);
        minSize.set(3);
        maxSize.set(7, 8, 7);
        interiorValidator = block -> block.defaultBlockState().isAir();
        setAssemblyValidator(centrifugeController -> {
            checkRequiredBlocksExist(terminals, "no_terminal");
            checkRequiredBlocksExist(inputs.values(), "no_input");
            checkRequiredBlocksExist(itemOutputs.values(), "no_item_output");
            checkRequiredBlocksExist(fluidOutputs.values(), "no_fluid_output");
            checkRequiredBlocksExist(energyPorts, "no_energy_port");
            if (terminals.size() != 1) throwValidationError("too_many_terminal");
            checkHasTooManyBlocks(processors, 63, "too_many_cpu");
            checkHasTooManyBlocks(gearboxes, 64, "too_many_gearbox");
            terminals.stream().findFirst().ifPresent(entity -> {
                this.terminal = entity;
                CentrifugeTier tier = entity.getTier();
                checkBlockExceedsTier(dumps.values(), tier, "void_exceeds_tier");
                checkBlockExceedsTier(inputs.values(), tier, "input_exceeds_tier");
                checkBlockExceedsTier(itemOutputs.values(), tier, "item_output_exceeds_tier");
                checkBlockExceedsTier(fluidOutputs.values(), tier, "fluid_output_exceeds_tier");
                checkBlockExceedsTier(energyPorts, tier, "energy_port_exceeds_tier");
            });
            validateBlockLocations();
            return true;
        });
    }

    public List<CentrifugeItemOutputEntity> getItemOutputs() {
        return new ArrayList<>(itemOutputs.values());
    }

    public List<CentrifugeFluidOutputEntity> getFluidOutputs() {
        return new ArrayList<>(fluidOutputs.values());
    }

    private <T extends AbstractCentrifugeEntity> void checkRequiredBlocksExist(Collection<T> blockSet, String error) {
        if (blockSet.isEmpty()) throwValidationError(error);
    }

    private <T extends AbstractCentrifugeEntity> void checkHasTooManyBlocks(Collection<T> blockSet, int allowed, String error) {
        if (blockSet.size() > allowed) throwValidationError(error);
    }

    private <T extends AbstractTieredCentrifugeEntity> void checkBlockExceedsTier(Collection<T> blockSet, CentrifugeTier tier, String error) {
        blockSet.forEach(t -> {
            if (t.getTier().ordinal() > tier.ordinal()) throwValidationError(error);
        });
    }

    private void throwValidationError(String error) {
        throw new ValidationError("multiblock.error.resourcefulbees." + error);
    }

    private void validateBlockLocations() {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (CentrifugeInputEntity input : inputs.values()) {
            mutablePos.set(input.getBlockPos());
            if (mutablePos.getY() != maxCoord().y()) throwValidationError("input_not_on_top");
        }

        for (CentrifugeItemOutputEntity itemOutput : itemOutputs.values()) {
            mutablePos.set(itemOutput.getBlockPos());
            if (mutablePos.getY() == maxCoord().y()) throwValidationError("wrong_output_location");
        }
        for (CentrifugeFluidOutputEntity fluidOutputOutput : fluidOutputs.values()) {
            mutablePos.set(fluidOutputOutput.getBlockPos());
            if (mutablePos.getY() == maxCoord().y()) throwValidationError("wrong_output_location");
        }
        for (CentrifugeVoidEntity dump : dumps.values()) {
            mutablePos.set(dump.getBlockPos());
            if (mutablePos.getY() == maxCoord().y()) throwValidationError("wrong_output_location");
        }
    }

    @Override
    protected void onPartPlaced(@Nonnull AbstractCentrifugeEntity placed) {
        onPartAttached(placed);
    }

    @Override
    protected synchronized void onPartAttached(@Nonnull AbstractCentrifugeEntity tile) {
        if (tile instanceof CentrifugeInputEntity) {
            inputs.put(tile.getBlockPos(), (CentrifugeInputEntity) tile);
        }
        if (tile instanceof CentrifugeProcessorEntity) {
            processors.add((CentrifugeProcessorEntity) tile);
        }
        if (tile instanceof CentrifugeGearboxEntity) {
            gearboxes.add((CentrifugeGearboxEntity) tile);
        }
        if (tile instanceof CentrifugeItemOutputEntity) {
            itemOutputs.put(tile.getBlockPos(), (CentrifugeItemOutputEntity) tile);
        }
        if (tile instanceof CentrifugeFluidOutputEntity) {
            fluidOutputs.put(tile.getBlockPos(), (CentrifugeFluidOutputEntity) tile);
        }
        if (tile instanceof CentrifugeVoidEntity) {
            dumps.put(tile.getBlockPos(), (CentrifugeVoidEntity) tile);
        }
        if (tile instanceof CentrifugeEnergyPortEntity) {
            CentrifugeEnergyPortEntity energyPort = (CentrifugeEnergyPortEntity) tile;
            energyPorts.add(energyPort);
            energyStorage.increaseCapacity(energyPort.getTier().getEnergyCapacity());
        }
        if (tile instanceof CentrifugeTerminalEntity) {
            terminals.add((CentrifugeTerminalEntity) tile);
        }
    }

    @Override
    protected void onPartBroken(@Nonnull AbstractCentrifugeEntity broken) {
        onPartDetached(broken);
    }

    @Override
    protected synchronized void onPartDetached(@Nonnull AbstractCentrifugeEntity tile) {
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
            dumps.remove(tile.getBlockPos());
        }
        if (tile instanceof CentrifugeEnergyPortEntity) {
            energyPorts.remove(tile);
            energyStorage.decreaseCapacity(((CentrifugeEnergyPortEntity) tile).getTier().getEnergyCapacity());
        }
        if (tile instanceof CentrifugeTerminalEntity) {
            terminals.remove(tile);
        }

    }
    //endregion

    public double getRecipeTimeModifier() {
        return gearboxes.isEmpty() ? 1 : Math.pow(1-0.1/inputs.size(), gearboxes.size());
        //TODO change 0.05 to be a config value or passed in as block property **make dividing by inputs configurable**
    }

    public int getRecipePowerModifier() {
        double cpuModifier = processors.isEmpty() ? 1 : 1 + (0.4 * Math.pow(1.1, processors.size()));
        //TODO change 0.4 to be a config value or passed in as block property
        double gbxModifier = gearboxes.isEmpty() ? 1 : 1 + (0.2 * Math.pow(1.1, gearboxes.size()));
        //TODO change 0.2 to be a config value or passed in as block property
        return (int) (gbxModifier * cpuModifier);
    }

    public int getMaxInputRecipes() {
        return 1 + processors.size();
    }

    public synchronized boolean dumpsContainItem(ItemStack stack) {
        for (CentrifugeVoidEntity dump : dumps.values()) {
            if (dump.containsItem(stack)) return true;
        }
        return false;
    }

    public synchronized boolean dumpsContainFluid(FluidStack stack) {
        for (CentrifugeVoidEntity dump : dumps.values()) {
            if (dump.containsFluid(stack)) return true;
        }
        return false;
    }

    boolean updateBlockStates = false;

/*    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockAndUpdate(terminal.getBlockPos(), terminal.getBlockState().setValue(CentrifugeActivity.PROPERTY, centrifugeActivity));
            terminal.setChanged();
        });
    }*/

    public synchronized void setActive(@Nonnull CentrifugeActivity newState) {
        if (centrifugeActivity != newState) {
            centrifugeActivity = newState;
            updateBlockStates = true;
        }
        //simulation.setActive(centrifugeActivity == CentrifugeActivity.ACTIVE); simulation stuff appears to be reactor specific and not phos specific
    }

    public synchronized void toggleActive() {
        setActive(centrifugeActivity == CentrifugeActivity.ACTIVE ? CentrifugeActivity.INACTIVE : CentrifugeActivity.ACTIVE);
    }

    public synchronized boolean isActive() {
        return centrifugeActivity == CentrifugeActivity.ACTIVE;
    }

    public CentrifugeEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    //region NBT HANDLING
    @Override
    protected void read(@Nonnull CompoundTag compound) {
        if (compound.contains("centrifugeState")) centrifugeActivity = CentrifugeActivity.fromByte(compound.getByte("centrifugeState"));
        energyStorage.deserializeNBT(compound);
    }

    @Override
    @Nonnull
    protected CompoundTag write() {
        CompoundTag compound = new CompoundTag();
        compound.putByte("centrifugeState", centrifugeActivity.getByte());
        energyStorage.serializeNBT(compound);
        return compound;
    }
    //endregion

    @Override
    protected void onMerge(@NotNull CentrifugeController otherController) {
        energyStorage.storeEnergy(otherController.energyStorage.getStored());
        energyStorage.increaseCapacity(otherController.energyStorage.getCapacity());
        //TODO test this ^^ make two separate centrifuges 3x3x3 with a one block space in between then combine the
    }

    public void updateCentrifugeState(CentrifugeState centrifugeState) {
        //centrifugeState.setCentrifugeActivity(centrifugeActivity);
        centrifugeState.setMaxCentrifugeTier(terminal.getTier());
        centrifugeState.setTerminal(terminal.getBlockPos().asLong());
        centrifugeState.setEnergyStored(energyStorage.getStored());
        centrifugeState.setEnergyCapacity(energyStorage.getCapacity());
        centrifugeState.setInputs(inputs.keySet());
        centrifugeState.setItemOutputs(itemOutputs.keySet());
        centrifugeState.setFluidOutputs(fluidOutputs.keySet());
        centrifugeState.setDumps(dumps.keySet());
        centrifugeState.setEnergyPorts(energyPorts.size());
        centrifugeState.setGearboxes(gearboxes.size());
        centrifugeState.setProcessors(processors.size());
        centrifugeState.setRecipePowerModifier(getRecipePowerModifier());
        centrifugeState.setRecipeTimeModifier(getRecipeTimeModifier());
    }


}