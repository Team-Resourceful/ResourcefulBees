package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;

public class BreedData {
    private final boolean isBreedable;
    private final double breedWeight;
    private final String parent1, parent2;
    private final String feedItem;
    private final int feedAmount;

    private BreedData(boolean isBreedable, double breedWeight, String parent1, String parent2, String feedItem, int feedAmount) {
        this.isBreedable = isBreedable;
        this.breedWeight = breedWeight;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.feedItem = feedItem;
        this.feedAmount = feedAmount;
    }

    public boolean isBreedable() {
        return isBreedable;
    }

    public double getBreedWeight() {
        return breedWeight;
    }

    public String getParent1() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public String getFeedItem() {
        return feedItem;
    }

    public int getFeedAmount() {
        return feedAmount;
    }

    public static class Builder {
        private final boolean isBreedable;
        private double breedWeight = BeeConstants.DEFAULT_BREED_WEIGHT;
        private String parent1 = "", parent2 = "";
        private String feedItem = BeeConstants.FLOWER_TAG_ALL;
        private int feedAmount = 1;

        public Builder(boolean isBreedable) {
            this.isBreedable = isBreedable;
        }

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

        public BreedData createBreedData() {
            return new BreedData(isBreedable, breedWeight, parent1, parent2, feedItem, feedAmount);
        }
    }
}
