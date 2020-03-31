package com.dungeonderps.resourcefulbees.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.awt.*;

public class BeeBuilderEntity extends BeeEntity {
    String name;
    Item drop;
    Color beeColor, hiveColor;
    Block flower, baseBlock, mutBlock;

    public BeeBuilderEntity(String name, String drop, int[] beeColor, int[] hiveColor, String flower, String baseBlock, String mutBlock, EntityType<? extends BeeEntity> p_i225714_1_, World p_i225714_2_) {
        super(p_i225714_1_, p_i225714_2_);
        this.name = name;
        this.beeColor = new Color(beeColor[0], beeColor[1],beeColor[2]);
        this.hiveColor = new Color(hiveColor[0], hiveColor[1], hiveColor[2]);
        this.drop = new Item();
        // create checker for what they passed in for flower
    }
    // CONSTANT FOR EVERY ENTITY

    // THESE CHANGE FOR EACH
}
