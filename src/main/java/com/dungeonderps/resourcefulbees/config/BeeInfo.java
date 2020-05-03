package com.dungeonderps.resourcefulbees.config;

import com.google.common.base.Splitter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class BeeInfo {
    //These are needed for dynamic creation from JSON configs
    public static final LinkedHashMap<String, BeeInfo> BEE_INFO = new LinkedHashMap<>();
    public static final HashMap<Biome, ArrayList<String>> SPAWNABLE_BIOMES = new HashMap<>();

    //TODO Change biomeList to an ArrayList
    // - Add "Ender" field for Particle Effects usage.

    private String name, flower, color, biomeList, baseBlock, mutationBlock, centrifugeOutput;
    private boolean spawnInWorld;

    private transient boolean enabled;
    // color stuff
    private transient float[] rgb;
    private transient int col;
    private final transient String hexLength = "INVALID HEX COLOR: please check your color value and make sure it is a # followed by 6 numbers/letters";
    public BeeInfo() {
        // run checkers

        //TODO This stuff is probably not need any longer
        //store as different vals
        //Color c = Color.decode(color);
        //c.getRGBColorComponents(rgb); // should work if not rbg needs to be assigned to this
        //col = c.getRGB();

    }

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
        return flower;
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
     * NOT CURRENTLY USED
     * Returns whether this bee can naturally spawn in world.
     * @return can this bee spawn naturally in world?
     */
    public boolean isSpawnInWorld() {
        return spawnInWorld;
    }

    /**
     * NOT CURRENTLY USED
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

    /**
     * Gets the Centrifuge Output for this bee's honeycomb.
     * @return This bee's honeycomb centrifuge output.
     */
    public String getCentrifugeOutput() {
        return centrifugeOutput;
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
        return mutationBlock;
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
        return baseBlock;
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
     * NOT CURRENTLY USED
     * Gets the biome(s) this bee can spawn in.
     * @return this bee's spawnable biome(s).
     */
    public String getBiomeList() {
        return biomeList;
    }

    /**
     * NOT CURRENTLY USED
     * Sets the biome(s) this bee can spawn in.
     * Can be used to modify bees through code.
     * @param biomeList this bee's spawnable biome(s).
     */
    public void setBiomeList(String biomeList) {
        this.biomeList = biomeList;
    }

    public static void parseBiomeList(BeeInfo bee){
        if (bee.getBiomeList().toLowerCase().contains("tag:")){
            //list with parsed biome tags
            List<String> biomeList = Splitter.on(',').trimResults().splitToList(bee.getBiomeList().toLowerCase().replace("tag:",""));
            for(String type : biomeList){
                //creates set containing all biomes of given type
                Set<Biome> biomeSet = BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(type));
                updateSpawnableBiomes(biomeSet,bee);
            }

        } else {
            List<String> biomeList = Splitter.on(',').trimResults().splitToList(bee.getBiomeList().toLowerCase());
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
            BeeInfo.SPAWNABLE_BIOMES.computeIfAbsent(biome,k -> new ArrayList<>()).add(bee.getName());
        }
    }

    // MAY BE DEPRECATED UNLESS A NEED IS FOUND
    public float[] getRGB() {
        return rgb;
    }
    // MAY BE DEPRECATED UNLESS A NEED IS FOUND
    public int getCol() {
        return col;
    }


    //TODO Needs modification before implementation.
    // - Doubt '#' has any performance impact,
    // - in addition '0x' prefix may be used in lieu of '#'
    /* JSON Validation is not currently implemented
        Some checks may need modification.

    public void checkColor() {
        if (color.charAt(0) != '#') {
            if (color.length() != 6) {
                enabled = false;
                ResourcefulBees.LOGGER.error(hexLength);
            }
            ResourcefulBees.LOGGER.warn("MISSING #: please remember to put a # in front of your hex value to increase performance :)");
        } else if (color.length() != 7) {
            enabled = false;
            ResourcefulBees.LOGGER.error(hexLength);
        }
    }

    public void checkBiomes() {
        ResourcefulBees.LOGGER.info("Biomes currently not implemented");
    }

    public void checkDims() {
        ResourcefulBees.LOGGER.info("Dimensions currently not implemented");
    }

    public void checkBlocksNDrops() {
        if (ResourcefulBeesConfig.DEBUG_MODE.get()) {
            ResourcefulBees.LOGGER.info("Strict block and item checking not implemented, please don't be stupid");
            // check base here
            // check mut here
            // check drop here
        }
        // light checker: only checks syntax
        for (String i: new String[] {baseBlock, mutationBlock, centrifugeOutput}) {
            int x = i.indexOf(':');
            if (x == -1 || x == 0 || x != i.length() - 1) {
                ResourcefulBees.LOGGER.error("INVALID ID: should be format of \"modid:item_or_block_id\"");
                enabled = false;
            }
        } 
    }

     */

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
        String[] resourceSplit = resource.split(":");
        return new ResourceLocation(resourceSplit[0], resourceSplit[1]);
    }
}
