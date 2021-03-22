package com.resourcefulbees.resourcefulbees.api.honeydata;

import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fml.RegistryObject;

public class DefaultHoneyBottleData extends HoneyBottleData {

    public Item bottle = Items.HONEY_BOTTLE;
    public Item blockItem = Items.HONEY_BLOCK;
    public Block block = Blocks.HONEY_BLOCK;
    public RegistryObject<FlowingFluid> flowingFluid = ModFluids.HONEY_FLOWING;
    public RegistryObject<FlowingFluid> stillFluid = ModFluids.HONEY_STILL;
    public RegistryObject<Item> bucket = ModItems.HONEY_FLUID_BUCKET;

}
