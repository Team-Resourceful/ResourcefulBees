package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class BeeInfo {
    //These are needed for dynamic creation from JSON configs
    public static final LinkedHashMap<String, BeeInfo> BEE_INFO = new LinkedHashMap<>();
    public static final HashMap<Biome, Set<String>> SPAWNABLE_BIOMES = new HashMap<>();
    public static final HashMap<Integer, String> FAMILY_TREE = new HashMap<>();

    private String flower, color, biomeList, baseBlock, mutationBlock, centrifugeOutput, parent1, parent2;
    private boolean spawnInWorld, enderBee, netherBee, breedable, creeperBee, skeletonBee, zomBee, pigmanBee, witherBee, blazeBee;

    private int maxTimeInHive = 2400;

    private double breedWeight = 0.33;

    private transient String name;
    private transient boolean enabled;
    public BeeInfo() {}

    /**
     * Gets the name/type of this bee.
     * @return this bee's name.
     */
    public String getName() { return name; }

    /**
     * Sets the name/type of this bee.
     * Can be used to modify bees through code.
     * @param name this bee's name.
     */
    public void setName(String name) { this.name = name; }

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

    /**
     *  **May be deprecated in favor of alternative method**
     * Primary use is for default-shipped bees.
     * Returns if this bee is enabled for use.
     * @return is this bee enabled?
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * **May be deprecated in favor of alternative method**
     * Primary use is for default-shipped bees.
     * Sets if this bee is enabled for use.
     * Can be used to modify bees through code.
     * @param enabled is this bee enabled?
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnderBee() { return enderBee; }

    public void setEnderBee(boolean enderBee) { this.enderBee = enderBee;}

    public boolean isNetherBee() { return netherBee; }

    public void setNetherBee(boolean netherBee) { this.netherBee = netherBee;}

    /**
     * Gets the Centrifuge Output for this bee's honeycomb.
     * @return This bee's honeycomb centrifuge output.
     */
    public String getCentrifugeOutput() {
        return centrifugeOutput.toLowerCase().trim();
    }

    /**
     * Sets the Centrifuge Output for this bee's honeycomb.
     * Can be used to modify bees through code.
     * @param centrifugeOutput This bee's honeycomb centrifuge output.
     */
    public void setCentrifugeOutput(String centrifugeOutput) {
        this.centrifugeOutput = centrifugeOutput;
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
        this.mutationBlock = mutationBlock;
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
        this.baseBlock = baseBlock;
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
        this.biomeList = biomeList;
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
    public void setParent1(String parent1) { this.parent1 = parent1; }

    /**
     * Gets this bee's first parent.
     * @return this bee's first parent.
     */
    public String getParent1() { return parent1; }

    /**
     * Sets this bee's second parent.
     * Can be used to modify bees through code.
     * @param parent2 this bee's second parent.
     */
    public void setParent2(String parent2) { this.parent2 = parent2; }

    /**
     * Gets this bee's second parent.
     * @return this bee's second parent.
     */
    public String getParent2() { return parent2; }

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


    /**
     * Returns an ArrayList of type String containing all information
     * about this bee. Useful method to have but not currently used anywhere.
     *
     *  @return Returns an ArrayList of type String containing all of this bee's information.
     */
    public ArrayList<String> getInfo() {
        ArrayList<String> s = new ArrayList<>();
        String i = spawnInWorld ? "true" : "false";
        s.add(color);
        s.add(flower);
        s.add(baseBlock);
        s.add(mutationBlock);
        s.add(centrifugeOutput);
        s.add(i);
        s.add(biomeList);
        return s;
    }

    /**
     * Returns a random bee from the BEE_INFO hashmap.
     * This is used for selecting a bee from all possible bees.
     *
     *  @return Returns random bee type as a string.
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public static String getRandomBee(){
        return BEE_INFO.get(BEE_INFO.keySet().toArray()[MathUtils.nextInt(BEE_INFO.size() - 1) +1]).getName();
    }

    /**
     * Returns a random bee from the SPAWNABLE_BIOMES hashmap.
     * This is used for in-world spawning based on biome.
     *
     *  @return Returns random bee type as a string.
     */
    public static String getRandomBee(Biome biome){
        ArrayList<String> spawnList = new ArrayList<>(BeeInfo.SPAWNABLE_BIOMES.get(biome));
        return BEE_INFO.get(spawnList.get(MathUtils.nextInt(spawnList.size()))).getName();
    }

    public static BeeInfo getInfo(String bee){
        BeeInfo info = BEE_INFO.get(bee);
        return info != null ? info : BEE_INFO.get(BeeConst.DEFAULT_BEE_TYPE);
    }
}
