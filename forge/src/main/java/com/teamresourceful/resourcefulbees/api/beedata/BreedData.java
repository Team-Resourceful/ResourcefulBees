package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.lib.BeeConstants;
import net.minecraft.core.Registry;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Immutable
public class BreedData {
    public static final BreedData DEFAULT = new BreedData(Collections.emptySet(), Collections.emptySet(), Optional.empty(), 0, 0, 0);


    public static Codec<BreedData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.createSetCodec(BeeFamily.codec(name)).fieldOf("parents").orElse(new HashSet<>()).forGetter(BreedData::getParents),
                CodecUtils.ITEM_SET_CODEC.fieldOf("feedItem").orElse(Sets.newHashSet(Items.POPPY)).forGetter(BreedData::getFeedItems),
                Registry.ITEM.optionalFieldOf("feedReturnItem").forGetter(BreedData::getFeedReturnItem),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("feedAmount").orElse(1).forGetter(BreedData::getFeedAmount),
                Codec.intRange(Integer.MIN_VALUE, 0).fieldOf("childGrowthDelay").orElse(BeeConstants.CHILD_GROWTH_DELAY).forGetter(BreedData::getChildGrowthDelay),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("breedDelay").orElse(BeeConstants.BREED_DELAY).forGetter(BreedData::getBreedDelay)
        ).apply(instance, BreedData::new));
    }

    private final Set<BeeFamily> parents;

    /**
     * The item the parents need to be fed with for breeding.
     */
    private final Set<Item> items;

    /**
     * The item that gets returned to the player after the bee has been fed its {@link #items}
     */
    private final Optional<Item> feedReturnItem;

    /**
     * The amount the single parent needs to be feed with the item.
     */
    private final int feedAmount;

    /**
     * The time it takes the child to be an adult.
     */
    private final int childGrowthDelay;

    /**
     * The delay till the same bees can breed again.
     */
    private final int breedDelay;

    public BreedData(Set<BeeFamily> parents, Set<Item> items, Optional<Item> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay) {
        this.parents = parents;
        this.items = items;
        this.feedReturnItem = feedReturnItem;
        this.feedAmount = feedAmount;
        this.childGrowthDelay = childGrowthDelay;
        this.breedDelay = breedDelay;
    }

    public Set<BeeFamily> getParents() {
        return parents;
    }

    public boolean hasParents() {
        return !parents.isEmpty();
    }

    public Set<Item> getFeedItems() {
        return items;
    }

    public Set<ItemStack> getFeedItemStacks() {
        return getFeedItems().stream()
                .map(item -> new ItemStack(item, getFeedAmount()))
                .collect(Collectors.toSet());
    }

    public Optional<Item> getFeedReturnItem() {
        return feedReturnItem;
    }

    public int getFeedAmount() {
        return feedAmount;
    }

    public int getChildGrowthDelay() {
        return childGrowthDelay;
    }

    public int getBreedDelay() {
        return breedDelay;
    }
}