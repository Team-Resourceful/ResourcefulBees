package com.teamresourceful.resourcefulbees.api.beedata.breeding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.api.beedata.itemsholder.IItemHolder;
import com.teamresourceful.resourcefulbees.api.beedata.itemsholder.ItemHolder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Unmodifiable
public class BreedData {
    public static final BreedData DEFAULT = new BreedData(Collections.emptySet(), new ItemHolder(Items.POPPY), Optional.empty(), 0, 0, 0);

    /**
     * A {@link Codec<BreedData>} that can be parsed to create a
     * {@link BreedData} object.
     */
    public static Codec<BreedData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.createSetCodec(BeeFamily.codec(name)).fieldOf("parents").orElse(new HashSet<>()).forGetter(BreedData::getFamilies),
                CodecUtils.ITEM_HOLDER_CODEC.fieldOf("feedItem").orElse(new ItemHolder(Items.POPPY)).forGetter(BreedData::getFeedItems),
                CodecUtils.ITEM_STACK_CODEC.optionalFieldOf("feedReturnItem").forGetter(BreedData::getFeedReturnItem),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("feedAmount").orElse(1).forGetter(BreedData::getFeedAmount),
                Codec.intRange(Integer.MIN_VALUE, 0).fieldOf("childGrowthDelay").orElse(BeeConstants.CHILD_GROWTH_DELAY).forGetter(BreedData::getChildGrowthDelay),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("breedDelay").orElse(BeeConstants.BREED_DELAY).forGetter(BreedData::getBreedDelay)
        ).apply(instance, BreedData::new));
    }

    protected Set<BeeFamily> families;
    protected IItemHolder itemHolder;
    protected Optional<ItemStack> feedReturnItem;
    protected int feedAmount;
    protected int childGrowthDelay;
    protected int breedDelay;

    public BreedData(Set<BeeFamily> families, IItemHolder itemHolder, Optional<ItemStack> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay) {
        this.families = families;
        this.itemHolder = itemHolder;
        this.feedReturnItem = feedReturnItem;
        this.feedAmount = feedAmount;
        this.childGrowthDelay = childGrowthDelay;
        this.breedDelay = breedDelay;
    }

    /**
     * Gets the families defined in the associated bees json file. This
     * method returns a {@link Set}<{@link BeeFamily}>.
     *
     * @return Returns a {@link Set}<{@link BeeFamily}>.
     */
    public Set<BeeFamily> getFamilies() {
        return families;
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

    /**
     * Gets the feed items used to trigger the associated bees "love" state.
     * The value returned is a {@link IItemHolder}>.
     *
     * @return Returns a {@link IItemHolder}.
     */
    public IItemHolder getFeedItems() {
        return itemHolder;
    }

    /**
     * @return returns the leftovers from feeding the bee
     */
    public Optional<ItemStack> getFeedReturnItem() {
        return feedReturnItem;
    }

    /**
     * Returns the amount of items that must be fed to the bee to
     * trigger its "love" state.
     *
     * @return Returns the amount of items that must be fed to the bee to
     * trigger its "love" state.
     */
    public int getFeedAmount() {
        return feedAmount;
    }

    /**
     * Returns the amount of time in ticks that the bee must wait before
     * changing to an adult bee.
     *
     * @return Returns the amount of time in ticks that the bee must wait before
     * changing to an adult bee.
     */
    public int getChildGrowthDelay() {
        return childGrowthDelay;
    }

    /**
     * Returns the amount of time in ticks that the bee must wait before
     * being able to breed again.
     *
     * @return Returns the amount of time in ticks that the bee must wait before
     * being able to breed again.
     */
    public int getBreedDelay() {
        return breedDelay;
    }

    public BreedData toImmutable() {
        return this;
    }

    public static class Mutable extends BreedData {

        public Mutable(Set<BeeFamily> families, IItemHolder items, Optional<ItemStack> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay) {
            super(families, items, feedReturnItem, feedAmount, childGrowthDelay, breedDelay);
        }

        public Mutable() {
            super(new HashSet<>(), new ItemHolder(Items.POPPY), Optional.empty(), 0, 0, 0);
        }

        public Mutable setFamilies(Set<BeeFamily> families) {
            this.families = families;
            return this;
        }

        public Mutable setItems(IItemHolder itemHolder) {
            this.itemHolder = itemHolder;
            return this;
        }

        public Mutable setFeedReturnItem(Optional<ItemStack> feedReturnItem) {
            this.feedReturnItem = feedReturnItem;
            return this;
        }

        public Mutable setFeedAmount(int feedAmount) {
            this.feedAmount = feedAmount;
            return this;
        }

        public Mutable setChildGrowthDelay(int childGrowthDelay) {
            this.childGrowthDelay = childGrowthDelay;
            return this;
        }

        public Mutable setBreedDelay(int breedDelay) {
            this.breedDelay = breedDelay;
            return this;
        }

        @Override
        public BreedData toImmutable() {
            return new BreedData(this.families, this.itemHolder, this.feedReturnItem, this.feedAmount, this.childGrowthDelay, this.breedDelay);        }
    }
}