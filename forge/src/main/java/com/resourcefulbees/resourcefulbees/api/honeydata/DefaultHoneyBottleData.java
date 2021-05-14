package com.resourcefulbees.resourcefulbees.api.honeydata;

import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;

public class DefaultHoneyBottleData {

    public static final Item bottle = Items.HONEY_BOTTLE;
    public static Item blockItem = Items.HONEY_BLOCK;
    public static Block block = Blocks.HONEY_BLOCK;
    public static RegistryObject<FlowingFluid> flowingFluid = ModFluids.HONEY_FLOWING;
    public static RegistryObject<FlowingFluid> stillFluid = ModFluids.HONEY_STILL;
    public static RegistryObject<Item> bucket = ModItems.HONEY_FLUID_BUCKET;
}
