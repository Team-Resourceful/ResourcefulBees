package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.utils.Color;
import com.google.common.base.Splitter;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.regex.Pattern;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class BeeInfo {
    //These are needed for dynamic creation from JSON configs
    public static final LinkedHashMap<String, BeeInfo> BEE_INFO = new LinkedHashMap<>();
    public static final HashMap<Biome, Set<String>> SPAWNABLE_BIOMES = new HashMap<>();
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("(tag:)?(\\w+):(\\w+/\\w+|\\w+)", Pattern.CASE_INSENSITIVE);

    private String name, flower, color, biomeList, baseBlock, mutationBlock, centrifugeOutput;
    private boolean spawnInWorld, enderBee;

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
     * @param mutationBlock this bee's mutationBlock.
     */
    public void setMutationBlock(String mutationBlock) {
        this.mutationBlock = mutationBlock;
    }

    /**
     * Gets the block this bee can apply "pollination" effects to.
     * Similar to vanilla bees applying growth ticks,
     * the Base Block can instead be "mutated" into a different block.
     * TODO Add TAG support
     * @return this bee's Base Block.
     */
    public String getBaseBlock() {
        return baseBlock.toLowerCase().trim();
    }

    /**
     * Sets the block this bee can apply "pollination" effects to.
     * Similar to vanilla bees applying growth ticks,
     * the Base Block can instead be "mutated" into a different block.
     * TODO Add TAG support
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

    public static void parseBiomeList(BeeInfo bee){
        if (bee.getBiomeList().contains("tag:")){
            //list with parsed biome tags
            List<String> biomeList = Splitter.on(',').trimResults().splitToList(bee.getBiomeList().replace("tag:",""));
            for(String type : biomeList){
                //creates set containing all biomes of given type
                Set<Biome> biomeSet = BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(type));
                updateSpawnableBiomes(biomeSet,bee);
            }

        } else {
            List<String> biomeList = Splitter.on(',').trimResults().splitToList(bee.getBiomeList());
            Set<Biome> biomeSet = new HashSet<>();
            for(String biome : biomeList){
                //creates set containing all biomes
                biomeSet.add(ForgeRegistries.BIOMES.getValue(new ResourceLocation(biome)));
            }
            updateSpawnableBiomes(biomeSet,bee);
        }
    }

    private static void updateSpawnableBiomes(Set<Biome> biomeSet, BeeInfo bee){
        for(Biome biome : biomeSet){
            //checks to see if spawnable biomes map contains current biome,
            //if so then adds bee to array value, otherwise creates new key
            if(biome != null){
                BeeInfo.SPAWNABLE_BIOMES.computeIfAbsent(biome,k -> new HashSet<>()).add(bee.getName());
            } else {
                LOGGER.error("");
            }
        }
    }


    public boolean validate() {
        boolean isValid = true;

        isValid = isValid && validateColor();
        isValid = isValid && validateFlower();
        isValid = isValid && validateBaseBlock();
        isValid = isValid && validateMutationBlock();
        //isValid = isValid ? validateBiomeList() : false;
        isValid = isValid && validateCentrifugeOutput();

        return isValid;
    }

    private boolean validateCentrifugeOutput() {
        if (RESOURCE_PATTERN.matcher(getMutationBlock()).matches()){
            if (ForgeRegistries.ITEMS.getValue(getResource(getCentrifugeOutput())) != null){
                LOGGER.debug(getName() + " Bee Centrifuge Output Check Passed!");
                return true;
            }
        }
        LOGGER.error(getName() + " Bee Centrifuge Output Check Failed! Please check JSON!\nCurrent value: " + getCentrifugeOutput() + " is not a valid item");
        return false;
    }

    private boolean validateMutationBlock() {
        if (RESOURCE_PATTERN.matcher(getMutationBlock()).matches()){
            if (ForgeRegistries.BLOCKS.getValue(getResource(getMutationBlock())) != null){
                LOGGER.debug(getName() + " Bee Mutation Block Check Passed!");
                return true;
            }
        }
        LOGGER.error(getName() + " Bee Mutation Block Check Failed! Please check JSON!\nCurrent value: " + getMutationBlock() + " is not a valid block");
        return false;
    }

    private boolean validateBaseBlock() {
        if(RESOURCE_PATTERN.matcher(getBaseBlock()).matches()){
            if (getBaseBlock().contains("tag:")){
                String cleanBaseBlock = getBaseBlock().replace("tag:", "");
                if (BlockTags.getCollection().get(getResource(cleanBaseBlock)) != null){
                    LOGGER.debug(getName() + " Bee BaseBlock Check Passed!");
                    return true;
                } else {
                    LOGGER.error(getName() + " Bee BaseBlock Check Failed! Please check JSON!\nCurrent value: " + getBaseBlock() + " is not a valid tag");
                    return false;
                }
            } else if (ForgeRegistries.BLOCKS.getValue(getResource(getBaseBlock())) != null){
                LOGGER.debug(getName() + "Bee BaseBlock Check Passed!");
                return true;
            }
        }
        LOGGER.error(getName() + " Bee BaseBlock Check Failed! Please check JSON!\nCurrent value: " + getBaseBlock() + " is not a valid block");
        return false;
    }

    private boolean validateFlower() {
        if (getFlower().equals("all") || getFlower().equals("small") || getFlower().equals("tall") || ForgeRegistries.BLOCKS.getValue(getResource(getFlower())) != null) {
            LOGGER.debug(getName() + " Bee Flower Check Passed!");
            return true;
        } else {
            LOGGER.error(getName() + " Bee Flower Check Failed! Please check JSON!\nCurrent value: " + getFlower() + " is not a valid flower");
            return false;
        }
    }

    private boolean validateColor() {
        if (Color.validate(getColor())){
            LOGGER.debug(getName() + " Bee Color Check Passed!");
            return true;
        } else {
            LOGGER.error(getName() + " Bee Color Check Failed! Please check JSON!\nCurrent value: " + getColor() + " is not a valid color");
            return false;
        }
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
     * Returns new Resource Location with given input.
     *
     * @param resource Resource input as String in the form of "mod_id:item_or_block_id".
     * @return Returns New Resource Location for given input.
     */
    public ResourceLocation getResource(String resource){
        return new ResourceLocation(resource);
    }
}
