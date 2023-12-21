package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class CentrifugeItems {

    public static final ResourcefulRegistry<Item> CENTRIFUGE_ITEMS = ResourcefulRegistries.create(ModItems.ITEMS);

    //region centrifuge items
    public static final RegistryEntry<Item> CENTRIFUGE_CASING = CENTRIFUGE_ITEMS.register("centrifuge/casing", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_CASING.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_PROCESSOR = CENTRIFUGE_ITEMS.register("centrifuge/processor", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_PROCESSOR.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_GEARBOX = CENTRIFUGE_ITEMS.register("centrifuge/gearbox", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_GEARBOX.get(), new Item.Properties()));

    //TERMINAL
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/basic", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_BASIC_TERMINAL.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/advanced", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ADVANCED_TERMINAL.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/elite", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ELITE_TERMINAL.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_TERMINAL = CENTRIFUGE_ITEMS.register("centrifuge/terminal/ultimate", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ULTIMATE_TERMINAL.get(), new Item.Properties()));

    //VOID
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/basic", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_BASIC_VOID.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/advanced", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ADVANCED_VOID.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/elite", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ELITE_VOID.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_VOID = CENTRIFUGE_ITEMS.register("centrifuge/void/ultimate", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ULTIMATE_VOID.get(), new Item.Properties()));

    //INPUT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/basic", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_BASIC_INPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/advanced", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ADVANCED_INPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/elite", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ELITE_INPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_INPUT = CENTRIFUGE_ITEMS.register("centrifuge/input/item/ultimate", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ULTIMATE_INPUT.get(), new Item.Properties()));

    //ENERGY PORT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/basic", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_BASIC_ENERGY_PORT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/advanced", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ADVANCED_ENERGY_PORT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/elite", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ELITE_ENERGY_PORT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_ENERGY_PORT = CENTRIFUGE_ITEMS.register("centrifuge/input/energy/ultimate", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get(), new Item.Properties()));

    //ITEM OUTPUT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/basic", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_BASIC_ITEM_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/advanced", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/elite", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ELITE_ITEM_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_ITEM_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/item/ultimate", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get(), new Item.Properties()));

    //FLUID OUTPUT
    public static final RegistryEntry<Item> CENTRIFUGE_BASIC_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/basic", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_BASIC_FLUID_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ADVANCED_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/advanced", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ELITE_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/elite", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ELITE_FLUID_OUTPUT.get(), new Item.Properties()));
    public static final RegistryEntry<Item> CENTRIFUGE_ULTIMATE_FLUID_OUTPUT = CENTRIFUGE_ITEMS.register("centrifuge/output/fluid/ultimate", () -> new BlockItem(CentrifugeBlocks.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get(), new Item.Properties()));
    //endregion
    
    private CentrifugeItems() throws UtilityClassException {
        throw new UtilityClassException();
    }
}
