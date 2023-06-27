package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.common.item.centrifuge.CrankItem;
import com.teamresourceful.resourcefulbees.common.item.centrifuge.ManualCentrifugeItem;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@SuppressWarnings("unused")
public final class ModItems {

    private ModItems() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ITEMS);

    public static final RegistryEntry<Item> HONEY_GENERATOR_ITEM = ITEMS.register("honey_generator", () -> new BlockItem(ModBlocks.HONEY_GENERATOR.get(), new Item.Properties()));

    public static final RegistryEntry<Item> FLOW_HIVE = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.NEST_ITEMS.register("flow_hive", () -> new BlockItem(ModBlocks.FLOW_HIVE.get(), new Item.Properties()));
    public static final RegistryEntry<Item> ENDER_BEECON_ITEM = ITEMS.register("ender_beecon", () -> new BlockItem(ModBlocks.ENDER_BEECON.get(), new Item.Properties()));
    public static final RegistryEntry<Item> SOLIDIFICATION_CHAMBER_ITEM = ITEMS.register("solidification_chamber", () -> new BlockItem(ModBlocks.SOLIDIFICATION_CHAMBER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> HONEY_POT_ITEM = ITEMS.register("honey_pot", () -> new BlockItem(ModBlocks.HONEY_POT.get(), new Item.Properties()));

    public static final RegistryEntry<Item> HONEY_FLUID_BUCKET = com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BUCKET_ITEMS.register("honey_bucket", () -> new BucketItem(ModFluids.HONEY_STILL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryEntry<Item> CENTRIFUGE_CRANK = ITEMS.register("centrifuge_crank", () -> new CrankItem(ModBlocks.CENTRIFUGE_CRANK.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE = ITEMS.register("centrifuge", () -> new ManualCentrifugeItem(ModBlocks.BASIC_CENTRIFUGE.get(), new Item.Properties()));
}
