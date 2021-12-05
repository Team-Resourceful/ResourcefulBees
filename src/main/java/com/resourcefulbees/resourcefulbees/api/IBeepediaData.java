package com.resourcefulbees.resourcefulbees.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public interface IBeepediaData extends INBTSerializable<CompoundNBT> {

    /**
     * gets all of the bees that the player has seen so far
     *
     * @return a set of resourceLocations of seen bees
     */
    Set<String> getBeeList();

    void setBeeList(Set<String> bees);
}