package com.resourcefulbees.resourcefulbees.api.honeydata;

import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;

public class DefaultHoneyBottleData extends HoneyBottleData {

    public Item bottle = Items.HONEY_BOTTLE;
    public Item blockItem = Items.HONEY_BLOCK;
    public Block block = Blocks.HONEY_BLOCK;
    public RegistryObject<FlowingFluid> flowingFluid = ModFluids.HONEY_FLOWING;
    public RegistryObject<FlowingFluid> stillFluid = ModFluids.HONEY_STILL;
    public RegistryObject<Item> bucket = ModItems.HONEY_FLUID_BUCKET;

}
