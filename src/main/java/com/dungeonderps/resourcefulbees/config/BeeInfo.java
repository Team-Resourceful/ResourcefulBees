package com.dungeonderps.resourcefulbees.config;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BeeInfo {
    private String name, flower, color, biomeList, baseBlock, mutBlock, drop;
    private boolean spawnInWorld;
    private int[] dimensionList;

    private transient boolean enabled;
    // color stuff
    private transient float[] rgb;
    private transient int col;
    private transient String hexLength = "INVALID HEX COLOR: please check your color value and make sure it is a # followed by 6 numbers/letters";
    public BeeInfo() {
        // run checkers
        // store as different vals
        //Color c = Color.decode(color);
        //c.getRGBColorComponents(rgb); // should work if not rbg needs to be assigned to this
        //col = c.getRGB();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public int[] getDimensionList() {
        return this.dimensionList;
    }

    public void setDimensionList(int[] dimensionList) {
        this.dimensionList = dimensionList;
    }

    public boolean isSpawnInWorld() {
        return spawnInWorld;
    }

    public void setSpawnInWorld(boolean spawnInWorld) {
        this.spawnInWorld = spawnInWorld;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getMutBlock() {
        return mutBlock;
    }

    public void setMutBlock(String mutBlock) {
        this.mutBlock = mutBlock;
    }

    public String getBaseBlock() {
        return baseBlock;
    }

    public void setBaseBlock(String baseBlock) {
        this.baseBlock = baseBlock;
    }

    public String getBiomeList() {
        return biomeList;
    }

    public void setBiomeList(String biomeList) {
        this.biomeList = biomeList;
    }
    
    public float[] getRGB() {
        return rgb;
    }
    
    public int getCol() {
        return col;
    }

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
        for (String i: new String[] {baseBlock, mutBlock, drop}) {
            int x = i.indexOf(':');
            if (x == -1 || x == 0 || x != i.length() - 1) {
                ResourcefulBees.LOGGER.error("INVALID ID: should be format of \"modid:itemorblockid\"");
                enabled = false;
            }
        } 
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> s = new ArrayList<>();
        String i = spawnInWorld ? "true" : "false";
        s.add(color);
        s.add(flower);
        s.add(baseBlock);
        s.add(mutBlock);
        s.add(drop);
        s.add(i);
        s.add(dimensionList.toString());
        s.add(biomeList);
        return s;
    }

    public ResourceLocation getResource(String resource){
        String[] resourceSplit = resource.split(":");
        return new ResourceLocation(resourceSplit[0], resourceSplit[1]);
    }
}
