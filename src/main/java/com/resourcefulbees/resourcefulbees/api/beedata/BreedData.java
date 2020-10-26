package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;

public class BreedData extends AbstractBeeData {
    /**
     * If bee can be breeded from 2 parents.
     */
    private boolean isBreedable;

    /**
     * The weight specified for that bee. The weight is calculated against all the other bees the bees parents can make with the same item.
     */
    private final double breedWeight;

    /**
     * Strings of the parent bees needed to be breed.
     */
    private final String parent1, parent2;

    /**
     * The item the parents need to be fed with for breeding.
     */
    private final String feedItem;

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

    private BreedData(boolean isBreedable, double breedWeight, String parent1, String parent2, String feedItem, int feedAmount, int childGrowthDelay, int breedDelay) {
        this.isBreedable = isBreedable;
        this.breedWeight = breedWeight;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.feedItem = feedItem;
        this.feedAmount = feedAmount;
        this.childGrowthDelay = childGrowthDelay;
        this.breedDelay = breedDelay;
    }

    public boolean isBreedable() { return isBreedable; }

    public void setBreedable(boolean breedable) { isBreedable = breedable; }

    public double getBreedWeight() { return breedWeight <= 0 ? BeeConstants.DEFAULT_BREED_WEIGHT : breedWeight; }

    public String getParent1() { return parent1 != null ? parent1.toLowerCase() : ""; }

    public String getParent2() { return parent2 != null ? parent2.toLowerCase() : ""; }

    public String getFeedItem() { return feedItem != null ? feedItem.toLowerCase() : BeeConstants.FLOWER_TAG_ALL; }

    public int getFeedAmount() { return Math.max(1, feedAmount); }

    public int getChildGrowthDelay() { return childGrowthDelay != 0 ? BeeConstants.CHILD_GROWTH_DELAY : childGrowthDelay; }

    public int getBreedDelay() { return breedDelay != 0 ? BeeConstants.BREED_DELAY : breedDelay; }

    public boolean hasParents() { return !getParent1().isEmpty() && !getParent2().isEmpty(); }

    /**
     * Builder to easily create new BreedData
     */
    public static class Builder {
        private final boolean isBreedable;
        private double breedWeight;
        private String parent1, parent2;
        private String feedItem;
        private int feedAmount;
        private int childGrowthDelay;
        private int breedDelay;

        public Builder(boolean isBreedable) { this.isBreedable = isBreedable; }

        public Builder setBreedWeight(double breedWeight) {
            this.breedWeight = breedWeight;
            return this;
        }

        public Builder setParent1(String parent1) {
            this.parent1 = parent1;
            return this;
        }

        public Builder setParent2(String parent2) {
            this.parent2 = parent2;
            return this;
        }

        public Builder setFeedItem(String feedItem) {
            this.feedItem = feedItem;
            return this;
        }

        public Builder setFeedAmount(int feedAmount) {
            this.feedAmount = feedAmount;
            return this;
        }

        public Builder setChildGrowthDelay(int childGrowthDelay) {
            this.childGrowthDelay = childGrowthDelay;
            return this;
        }

        public Builder setBreedDelay(int breedDelay) {
            this.breedDelay = breedDelay;
            return this;
        }

        public BreedData createBreedData() {
            return new BreedData(isBreedable, breedWeight, parent1, parent2, feedItem, feedAmount, childGrowthDelay, breedDelay);
        }
    }

    /**
     * Creates a default BreedData for faster Bee Creation
     * @return BreedData that says Bee can't be breed
     */
    public static BreedData createDefault() {
        return new Builder(false).createBreedData();
    }
}
