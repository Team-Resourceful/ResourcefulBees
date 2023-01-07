package com.teamresourceful.resourcefulbees.centrifuge.common;

import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.centrifuge.common.blocks.AbstractCentrifuge;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractTieredCentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeEnergyStorage;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeActivity;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.AbstractOutput;
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
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3ic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static final String WRONG_OUTPUT_LOC = "wrong_output_location";

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
            throw new ValidationException("min blocks");
        }
        checkRequiredBlocksExist(terminals, "no_terminal");
        checkRequiredBlocksExist(inputs.values(), "no_input");
        checkRequiredBlocksExist(itemOutputs.values(), "no_item_output");
        checkRequiredBlocksExist(fluidOutputs.values(), "no_fluid_output");
        checkRequiredBlocksExist(energyPorts, "no_energy_port");
    }

    @Override
    public void validateStage2() throws ValidationException {
        this.terminal = terminals.iterator().next();
        CentrifugeTier tier = this.terminal.getTier();
        checkBlockExceedsTier(dumps.values(), tier, "void_exceeds_tier");
        checkBlockExceedsTier(inputs.values(), tier, "input_exceeds_tier");
        checkBlockExceedsTier(itemOutputs.values(), tier, "item_output_exceeds_tier");
        checkBlockExceedsTier(fluidOutputs.values(), tier, "fluid_output_exceeds_tier");
        checkBlockExceedsTier(energyPorts, tier, "energy_port_exceeds_tier");
        if (terminals.size() != 1) throwValidationException("too_many_terminal");
        checkHasTooManyBlocks(processors, 63, "too_many_cpu");
        checkHasTooManyBlocks(gearboxes, 64, "too_many_gearbox");
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

    private <T extends AbstractCentrifugeEntity> void checkRequiredBlocksExist(Collection<T> blockSet, String error) throws ValidationException {
        if (blockSet.isEmpty()) throwValidationException(error);
    }

    private <T extends AbstractCentrifugeEntity> void checkHasTooManyBlocks(Collection<T> blockSet, int allowed, String error) throws ValidationException {
        if (blockSet.size() > allowed) throwValidationException(error);
    }

    private <T extends AbstractTieredCentrifugeEntity> void checkBlockExceedsTier(Collection<T> blockSet, CentrifugeTier tier, String error) throws ValidationException {
        for (T block : blockSet) {
            if (block.getTier().ordinal() > tier.ordinal()) throwValidationException(error);
        }
    }

    private void throwValidationException(String error) throws ValidationException {
        throw new ValidationException(Component.translatable("multiblock.error.resourcefulbees." + error));
    }

    private void validateBlockLocations() throws ValidationException {
        for (CentrifugeInputEntity input : inputs.values()) {
            if (input.getBlockPos().getY() != max().y()) throwValidationException("input_not_on_top");
        }
        for (CentrifugeItemOutputEntity itemOutput : itemOutputs.values()) {
            if (itemOutput.getBlockPos().getY() == max().y()) throwValidationException(WRONG_OUTPUT_LOC);
        }
        for (CentrifugeFluidOutputEntity fluidOutputOutput : fluidOutputs.values()) {
            if (fluidOutputOutput.getBlockPos().getY() == max().y()) throwValidationException(WRONG_OUTPUT_LOC);
        }
        for (CentrifugeVoidEntity dump : dumps.values()) {
            if (dump.getBlockPos().getY() == max().y()) throwValidationException(WRONG_OUTPUT_LOC);
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
            dumps.put(voidEntity.getBlockPos(), voidEntity);
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
            dumps.remove(tile.getBlockPos());
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
        return gearboxes.isEmpty() ? 1 : Math.pow(1-0.1/inputs.size(), gearboxes.size());
        //TODO change 0.05 to be a config value or passed in as block property **make dividing by inputs configurable**
    }

    public double getRecipePowerModifier() {
        double cpuModifier = processors.isEmpty() ? 1 : 1 + (0.4 * Math.pow(1.1, processors.size()));
        //TODO change 0.4 to be a config value or passed in as block property
        double gbxModifier = gearboxes.isEmpty() ? 1 : 1 + (0.2 * Math.pow(1.1, gearboxes.size()));
        //TODO change 0.2 to be a config value or passed in as block property
        return gbxModifier * cpuModifier;
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

    @Override
    public @NotNull CompoundTag mergeNBTs(@NotNull CompoundTag nbtA, @NotNull CompoundTag nbtB) {
        return null; //TODO implement merging
    }

/*    @Override
    protected void onMerge(@NotNull CentrifugeController otherController) {
        energyStorage.storeEnergy(otherController.energyStorage.getStored());
        energyStorage.increaseCapacity(otherController.energyStorage.getCapacity());
        //TODO test this ^^ make two separate centrifuges 3x3x3 with a one block space in between then combine the
    }*/

    public void updateCentrifugeState(CentrifugeState centrifugeState) {
        centrifugeState.setMaxCentrifugeTier(terminal.getTier());
        centrifugeState.setTerminal(terminal.getBlockPos().asLong());
        centrifugeState.setEnergyCapacity(energyStorage.getCapacity());
        centrifugeState.setInputs(inputs.keySet());
        centrifugeState.setItemOutputs(itemOutputs.keySet().stream().toList());
        centrifugeState.setFluidOutputs(fluidOutputs.keySet().stream().toList());
        centrifugeState.setDumps(dumps.keySet());
        centrifugeState.setEnergyPorts(energyPorts.size());
        centrifugeState.setGearboxes(gearboxes.size());
        centrifugeState.setProcessors(processors.size());
        centrifugeState.setRecipePowerModifier(getRecipePowerModifier());
        centrifugeState.setRecipeTimeModifier(getRecipeTimeModifier());
    }
}