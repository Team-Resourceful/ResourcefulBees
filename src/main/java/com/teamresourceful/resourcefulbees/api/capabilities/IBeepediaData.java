package com.teamresourceful.resourcefulbees.api.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public interface IBeepediaData extends INBTSerializable<CompoundNBT> {

    /**
     * gets all of the bees that the player has seen so far
     *
     * @return a set of resourceLocations of seen bees
     */
    Set<ResourceLocation> getBeeList();
}