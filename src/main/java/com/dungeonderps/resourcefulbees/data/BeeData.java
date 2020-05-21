package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.item.Items;

public class BeeData {

    private String flower, color, biomeList, parent1, parent2;

    private String baseBlock = "";
    private String mutationBlock = "";
    private String mainOutput = "";

    private String secondaryOutput = RegistryHandler.BEESWAX.getId().toString();
    private String bottleOutput = Items.HONEY_BOTTLE.getRegistryName().toString();

    private boolean spawnInWorld, enderBee, netherBee, breedable, creeperBee, skeletonBee, zomBee, pigmanBee, witherBee, blazeBee;

    private int maxTimeInHive = BeeConst.MAX_TIME_IN_HIVE;
    private double breedWeight = BeeConst.DEFAULT_BREED_WEIGHT;
    private double mainOutputWeight = BeeConst.DEFAULT_MAIN_OUTPUT_WEIGHT;
    private double secondaryOutputWeight = BeeConst.DEFAULT_SEC_OUTPUT_WEIGHT;
    private double bottleOutputWeight = BeeConst.DEFAULT_BOT_OUTPUT_WEIGHT;

    private transient String name = BeeConst.DEFAULT_BEE_TYPE;


    public BeeData() {}

    /**
     * Gets the name/type of this bee.
     * @return this bee's name.
     */
    public String getName() { return name.toLowerCase(); }

    /**
     * Sets the name/type of this bee.
     * Can be used to modify bees through code.
     * @param name this bee's name.
     */
    public void setName(String name) { this.name = name.toLowerCase(); }

    /**
     * Gets the flower(s) this bee can pollinate.
     * @return this bee's flower(s).
     */
    public String getFlower() {
        return flower.toLowerCase().trim();
    }

    /**
     * Sets the flower(s) this bee can pollinate.
     * Can be used to modify bees through code.
     * @param flower this bee's flower(s).
     */
    public void setFlower(String flower) {
        this.flower = flower;
    }

    /**
     * Gets the color for this bee and its honeycomb.
     * @return this bee's color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color for this bee and its honeycomb.
     * Can be used to modify bees through code.
     * @param color this bee's color.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns whether this bee can naturally spawn in world.
     * @return can this bee spawn naturally in world?
     */
    public boolean canSpawnInWorld() {
        return spawnInWorld;
    }

    /**
     * Sets whether this bee can naturally spawn in world.
     * Can be used to modify bees through code.
     * @param spawnInWorld can this bee spawn in world?
     */
    public void setSpawnInWorld(boolean spawnInWorld) {
        this.spawnInWorld = spawnInWorld;
    }

    public boolean isEnderBee() { return enderBee; }

    public void setEnderBee(boolean enderBee) { this.enderBee = enderBee;}

    public boolean isNetherBee() { return netherBee; }

    public void setNetherBee(boolean netherBee) { this.netherBee = netherBee;}

    /**
     * Gets the Centrifuge Output for this bee's honeycomb.
     * @return This bee's honeycomb centrifuge output.
     */
    public String getMainOutput() {
        return mainOutput.toLowerCase().trim();
    }

    /**
     * Sets the Centrifuge Output for this bee's honeycomb.
     * Can be used to modify bees through code.
     * @param mainOutput This bee's honeycomb centrifuge output.
     */
    public void setMainOutput(String mainOutput) {
        this.mainOutput = mainOutput.toLowerCase();
    }

    /**
     * Gets the Centrifuge Secondary Output for this bee's honeycomb.
     * @return This bee's secondary centrifuge output.
     */
    public String getSecondaryOutput() {
        return secondaryOutput.toLowerCase();
    }

    /**
     * Sets the Centrifuge Secondary Output for this bee's honeycomb.
     * Can be used to modify bees through code.
     * @param secondaryOutput This bee's secondary centrifuge output.
     */
    public void setSecondaryOutput(String secondaryOutput) {
        this.secondaryOutput = secondaryOutput.toLowerCase();
    }

    /**
     * Gets the Centrifuge Bottle Output for this bee's honeycomb.
     * @return This bee's bottle centrifuge output.
     */
    public String getBottleOutput() {
        return bottleOutput.toLowerCase();
    }

    /**
     * Sets the Centrifuge Bottle Output for this bee's honeycomb.
     * Can be used to modify bees through code.
     * @param bottleOutput This bee's bottle centrifuge output.
     */
    public void setBottleOutput(String bottleOutput) {
        this.bottleOutput = bottleOutput.toLowerCase();
    }

    /**
     * Gets the main output wighting for centrifuge recipes.
     * @return This bee's bottle centrifuge output weight.
     */
    public double getMainOutputWeight() {
        return mainOutputWeight;
    }

    /**
     * Sets the main output wighting for centrifuge recipes.
     * Can be used to modify bees through code.
     * @param mainOutputWeight This bee's main centrifuge output weight.
     */
    public void setMainOutputWeight(double mainOutputWeight) {
        this.mainOutputWeight = mainOutputWeight;
    }

    /**
     * Gets the secondary output wighting for centrifuge recipes.
     * @return This bee's bottle centrifuge output weight.
     */
    public double getSecondaryOutputWeight() {
        return secondaryOutputWeight;
    }

    /**
     * Sets the secondary output wighting for centrifuge recipes.
     * Can be used to modify bees through code.
     * @param secondaryOutputWeight This bee's secondary centrifuge output weight.
     */
    public void setSecondaryOutputWeight(double secondaryOutputWeight) {
        this.secondaryOutputWeight = secondaryOutputWeight;
    }

    /**
     * Gets the secondary output wighting for centrifuge recipes.
     * @return This bee's bottle centrifuge output weight.
     */
    public double getBottleOutputWeight() {
        return bottleOutputWeight;
    }

    /**
     * Sets the bottle output wighting for centrifuge recipes.
     * Can be used to modify bees through code.
     * @param bottleOutputWeight This bee's bottle centrifuge output weight.
     */
    public void setBottleOutputWeight(double bottleOutputWeight) {
        this.bottleOutputWeight = bottleOutputWeight;
    }

    /**
     * Gets the Mutation Block for this bee.
     * Bees can only have *ONE* Mutation Block.
     * @return this bee's Mutation Block.
     */
    public String getMutationBlock() {
        return mutationBlock.toLowerCase().trim();
    }

    /**
     * Sets the Mutation Block for this bee.
     * Bees can only have *ONE* Mutation Block.
     * Can be used to modify bees through code.
     * @param mutationBlock this bee's Mutation Block.
     */
    public void setMutationBlock(String mutationBlock) {
        this.mutationBlock = mutationBlock.toLowerCase().trim();
    }

    /**
     * Gets the block this bee can apply "pollination" effects to.
     * Similar to vanilla bees applying growth ticks,
     * the Base Block can instead be "mutated" into a different block.
     * @return this bee's Base Block.
     */
    public String getBaseBlock() {
        return baseBlock.toLowerCase().trim();
    }

    /**
     * Sets the block this bee can apply "pollination" effects to.
     * Similar to vanilla bees applying growth ticks,
     * the Base Block can instead be "mutated" into a different block.
     * Can be used to modify bees through code.
     * @param baseBlock this bee's flower(s).
     */
    public void setBaseBlock(String baseBlock) {
        this.baseBlock = baseBlock.toLowerCase().trim();
    }

    /**
     * Gets the biome(s) this bee can spawn in.
     * @return this bee's spawnable biome(s).
     */
    public String getBiomeList() {
        return biomeList.toLowerCase().trim();
    }

    /**
     * Sets the biome(s) this bee can spawn in.
     * Can be used to modify bees through code.
     * @param biomeList this bee's spawnable biome(s).
     */
    public void setBiomeList(String biomeList) {
        this.biomeList = biomeList.toLowerCase().trim();
    }

    /**
     * Sets if this bee is a child of 2 parents.
     * Can be used to modify bees through code.
     * @param isChild this bee's a child.
     */
    public void setBreedable(Boolean isChild) {this.breedable = isChild;}

    /**
     * Gets if the bee is a child.
     * @return if this bee is a child.
     */
    public boolean isBreedable() {return breedable;}

    /**
     * Sets this bee's first parent.
     * Can be used to modify bees through code.
     * @param parent1 this bee's first parent.
     */
    public void setParent1(String parent1) { this.parent1 = parent1.toLowerCase(); }

    /**
     * Gets this bee's first parent.
     * @return this bee's first parent.
     */
    public String getParent1() { return parent1.toLowerCase(); }

    /**
     * Sets this bee's second parent.
     * Can be used to modify bees through code.
     * @param parent2 this bee's second parent.
     */
    public void setParent2(String parent2) { this.parent2 = parent2.toLowerCase(); }

    /**
     * Gets this bee's second parent.
     * @return this bee's second parent.
     */
    public String getParent2() { return parent2.toLowerCase(); }

    /**
     * Returns true if bee has the creeper trait.
     * @return is this a creeper bee?
     */
    public boolean isCreeperBee() { return creeperBee; }

    /**
     * Returns true if bee has the skeleton trait.
     * @return is this a skeleton bee?
     */
    public boolean isSkeletonBee() { return skeletonBee; }

    /**
     * Returns true if bee has the zombie trait.
     * @return is this a zombie bee?
     */
    public boolean isZomBee() { return zomBee; }

    /**
     * Returns true if bee has the pigman trait.
     * @return is this a pigman bee?
     */
    public boolean isPigmanBee() { return pigmanBee; }

    /**
     * Returns true if bee has the wither trait.
     * @return is this a wither bee?
     */
    public boolean isWitherBee() { return witherBee; }

    /**
     * Returns true if bee has the blaze trait.
     * @return is this a blaze bee?
     */
    public boolean isBlazeBee() { return blazeBee; }

    /**
     * Returns the max time bee a has to be in the hive to produce a honeycomb.
     * @return Gets this bee's max time in hive in ticks.
     */
    public int getMaxTimeInHive() {
        return maxTimeInHive;
    }

    /**
     * Sets the max time bee a has to be in the hive to produce a honeycomb.
     * @param maxTimeInHive Sets this bee's max time in hive in ticks.
     */
    public void setMaxTimeInHive(int maxTimeInHive) {
        this.maxTimeInHive = maxTimeInHive;
    }

    /**
     * A 0 -> 1 Float value weighting for bee breeding
     * @return Gets this bee's breed weight.
     */
    public double getBreedWeight() {
        return breedWeight;
    }

    /**
     * Returns true if bee has the blaze trait.
     * @param breedWeight Sets this bee's breed weight.
     */
    public void setBreedWeight(double breedWeight) {
        this.breedWeight = breedWeight;
    }
}
