package com.dungeonderps.resourcefulbees.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

public class BeeBuilderEntity extends BeeEntity {
    String name;
    ItemStack drop;
    Color beeColor, hiveColor;
    Block flower, baseBlock, mutBlock;

    public BeeBuilderEntity(String name, String drop, int[] beeColor, int[] hiveColor, String flower, String baseBlock, String mutBlock, EntityType<? extends BeeEntity> p_i225714_1_, World p_i225714_2_) {
        super(p_i225714_1_, p_i225714_2_);
        this.name = name;
        this.beeColor = new Color(beeColor[0], beeColor[1],beeColor[2]);
        this.hiveColor = new Color(hiveColor[0], hiveColor[1], hiveColor[2]);
        this.drop = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(drop)));
        this.baseBlock = Block.getBlockFromItem(ForgeRegistries.ITEMS.getValue(new ResourceLocation(baseBlock)));
        this.mutBlock = Block.getBlockFromItem(ForgeRegistries.ITEMS.getValue(new ResourceLocation(mutBlock)));
        // create checker for what they passed in for flower
        // single flower
        // tag
        // all
        // small only
        // tall only
        switch (flower) {
            case "all":

            case "small":

            case "tall":

            default:
                if (flower.charAt(0) == '#') {
                    // do something
                } else {
                    // do something
                }
        }
    }
    // CONSTANT FOR EVERY ENTITY

    // THESE CHANGE FOR EACH
}
