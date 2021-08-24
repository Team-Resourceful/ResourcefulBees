package com.teamresourceful.resourcefulbees.api.honeydata;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;

public class DefaultHoneyBottleData {

    public static final Item bottle = Items.HONEY_BOTTLE;
    public static Item blockItem = Items.HONEY_BLOCK;
    public static Block block = Blocks.HONEY_BLOCK;
    public static RegistryObject<FlowingFluid> flowingFluid = ModFluids.HONEY_FLOWING;
    public static RegistryObject<FlowingFluid> stillFluid = ModFluids.HONEY_STILL;
    public static RegistryObject<Item> bucket = ModItems.HONEY_FLUID_BUCKET;
}
