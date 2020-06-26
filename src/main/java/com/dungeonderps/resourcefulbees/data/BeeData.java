package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.lib.MutationTypes;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.Color;
import net.minecraft.item.Items;

public class BeeData {

    private String flower, parent1, parent2;

    private String biomeWhitelist = "";
    private String biomeBlacklist = "";

    private String baseLayerTexture = "/custom/bee";
    private String primaryColor = Color.format(BeeConst.DEFAULT_COLOR);
    private String secondaryColor = Color.format(BeeConst.DEFAULT_SECONDARY_COLOR);
    private String honeycombColor = Color.format(BeeConst.DEFAULT_COLOR);
    private String primaryLayerTexture = "/custom/primary_layer";
    private String secondaryLayerTexture = "/custom/secondary_layer";

    private boolean isBeeColored = true;


    private String mutationInput = "";
    private String mutationOutput = "";

    private String mainOutput = "";

    private String secondaryOutput = RegistryHandler.BEESWAX.getId().toString();
    private String bottleOutput = Items.HONEY_BOTTLE.getRegistryName().toString();

    private boolean spawnInWorld, enderBee, netherBee, breedable, creeperBee, skeletonBee, zomBee, pigmanBee, witherBee, blazeBee;

    private int maxTimeInHive = BeeConst.MAX_TIME_IN_HIVE;
    private double breedWeight = BeeConst.DEFAULT_BREED_WEIGHT;

    private double mainOutputWeight = BeeConst.DEFAULT_MAIN_OUTPUT_WEIGHT;
    private double secondaryOutputWeight = BeeConst.DEFAULT_SEC_OUTPUT_WEIGHT;
    private double bottleOutputWeight = BeeConst.DEFAULT_BOT_OUTPUT_WEIGHT;

    private int mainOutputCount = 1;
    private int secondaryOutputCount = 1;
    private int bottleOutputCount = 1;


    private float sizeModifier = 1.0f;

    private transient String name = BeeConst.DEFAULT_BEE_TYPE;
    private transient boolean mutation;
    private transient Enum<MutationTypes> mutationType;

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
    public String getHoneycombColor() {
        return honeycombColor;
    }

    /**
     * Sets the color for this bee and its honeycomb.
     * Can be used to modify bees through code.
     * @param honeycombColor this bee's color.
     */
    public void setHoneycombColor(String honeycombColor) {
        this.honeycombColor = honeycombColor;
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
    public String getMutationOutput() {
        return mutationOutput.toLowerCase().trim();
    }

    /**
     * Sets the Mutation Block for this bee.
     * Bees can only have *ONE* Mutation Block.
     * Can be used to modify bees through code.
     * @param mutationOutput this bee's Mutation Block.
     */
    public void setMutationOutput(String mutationOutput) {
        this.mutationOutput = mutationOutput.toLowerCase().trim();
    }

    /**
     * Gets the block this bee can apply "pollination" effects to.
     * Similar to vanilla bees applying growth ticks,
     * the Base Block can instead be "mutated" into a different block.
     * @return this bee's Base Block.
     */
    public String getMutationInput() {
        return mutationInput.toLowerCase().trim();
    }

    /**
     * Sets the block this bee can apply "pollination" effects to.
     * Similar to vanilla bees applying growth ticks,
     * the Base Block can instead be "mutated" into a different block.
     * Can be used to modify bees through code.
     * @param mutationInput this bee's flower(s).
     */
    public void setMutationInput(String mutationInput) {
        this.mutationInput = mutationInput.toLowerCase().trim();
    }

    /**
     * Gets the biome(s) this bee can spawn in.
     * @return this bee's spawnable biome(s).
     */
    public String getBiomeWhitelist() {
        return biomeWhitelist.toLowerCase().trim();
    }

    /**
     * Sets the biome(s) this bee can spawn in.
     * Can be used to modify bees through code.
     * @param biomeWhitelist this bee's spawnable biome(s).
     */
    public void setBiomeWhitelist(String biomeWhitelist) {
        this.biomeWhitelist = biomeWhitelist.toLowerCase().trim();
    }

    /**
     * Gets if the bee is a child.
     * @return if this bee is a child.
     */
    public boolean isBreedable() {return breedable;}

    /**
     * Sets if this bee is a child of 2 parents.
     * Can be used to modify bees through code.
     * @param isChild this bee's a child.
     */
    public void setBreedable(Boolean isChild) {this.breedable = isChild;}

    /**
     * Gets this bee's first parent.
     * @return this bee's first parent.
     */
    public String getParent1() { return parent1.toLowerCase(); }

    /**
     * Sets this bee's first parent.
     * Can be used to modify bees through code.
     * @param parent1 this bee's first parent.
     */
    public void setParent1(String parent1) { this.parent1 = parent1.toLowerCase(); }

    /**
     * Gets this bee's second parent.
     * @return this bee's second parent.
     */
    public String getParent2() { return parent2.toLowerCase(); }

    /**
     * Sets this bee's second parent.
     * Can be used to modify bees through code.
     * @param parent2 this bee's second parent.
     */
    public void setParent2(String parent2) { this.parent2 = parent2.toLowerCase(); }

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

    //set during validation
    public boolean hasMutation() {
        return mutation;
    }

    //set during validation
    public void setMutation(boolean mutation) {
        this.mutation = mutation;
    }

    public Enum<MutationTypes> getMutationType() {
        return mutationType;
    }

    public void setMutationType(Enum<MutationTypes> mutationType) {
        this.mutationType = mutationType;
    }

    public String getBaseLayerTexture() {
        return baseLayerTexture;
    }

    public void setBaseLayerTexture(String baseLayerTexture) {
        this.baseLayerTexture = baseLayerTexture;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getPrimaryLayerTexture() {
        return primaryLayerTexture;
    }

    public void setPrimaryLayerTexture(String primaryLayerTexture) {
        this.primaryLayerTexture = primaryLayerTexture;
    }

    public String getSecondaryLayerTexture() {
        return secondaryLayerTexture;
    }

    public void setSecondaryLayerTexture(String secondaryLayerTexture) {
        this.secondaryLayerTexture = secondaryLayerTexture;
    }

    public boolean isBeeColored() {
        return isBeeColored;
    }

    public void setBeeColored(boolean beeColored) {
        isBeeColored = beeColored;
    }

    public float getSizeModifier() {
        return sizeModifier;
    }

    public void setSizeModifier(float sizeModifier) {
        this.sizeModifier = sizeModifier;
    }

    public String getBiomeBlacklist() {
        return biomeBlacklist;
    }

    public void setBiomeBlacklist(String biomeBlacklist) {
        this.biomeBlacklist = biomeBlacklist;
    }

    public int getMainOutputCount() { return mainOutputCount; }

    public void setMainOutputCount(int mainOutputCount) { this.mainOutputCount = mainOutputCount; }

    public int getSecondaryOutputCount() { return secondaryOutputCount; }

    public void setSecondaryOutputCount(int secondaryOutputCount) { this.secondaryOutputCount = secondaryOutputCount; }

    public int getBottleOutputCount() { return bottleOutputCount; }

    public void setBottleOutputCount(int bottleOutputCount) { this.bottleOutputCount = bottleOutputCount; }
}
