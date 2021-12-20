package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states;

import com.google.common.collect.Sets;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashSet;
import java.util.Set;

public class CentrifugeState {

    private CentrifugeTier maxCentrifugeTier = CentrifugeTier.ERROR;
    private String owner = "null";
    private int energyCapacity = 0;
    private long terminal = 0;
    private Set<BlockPos> inputs = new HashSet<>();
    private Set<BlockPos> itemOutputs = new HashSet<>();
    private Set<BlockPos> fluidOutputs = new HashSet<>();
    private Set<BlockPos> dumps = new HashSet<>();
    private int energyPorts = 0;
    private int gearboxes = 0;
    private int processors = 0;
    private int recipePowerModifier = 1;
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
     * total number of item outputs attached to the centrifuge
     */
    public Set<BlockPos> getItemOutputs() {
        return itemOutputs;
    }

    public void setItemOutputs(Set<BlockPos> itemOutputs) {
        this.itemOutputs = itemOutputs;
    }

    /**
     * total number of fluid outputs attached to the centrifuge
     */
    public Set<BlockPos> getFluidOutputs() {
        return fluidOutputs;
    }

    public void setFluidOutputs(Set<BlockPos> fluidOutputs) {
        this.fluidOutputs = fluidOutputs;
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
    public int getRecipePowerModifier() {
        return recipePowerModifier;
    }

    public void setRecipePowerModifier(int recipePowerModifier) {
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
        buf.writeInt(gearboxes);
        buf.writeInt(processors);
        buf.writeInt(recipePowerModifier);
        buf.writeDouble(recipeTimeModifier);
    }

    public CentrifugeState deserializeBytes(FriendlyByteBuf buf) {
        owner = buf.readUtf();
        maxCentrifugeTier = buf.readEnum(CentrifugeTier.class);
        energyCapacity = buf.readInt();
        terminal = buf.readLong();
        inputs = buf.readCollection(Sets::newHashSetWithExpectedSize, FriendlyByteBuf::readBlockPos);
        itemOutputs = buf.readCollection(Sets::newHashSetWithExpectedSize, FriendlyByteBuf::readBlockPos);
        fluidOutputs = buf.readCollection(Sets::newHashSetWithExpectedSize, FriendlyByteBuf::readBlockPos);
        dumps = buf.readCollection(Sets::newHashSetWithExpectedSize, FriendlyByteBuf::readBlockPos);
        gearboxes = buf.readInt();
        processors = buf.readInt();
        recipePowerModifier = buf.readInt();
        recipeTimeModifier = buf.readDouble();
        return this;
    }
}
