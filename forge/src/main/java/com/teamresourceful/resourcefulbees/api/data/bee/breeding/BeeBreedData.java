package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.Set;

public interface BeeBreedData extends BeeData<BeeBreedData> {

    Set<FamilyUnit> families();

    HolderSet<Item> feedItems();

    Optional<ItemStack> feedReturnItem();

    int feedAmount();

    int childGrowthDelay();

    int breedDelay();

    boolean hasParents();
}
