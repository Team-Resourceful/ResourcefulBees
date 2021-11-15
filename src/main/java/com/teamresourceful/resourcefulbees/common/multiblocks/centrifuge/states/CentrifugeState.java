package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CentrifugeState implements GuiSync.IGUIPacket {

    /**
     * Activity status for the Centrifuge - Not sure if we're gonna use this yet...
     */
    public CentrifugeActivity centrifugeActivity = CentrifugeActivity.INACTIVE;

    /**
     * the maximum tier for this centrifuge - affects the maximum tier for blocks attached to it
     */
    public CentrifugeTier maxCentrifugeTier = CentrifugeTier.ERROR;

    /**
     * Owner of the Centrifuge - idk if we'll add this or not
     */
    public UUID owner = null;

    /**
     * the current energy stored in the centrifuge
     */
    public int energyStored = 0;

    /**
     * max energy the centrifuge can hold
     */
    public int energyCapacity = 0;

    /**
     * total number of inputs attached to the centrifuge
     */
    public int numInputs = 0;

    /**
     * total number of item outputs attached to the centrifuge
     */
    public int numItemOutputs = 0;

    /**
     * total number of fluid outputs attached to the centrifuge
     */
    public int numFluidOutputs = 0;

    /**
     * total number of energy ports attached to the centrifuge
     */
    public int numEnergyPorts = 0;

    /**
     * total number of voids attached to the centrifuge
     */
    public int numDumps = 0;

    /**
     * total number of gearboxes attached to the centrifuge
     */
    public int numGearboxes = 0;

    /**
     * total number of processors attached to the centrifuge
     */
    public int numProcessors = 0;

    /**
     * the per tick recipe power multiplier
     */
    public int recipePowerModifier = 1;

    /**
     * the recipe time reduction multiplier
     */
    public double recipeTimeModifier = 1;

    /**
     * the terminal whose info this belongs to
     */
    private @Nullable CentrifugeTerminalEntity centrifugeTerminal;

    public CentrifugeState(@Nullable CentrifugeTerminalEntity centrifugeTerminal) {
        this.centrifugeTerminal = centrifugeTerminal;
    }

    @Override
    public void read(@NotNull Map<?, ?> data) {
        centrifugeActivity = CentrifugeActivity.fromByte((Byte) data.get("centrifugeActivity"));
        maxCentrifugeTier = CentrifugeTier.byName((String) data.get("maxCentrifugeTier"));
        energyStored = (Integer) data.get("energyStored");
        energyCapacity = (Integer) data.get("energyCapacity");
        numInputs = (Integer) data.get("numInputs");
        numItemOutputs = (Integer) data.get("numItemOutputs");
        numFluidOutputs = (Integer) data.get("numFluidOutputs");
        numEnergyPorts = (Integer) data.get("numEnergyPorts");
        numDumps = (Integer) data.get("numDumps");
        numGearboxes = (Integer) data.get("numGearboxes");
        numProcessors = (Integer) data.get("numProcessors");
        recipePowerModifier = (Integer) data.get("recipePowerModifier");
        recipeTimeModifier = (Double) data.get("recipeTimeModifier");
    }

    @Nullable
    @Override
    public Map<?, ?> write() {
        if (centrifugeTerminal != null) centrifugeTerminal.updateState();
        HashMap<String, Object> data = new HashMap<>();
        data.put("centrifugeActivity", centrifugeActivity.getByte());
        data.put("maxCentrifugeTier", maxCentrifugeTier.getSerializedName());
        //data.put("owner", owner.)   - not sure if we'll use this or not yet......
        data.put("energyStored", energyStored);
        data.put("energyCapacity", energyCapacity);
        data.put("numInputs", numInputs);
        data.put("numItemOutputs", numItemOutputs);
        data.put("numFluidOutputs", numFluidOutputs);
        data.put("numEnergyPorts", numEnergyPorts);
        data.put("numDumps", numDumps);
        data.put("numGearboxes", numGearboxes);
        data.put("numProcessors", numProcessors);
        data.put("recipePowerModifier", recipePowerModifier);
        data.put("recipeTimeModifier", recipeTimeModifier);

        return data;
    }
}
