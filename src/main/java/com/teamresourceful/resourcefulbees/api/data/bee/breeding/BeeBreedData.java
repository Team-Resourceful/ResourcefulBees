package com.teamresourceful.resourcefulbees.api.data.bee.breeding;

import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

@NullMarked
public interface BeeBreedData extends BeeData<BeeBreedData> {

    Set<FamilyUnit> families();

    RegistryPredicate<Item> feedItems();

    Optional<ItemStackTemplate> feedReturnItem();

    int feedAmount();

    int childGrowthDelay();

    int breedDelay();

    boolean hasParents();

    default boolean isFood(ItemStack stack) {
        return feedItems().test(stack);
    }
}
