package com.dungeonderps.resourcefulbees.config;

import javafx.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class BeeInfo {
    //These are needed for dynamic creation from JSON configs
    public static final LinkedHashMap<String, BeeInfo> BEE_INFO = new LinkedHashMap<>();
    public static final HashMap<Biome, Set<String>> SPAWNABLE_BIOMES = new HashMap<>();
    public static final HashMap<Pair, String> FAMILY_TREE = new HashMap<>();

    private String name, flower, color, biomeList, baseBlock, mutationBlock, centrifugeOutput, parent1, parent2;
    private boolean spawnInWorld, enderBee, netherBee, isChild;

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
    public void setIsChild(Boolean isChild) {this.isChild = isChild;}
    /**
     * Gets if the bee is a child.
     * @return if this bee is a child.
     */
    public boolean isChild() {return isChild;}

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
     * Returns new Resource Location with given input.
     *
     * @param resource Resource input as String in the form of "mod_id:item_or_block_id".
     * @return Returns New Resource Location for given input.
     */
    public ResourceLocation getResource(String resource){
        return new ResourceLocation(resource);
    }
}
