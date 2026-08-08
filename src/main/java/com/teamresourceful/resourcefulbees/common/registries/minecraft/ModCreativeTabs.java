package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeModeTab;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModCreativeTabs {

    public static final ResourcefulRegistry<CreativeModeTab> CREATIVE_TABS = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, ResourcefulBees.MODID);

    public static final RegistryEntry<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main_tab", () -> CreativeModeTab.builder()

                .icon(() -> ModItems.HONEY_DIPPER.get().getDefaultInstance())
                .title(Component.translatable("itemGroup.resourcefulbees"))
                .displayItems((parameters, output) -> {
                if (GeneralConfig.enableDevBees) output.accept(ModItems.OREO_COOKIE.get());
                if (GeneralConfig.enableDevBees) output.accept(ModItems.STRAWBEERRY_MILKSHAKE.get());
                output.accept(ModItems.BEE_JAR.get());
                output.accept(ModItems.BEE_BOX.get());
                output.accept(ModItems.BEE_BOX_TEMP.get());
                output.accept(ModItems.HONEY_DIPPER.get());
                output.accept(ModItems.SCRAPER.get());
                output.accept(ModItems.SMOKER.get());
                output.accept(ModItems.BELLOW.get());
                output.accept(ModItems.SMOKER_CAN.get());

                output.accept(ModItems.WAX.get());
                output.accept(ModItems.WAX_BLOCK_ITEM.get());
                output.accept(ModItems.GOLD_FLOWER_ITEM.get());

                output.accept(ModItems.FLOW_HIVE.get());
                output.accept(ModItems.BREEDER_ITEM.get());
                output.accept(ModItems.T1_APIARY_ITEM.get());
                output.accept(ModItems.T2_APIARY_ITEM.get());
                output.accept(ModItems.T3_APIARY_ITEM.get());
                output.accept(ModItems.T4_APIARY_ITEM.get());

//                output.accept(ModItems.HONEY_GENERATOR_ITEM.get());
//                output.accept(ModItems.ENDER_BEECON_ITEM.get());
//                output.accept(ModItems.SOLIDIFICATION_CHAMBER_ITEM.get());
//                output.accept(ModItems.HONEY_POT_ITEM.get());
                output.accept(ModItems.CENTRIFUGE_CRANK.get());
                output.accept(ModItems.CENTRIFUGE.get());
                output.accept(ModItems.WAXED_MACHINE_BLOCK.get());
//                output.accept(ModItems.FAKE_FLOWER.get());

                output.accept(ModItems.ENERGY_CAP_UPGRADE.get());
                output.accept(ModItems.ENERGY_XFER_UPGRADE.get());
                output.accept(ModItems.ENERGY_FILL_UPGRADE.get());
                output.accept(ModItems.HONEY_CAP_UPGRADE.get());
                output.accept(ModItems.BREED_TIME_UPGRADE.get());
                output.accept(ModItems.T2_NEST_UPGRADE.get());
                output.accept(ModItems.T3_NEST_UPGRADE.get());
                output.accept(ModItems.T4_NEST_UPGRADE.get());

                output.accept(ModItems.HONEY_GLASS.get());
                output.accept(ModItems.HONEY_GLASS_PLAYER.get());
                output.accept(ModItems.TRIMMED_WAXED_PLANKS.get());
                output.accept(ModItems.WAXED_PLANKS.get());
                output.accept(ModItems.WAXED_STAIRS.get());
                output.accept(ModItems.WAXED_SLAB.get());
                output.accept(ModItems.WAXED_FENCE.get());
                output.accept(ModItems.WAXED_FENCE_GATE.get());
                output.accept(ModItems.WAXED_BUTTON.get());
                output.accept(ModItems.WAXED_PRESSURE_PLATE.get());
                output.accept(ModItems.WAXED_DOOR.get());
                output.accept(ModItems.WAXED_TRAPDOOR.get());
                output.accept(ModItems.WAXED_SIGN.get());
                output.accept(ModItems.WAXED_HANGING_SIGN.get());
            }).build()
    );

    public static final RegistryEntry<CreativeModeTab> RESOURCEFUL_BEES_HIVES = CREATIVE_TABS.register("hives", () -> CreativeModeTab.builder()
                    .icon(Items.BEEHIVE::getDefaultInstance)
                    .displayItems((parameters, output) ->
                            ModItems.NEST_ITEMS.getEntries().stream()
                                    .map(RegistryEntry::get)
                                    .sorted(Comparator.comparing(item ->
                                            BuiltInRegistries.ITEM.getKey(item).toString()
                                    ))
                                    .forEach(output::accept)
                    )
                    .build()
            );

    public static final RegistryEntry<CreativeModeTab> RESOURCEFUL_BEES_HONEY =
            CREATIVE_TABS.register("honey", () ->
                    new ResourcefulCreativeModeTab(ModIdentifier.of("honey"))
                            .setItemIcon(() -> Items.HONEY_BOTTLE)
                            .addContent(() ->
                                    Stream.concat(
                                                    Stream.concat(
                                                            ModItems.HONEY_BOTTLE_ITEMS.boundStream(),
                                                            ModItems.HONEY_BLOCK_ITEMS.boundStream()
                                                    ),
                                                    ModItems.HONEY_BUCKET_ITEMS.boundStream()
                                            )
                                            .sorted(Comparator.comparing(item ->
                                                    BuiltInRegistries.ITEM
                                                            .getKey(item.asItem())
                                                            .toString()
                                            ))
                                            .map(ItemStack::new)
                            )
                            //.addStack(() -> Items.HONEY_BOTTLE)
                            //.addStack(() -> Items.HONEY_BLOCK)
                            //.addStack(ModItems.HONEY_BUCKET::get)
                            .build()
            );

    public static final RegistryEntry<CreativeModeTab> RESOURCEFUL_BEES_COMBS =
            CREATIVE_TABS.register("combs", () ->
                    new ResourcefulCreativeModeTab(ModIdentifier.of("combs"))
                            .setItemIcon(() -> Items.HONEYCOMB)
                            .addContent(() ->
                                    Stream.concat(
                                                    ModItems.HONEYCOMB_ITEMS.boundStream(),
                                                    ModItems.HONEYCOMB_BLOCK_ITEMS.boundStream()
                                            )
                                            .sorted(Comparator.comparing(item ->
                                                    BuiltInRegistries.ITEM
                                                            .getKey(item.asItem())
                                                            .toString()
                                            ))
                                            .map(ItemStack::new)
                            )
                            .build()
            );

    public static final RegistryEntry<CreativeModeTab> RESOURCEFUL_BEES_BEES =
            CREATIVE_TABS.register("bees", () ->
                    new ResourcefulCreativeModeTab(ModIdentifier.of("bees"))
                            .setItemIcon(() -> Items.BEE_SPAWN_EGG)
                            .addContent(() ->
                                    ModItems.SPAWN_EGG_ITEMS.boundStream()
                                            .sorted(Comparator.comparing(item ->
                                                    BuiltInRegistries.ITEM
                                                            .getKey(item.asItem())
                                                            .toString()
                                            ))
                                            .map(ItemStack::new)
                            )
                            .build()
            );
}
