package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO: consider switching to record. (de)serialize methods would need to be changed to accommodate, as well as default values
public class CentrifugeState {

    private CentrifugeTier maxCentrifugeTier = CentrifugeTier.ERROR;
    private String owner = "null";
    private int energyCapacity = 0;
    private long terminal = 0;
    private Set<BlockPos> inputs = new HashSet<>();
    private List<BlockPos> itemOutputs = new ArrayList<>();
    private List<BlockPos> fluidOutputs = new ArrayList<>();
    private Set<BlockPos> dumps = new HashSet<>();
    private int energyPorts = 0;
    private int gearboxes = 0;
    private int processors = 0;
    private double recipePowerModifier = 1;
    private double recipeTimeModifier = 1;

    /**
     * the maximum tier for this centrifuge - affects the maximum tier for blocks attached to it
     */
    public CentrifugeTier getMaxCentrifugeTier() {
        return maxCentrifugeTier;
    }

    public void setMaxCentrifugeTier(CentrifugeTier maxCentrifugeTier) {
        this.maxCentrifugeTier = maxCentrifugeTier;
    }

    /**
     * Owner of the Centrifuge - idk if we'll add this or not
     */
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /** The Terminal block position represented as a long **/
    public long getTerminal() {
        return terminal;
    }

    public void setTerminal(long terminal) {
        this.terminal = terminal;
    }

    /**
     * max energy the centrifuge can hold
     */
    public int getEnergyCapacity() {
        return energyCapacity;
    }

    public void setEnergyCapacity(int energyCapacity) {
        this.energyCapacity = energyCapacity;
    }

    /**
     * total number of inputs attached to the centrifuge
     */
    public Set<BlockPos> getInputs() {
        return inputs;
    }

    public void setInputs(Set<BlockPos> inputs) {
        this.inputs = inputs;
    }

    /**
     * all item outputs attached to the centrifuge
     */
    public List<BlockPos> getItemOutputs() {
        return itemOutputs;
    }

    public void setItemOutputs(List<BlockPos> itemOutputs) {
        this.itemOutputs = itemOutputs;
    }

    /**
     * all fluid outputs attached to the centrifuge
     */
    public List<BlockPos> getFluidOutputs() {
        return fluidOutputs;
    }

    public void setFluidOutputs(List<BlockPos> fluidOutputs) {
        this.fluidOutputs = fluidOutputs;
    }

    //might not be worth keeping...
    public List<BlockPos> getOutputsByType(CentrifugeOutputType outputType) {
        return outputType.isItem() ? itemOutputs : fluidOutputs;
    }

    /**
     * total number of energy ports attached to the centrifuge
     */
    public int getEnergyPorts() {
        return energyPorts;
    }

    public void setEnergyPorts(int energyPorts) {
        this.energyPorts = energyPorts;
    }

    /**
     * total number of voids attached to the centrifuge
     */
    public Set<BlockPos> getDumps() {
        return dumps;
    }

    public void setDumps(Set<BlockPos> dumps) {
        this.dumps = dumps;
    }

    public boolean hasDumps() {
        return !this.dumps.isEmpty();
    }

    /**
     * total number of gearboxes attached to the centrifuge
     */
    public int getGearboxes() {
        return gearboxes;
    }

    public void setGearboxes(int gearboxes) {
        this.gearboxes = gearboxes;
    }

    /**
     * total number of processors attached to the centrifuge
     */
    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }

    /**
     * the per tick recipe power multiplier
     */
    public double getRecipePowerModifier() {
        return recipePowerModifier;
    }

    public void setRecipePowerModifier(double recipePowerModifier) {
        this.recipePowerModifier = recipePowerModifier;
    }

    /**
     * the recipe time reduction multiplier
     */
    public double getRecipeTimeModifier() {
        return recipeTimeModifier;
    }

    public void setRecipeTimeModifier(double recipeTimeModifier) {
        this.recipeTimeModifier = recipeTimeModifier;
    }

    public void serializeBytes(FriendlyByteBuf buf) {
        buf.writeUtf(owner);
        buf.writeEnum(maxCentrifugeTier);
        buf.writeInt(energyCapacity);
        buf.writeLong(terminal);
        buf.writeCollection(inputs, FriendlyByteBuf::writeBlockPos);
        buf.writeCollection(itemOutputs, FriendlyByteBuf::writeBlockPos);
        buf.writeCollection(fluidOutputs, FriendlyByteBuf::writeBlockPos);
        buf.writeCollection(dumps, FriendlyByteBuf::writeBlockPos);
        buf.writeInt(energyPorts);
        buf.writeInt(gearboxes);
        buf.writeInt(processors);
        buf.writeDouble(recipePowerModifier);
        buf.writeDouble(recipeTimeModifier);
    }

    public CentrifugeState deserializeBytes(FriendlyByteBuf buf) {
        owner = buf.readUtf();
        maxCentrifugeTier = buf.readEnum(CentrifugeTier.class);
        energyCapacity = buf.readInt();
        terminal = buf.readLong();
        inputs = buf.readCollection(Sets::newHashSetWithExpectedSize, FriendlyByteBuf::readBlockPos);
        itemOutputs = buf.readCollection(Lists::newArrayListWithExpectedSize, FriendlyByteBuf::readBlockPos);
        fluidOutputs = buf.readCollection(Lists::newArrayListWithExpectedSize, FriendlyByteBuf::readBlockPos);
        dumps = buf.readCollection(Sets::newHashSetWithExpectedSize, FriendlyByteBuf::readBlockPos);
        energyPorts = buf.readInt();
        gearboxes = buf.readInt();
        processors = buf.readInt();
        recipePowerModifier = buf.readDouble();
        recipeTimeModifier = buf.readDouble();
        return this;
    }
}
