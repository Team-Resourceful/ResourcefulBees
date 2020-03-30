package com.dungeonderps.resourcefulbees;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ResourcefulBees.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, ResourcefulBees.MOD_ID);
	
	
	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<Item> BEEWAX = ITEMS.register("bee_wax", () -> new Item(new Item.Properties().group(ItemGroup.DECORATIONS)));
	//TODO Change hive block to use custom give block class for bee hive implementation.
	public static final RegistryObject<Block> INDUSTRIALHIVE = BLOCKS.register("industrial_hive", () -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0F, 3.0F)));
	public static final RegistryObject<Item> INDUSTRIALHIVEITEM = ITEMS.register("industrial_hive",  () -> new BlockItem(INDUSTRIALHIVE.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
}