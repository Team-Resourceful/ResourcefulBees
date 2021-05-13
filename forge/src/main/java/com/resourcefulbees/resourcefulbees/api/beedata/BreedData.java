package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class BreedData {

    //Consider modifying this class such that parents are two arrays of strings and feedReturnItem is an Optional
    //

    public static final Codec<BreedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isBreedable").orElse(false).forGetter(BreedData::isBreedable),
            Codec.DOUBLE.fieldOf("breedWeight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BreedData::getBreedWeight),
            Codec.FLOAT.fieldOf("breedChance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BreedData::getBreedChance),
            Codec.STRING.listOf().fieldOf("parent1").orElse(new ArrayList<>()).forGetter(BreedData::getParent1),
            Codec.STRING.listOf().fieldOf("parent2").orElse(new ArrayList<>()).forGetter(BreedData::getParent2),
            CodecUtils.ITEM_SET_CODEC.fieldOf("feedItem").orElse(new HashSet<>()).forGetter(BreedData::getFeedItems),
            Registry.ITEM.optionalFieldOf("feedReturnItem").forGetter(BreedData::getFeedReturnItem),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("feedAmount").orElse(1).forGetter(BreedData::getFeedAmount),
            Codec.intRange(Integer.MIN_VALUE, 0).fieldOf("childGrowthDelay").orElse(BeeConstants.CHILD_GROWTH_DELAY).forGetter(BreedData::getChildGrowthDelay),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("breedDelay").orElse(BeeConstants.BREED_DELAY).forGetter(BreedData::getBreedDelay)
    ).apply(instance, BreedData::new));

    /**
     * If bee can be bred from 2 parents.
     */
    private final boolean isBreedable;

    /**
     * The weight specified for that bee. The weight is calculated against all the other bees the bees parents can make with the same item.
     */
    private final double breedWeight;

    /**
     * The the chance that when trying to breed this bee the breed will succeed, if the breed fails, items will be consumed but no bee will be made.
     */
    private final float breedChance;

    /**
     * Strings of the parent bees needed to be breed.
     */
    private final List<String> parent1;
    private final List<String> parent2;

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

    public BreedData(boolean isBreedable, double breedWeight, float breedChance, List<String> parent1, List<String> parent2, Set<Item> items, Optional<Item> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay) {
        this.isBreedable = isBreedable;
        this.breedWeight = breedWeight;
        this.breedChance = breedChance;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.items = items;
        this.feedReturnItem = feedReturnItem;
        this.feedAmount = feedAmount;
        this.childGrowthDelay = childGrowthDelay;
        this.breedDelay = breedDelay;
    }

    public boolean isBreedable() {
        return isBreedable;
    }

    public double getBreedWeight() {
        return breedWeight;
    }

    public float getBreedChance() {
        return breedChance;
    }

    public List<String> getParent1() {
        return parent1;
    }

    public List<String> getParent2() {
        return parent2;
    }

    public boolean hasParents() {
        return !parent1.isEmpty() && !parent2.isEmpty();
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

    public static BreedData createDefault() {
        return new BreedData(false, 0, 0, Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Optional.empty(), 0, 0, 0);
    }


}
