package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CentrifugeState implements INBTSerializable<CompoundTag> {

    /*<T extends AbstractGUICentrifugeEntity>*/

    //private CentrifugeActivity centrifugeActivity = CentrifugeActivity.INACTIVE;
    private CentrifugeTier maxCentrifugeTier = CentrifugeTier.ERROR;
    private String owner = "null";
    private int energyStored = 0;
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

/*    *//**
     * Activity status for the Centrifuge - Not sure if we're gonna use this yet...
     *//*
    public CentrifugeActivity getCentrifugeActivity() {
        return centrifugeActivity;
    }

    public void setCentrifugeActivity(CentrifugeActivity centrifugeActivity) {
        this.centrifugeActivity = centrifugeActivity;
    }*/

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
     * the current energy stored in the centrifuge
     */
    public int getEnergyStored() {
        return energyStored;
    }

    public void setEnergyStored(int energyStored) {
        this.energyStored = energyStored;
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Owner", owner);
        nbt.putString("MaxTier", maxCentrifugeTier.getName());
        nbt.putInt("EnergyStored", energyStored);
        nbt.putInt("EnergyCapacity", energyCapacity);
        nbt.putLong("Terminal", terminal);
        nbt.put("Inputs", writeBlockList(inputs));
        nbt.put("ItemOutputs", writeBlockList(itemOutputs));
        nbt.put("FluidOutputs", writeBlockList(fluidOutputs));
        nbt.put("Dumps", writeBlockList(dumps));
        nbt.putInt("EnergyPorts", energyPorts);
        nbt.putInt("Gearboxes", gearboxes);
        nbt.putInt("Processors", processors);
        nbt.putInt("RecipePowerModifier", recipePowerModifier);
        nbt.putDouble("RecipeTimeModifier", recipeTimeModifier);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        owner = nbt.getString("Owner");
        maxCentrifugeTier = CentrifugeTier.byName(nbt.getString("MaxTier"));
        energyStored = nbt.getInt("EnergyStored");
        energyCapacity = nbt.getInt("EnergyCapacity");
        terminal = nbt.getLong("Terminal");
        inputs = readBlockList(nbt.getList("Inputs", 10));
        itemOutputs = readBlockList(nbt.getList("ItemOutputs", 10));
        fluidOutputs = readBlockList(nbt.getList("FluidOutputs", 10));
        dumps = readBlockList(nbt.getList("Dumps", 10));
        energyPorts = nbt.getInt("EnergyPorts");
        gearboxes = nbt.getInt("Gearboxes");
        processors = nbt.getInt("Processors");
        recipePowerModifier = nbt.getInt("RecipePowerModifier");
        recipeTimeModifier = nbt.getDouble("RecipeTimeModifier");
    }

    private static ListTag writeBlockList(Collection<BlockPos> set) {
        ListTag listNBT = new ListTag();
        AtomicInteger i = new AtomicInteger();
        set.forEach(value -> {
            CompoundTag nbt = new CompoundTag();
            nbt.put(String.valueOf(i.getAndIncrement()), NbtUtils.writeBlockPos(value));
            listNBT.add(nbt);
        });
        return listNBT;
    }

    private Set<BlockPos> readBlockList(ListTag listNBT) {
        Set<BlockPos> blockSet = new HashSet<>();
        if (!listNBT.isEmpty()) {
            AtomicInteger i = new AtomicInteger();
            return listNBT.stream()
                    .map(CompoundTag.class::cast)
                    .map(tag -> NbtUtils.readBlockPos(tag.getCompound(String.valueOf(i.getAndIncrement()))))
                    .collect(Collectors.toSet());
        }
        return blockSet;
    }
}
