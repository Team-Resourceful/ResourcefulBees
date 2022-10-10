package com.teamresourceful.resourcefulbees.api.beedata.breeding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @param families Gets the families defined in the associated bees json file.
 * @param feedItems Gets the feed items used to trigger the associated bees "love" state.
 * @param feedReturnItem Gets the item to be returned when feed items were used.
 * @param breedDelay Returns the amount of time in ticks that the bee must wait before being able to breed again.
 * @param feedAmount Returns the amount of items that must be fed to the bee to trigger its "love" state.
 * @param childGrowthDelay Returns the amount of time in ticks that the bee must wait before changing to an adult bee.
 */
public record BreedData(Set<BeeFamily> families, HolderSet<Item> feedItems, Optional<ItemStack> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay) {

    public static final BreedData DEFAULT = new BreedData(Collections.emptySet(), HolderSet.direct(Item::builtInRegistryHolder, Items.POPPY), Optional.empty(), 0, 0, 0);

    /**
     * A {@link Codec<BreedData>} that can be parsed to create a
     * {@link BreedData} object.
     */
    public static Codec<BreedData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                CodecExtras.set(BeeFamily.codec(name)).fieldOf("parents").orElse(new HashSet<>()).forGetter(BreedData::families),
                HolderSetCodec.of(Registry.ITEM).fieldOf("feedItem").orElse(HolderSet.direct(Item::builtInRegistryHolder, Items.POPPY)).forGetter(BreedData::feedItems),
                ItemStackCodec.CODEC.optionalFieldOf("feedReturnItem").forGetter(BreedData::feedReturnItem),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("feedAmount").orElse(1).forGetter(BreedData::feedAmount),
                Codec.intRange(Integer.MIN_VALUE, 0).fieldOf("childGrowthDelay").orElse(BeeConstants.CHILD_GROWTH_DELAY).forGetter(BreedData::childGrowthDelay),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("breedDelay").orElse(BeeConstants.BREED_DELAY).forGetter(BreedData::breedDelay)
        ).apply(instance, BreedData::new));
    }

    /**
     * This method returns <b>true</b> if the bee has no families defined
     * in its json file.
     *
     * @return Returns <b>true</b> if the bee has no families defined in its json file
     */
    public boolean hasParents() {
        return !families.isEmpty();
    }
}