package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.AbstractCentrifuge;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.*;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeActivity;
import net.minecraft.block.AirBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.phosphophyllite.multiblock.generic.ValidationError;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class CentrifugeController extends RectangularMultiblockController<CentrifugeController, AbstractCentrifugeEntity, AbstractCentrifuge> {

    private CentrifugeActivity centrifugeActivity = CentrifugeActivity.INACTIVE;

    private final Set<CentrifugeInputEntity> inputs = new HashSet<>();
    private final Set<CentrifugeProcessorEntity> processors = new HashSet<>();
    private final Set<CentrifugeGearboxEntity> gearboxes = new HashSet<>();
    private final Set<CentrifugeItemOutputEntity> itemOutputs = new HashSet<>();
    private final Set<CentrifugeFluidOutputEntity> fluidOutputs = new HashSet<>();
    private final Set<CentrifugeVoidEntity> dumps = new HashSet<>();
    private final Set<CentrifugeEnergyPortEntity> energyPorts = new HashSet<>();
    private final Set<CentrifugeTerminalEntity> terminals = new HashSet<>();
    private final CentrifugeEnergyStorage energyStorage = new CentrifugeEnergyStorage();

    //region structure building and validation

    //TODO enforce only 1 redstone control block
    public CentrifugeController(@NotNull World world) {
        super(world, AbstractCentrifugeEntity.class::isInstance , AbstractCentrifuge.class::isInstance);
        minSize.set(3);
        maxSize.set(16);
        interiorValidator = AirBlock.class::isInstance;
        setAssemblyValidator(centrifugeController -> {
            checkRequiredBlocksExist(terminals, "no_terminal");
            checkRequiredBlocksExist(inputs, "no_input");
            checkRequiredBlocksExist(itemOutputs, "no_item_output");
            checkRequiredBlocksExist(fluidOutputs, "no_fluid_output");
            checkRequiredBlocksExist(energyPorts, "no_energy_port");
            checkHasTooManyBlocks(terminals, 1,"too_many_terminal" );
            checkHasTooManyBlocks(processors, 63, "too_many_cpu");
            checkHasTooManyBlocks(gearboxes, 64, "too_many_gearbox");
            terminals.stream().findFirst().ifPresent(terminal -> {
                CentrifugeTier tier = terminal.getTier();
                checkBlockExceedsTier(dumps, tier, "void_exceeds_tier");
                checkBlockExceedsTier(inputs, tier, "input_exceeds_tier");
                checkBlockExceedsTier(itemOutputs, tier, "item_output_exceeds_tier");
                checkBlockExceedsTier(fluidOutputs, tier, "fluid_output_exceeds_tier");
                checkBlockExceedsTier(energyPorts, tier, "energy_port_exceeds_tier");
            });
            validateBlockLocations();
            return true;
        });
    }

    public Set<CentrifugeItemOutputEntity> getItemOutputs() {
        return itemOutputs;
    }

    public Set<CentrifugeFluidOutputEntity> getFluidOutputs() {
        return fluidOutputs;
    }

    private <T extends AbstractCentrifugeEntity> void checkRequiredBlocksExist(Set<T> blockSet, String error) {
        if (blockSet.isEmpty()) throwValidationError(error);
    }

    private <T extends AbstractCentrifugeEntity> void checkHasTooManyBlocks(Set<T> blockSet, int allowed, String error) {
        if (blockSet.size() > allowed) throwValidationError(error);
    }

    private <T extends AbstractTieredCentrifugeEntity> void checkBlockExceedsTier(Set<T> blockSet, CentrifugeTier tier, String error) {
        blockSet.forEach(t -> {
            if (t.getTier().ordinal() > tier.ordinal()) throwValidationError(error);
        });
    }

    private void throwValidationError(String error) {
        throw new ValidationError("multiblock.error.resourcefulbees." + error);
    }

    private void validateBlockLocations() {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (CentrifugeInputEntity input : inputs) {
            mutablePos.set(input.getBlockPos());
            if (mutablePos.getY() != maxCoord().y()) throwValidationError("input_not_on_top");
        }

        for (CentrifugeItemOutputEntity itemOutput : itemOutputs) {
            mutablePos.set(itemOutput.getBlockPos());
            if (mutablePos.getY() == maxCoord().y()) throwValidationError("wrong_output_location");
        }
        for (CentrifugeFluidOutputEntity fluidOutputOutput : fluidOutputs) {
            mutablePos.set(fluidOutputOutput.getBlockPos());
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
            inputs.add((CentrifugeInputEntity) tile);
        }
        if (tile instanceof CentrifugeProcessorEntity) {
            processors.add((CentrifugeProcessorEntity) tile);
        }
        if (tile instanceof CentrifugeGearboxEntity) {
            gearboxes.add((CentrifugeGearboxEntity) tile);
        }
        if (tile instanceof CentrifugeItemOutputEntity) {
            itemOutputs.add((CentrifugeItemOutputEntity) tile);
        }
        if (tile instanceof CentrifugeFluidOutputEntity) {
            fluidOutputs.add((CentrifugeFluidOutputEntity) tile);
        }
        if (tile instanceof CentrifugeVoidEntity) {
            dumps.add((CentrifugeVoidEntity) tile);
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
            inputs.remove(tile);
        }
        if (tile instanceof CentrifugeProcessorEntity) {
            processors.remove(tile);
        }
        if (tile instanceof CentrifugeGearboxEntity) {
            gearboxes.remove(tile);
        }
        if (tile instanceof CentrifugeItemOutputEntity) {
            itemOutputs.remove(tile);
        }
        if (tile instanceof CentrifugeFluidOutputEntity) {
            fluidOutputs.remove(tile);
        }
        if (tile instanceof CentrifugeVoidEntity) {
            dumps.remove(tile);
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
        for (CentrifugeVoidEntity dump : dumps) {
            if (dump.containsItem(stack)) return true;
        }
        return false;
    }

    public synchronized boolean dumpsContainFluid(FluidStack stack) {
        for (CentrifugeVoidEntity dump : dumps) {
            if (dump.containsFluid(stack)) return true;
        }
        return false;
    }

    boolean updateBlockStates = false;

    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockAndUpdate(terminal.getBlockPos(), terminal.getBlockState().setValue(CentrifugeActivity.CENTRIFUGE_ACTIVITY_ENUM_PROPERTY, centrifugeActivity));
            terminal.setChanged();
        });
    }

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
    protected void read(@Nonnull CompoundNBT compound) {
        if (compound.contains("centrifugeState")) centrifugeActivity = CentrifugeActivity.fromByte(compound.getByte("centrifugeState"));
        energyStorage.deserializeNBT(compound);
    }

    @Override
    @Nonnull
    protected CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        compound.putByte("centrifugeState", centrifugeActivity.getByte());
        energyStorage.serializeNBT(compound);
        return compound;
    }
    //endregion


    @Override
    public void tick() {
        markDirty();
        super.tick();
    }

    @Override
    protected void onMerge(@NotNull CentrifugeController otherController) {
        super.onMerge(otherController);
    }
}