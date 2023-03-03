package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.creativetab.CreativeTabBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() {
        throw new UtilityClassError();
    }

    public static final CreativeModeTab RESOURCEFUL_BEES = CreativeTabBuilder.of(new ResourceLocation(ModConstants.MOD_ID, "resourcefulbees"))
            .setIcon(() -> new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_DIPPER.get()))
            .addRegistry(() -> CentrifugeItems.CENTRIFUGE_ITEMS)
            .setAddingFunction(list -> {
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_JAR);
                if (GeneralConfig.enableDevBees) {
                    list.add(ModItems.OREO_COOKIE);
                }
                if (GeneralConfig.enableSupporterBees) {
                    list.add(ModItems.STRAWBEERRY_MILKSHAKE);
                }
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_BOX);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_BOX_TEMP);
                ItemStack creativeBeepedia = new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEEPEDIA.get());
                creativeBeepedia.getOrCreateTag().putBoolean(NBTConstants.Beepedia.CREATIVE, true);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEEPEDIA);
                list.add(creativeBeepedia);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_LOCATOR);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_DIPPER);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SCRAPER);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SMOKER);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BELLOW);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SMOKERCAN);
                list.add(ModItems.WAX);
                list.add(ModItems.WAX_BLOCK_ITEM);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.GOLD_FLOWER_ITEM);

                list.add(ModItems.FLOW_HIVE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BREEDER_ITEM);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T1_APIARY_ITEM);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_APIARY_ITEM);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T3_APIARY_ITEM);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T4_APIARY_ITEM);

                list.add(ModItems.HONEY_GENERATOR_ITEM);
                list.add(ModItems.ENDER_BEECON_ITEM);
                list.add(ModItems.SOLIDIFICATION_CHAMBER_ITEM);
                list.add(ModItems.HONEY_POT_ITEM);
                list.add(ModItems.CENTRIFUGE_CRANK);
                list.add(ModItems.CENTRIFUGE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_MACHINE_BLOCK);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.FAKE_FLOWER);

                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ENERGY_CAP_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ENERGY_XFER_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ENERGY_FILL_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_CAP_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BREED_TIME_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_NEST_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T3_NEST_UPGRADE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T4_NEST_UPGRADE);

                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_GLASS);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_GLASS_PLAYER);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.TRIMMED_WAXED_PLANKS);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_PLANKS);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_STAIRS);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_SLAB);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_FENCE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_FENCE_GATE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_BUTTON);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_PRESSURE_PLATE);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_DOOR);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_TRAPDOOR);
                list.add(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_SIGN);
            })
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_HIVES = CreativeTabBuilder.of(new ResourceLocation(ModConstants.MOD_ID, "hives"))
        .setIcon(() -> new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.OAK_BEE_NEST_ITEM.get()))
        .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.NEST_ITEMS)
        .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_HONEY = CreativeTabBuilder.of(new ResourceLocation(ModConstants.MOD_ID, "honey"))
            .setIcon(() -> new ItemStack(Items.HONEY_BOTTLE))
            .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BOTTLE_ITEMS)
            .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BLOCK_ITEMS)
            .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BUCKET_ITEMS)
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_COMBS = CreativeTabBuilder.of(new ResourceLocation(ModConstants.MOD_ID, "combs"))
            .setIcon(() -> new ItemStack(Items.HONEYCOMB))
            .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEYCOMB_ITEMS)
            .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEYCOMB_BLOCK_ITEMS)
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_BEES = CreativeTabBuilder.of(new ResourceLocation(ModConstants.MOD_ID, "bees"))
            .setIcon(() -> new ItemStack(Items.BEE_SPAWN_EGG))
            .addRegistry(() -> com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SPAWN_EGG_ITEMS)
            .build();

    public static void register() {
        // NO-OP
    }
}
