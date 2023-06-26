package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() {
        throw new UtilityClassError();
    }

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "resourcefulbees"))
            .setItemIcon(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_DIPPER)
            .addRegistry(CentrifugeItems.CENTRIFUGE_ITEMS)
            .addContent(() -> {
                List<ItemStack> list = new ArrayList<>();
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_JAR.get()));
                if (GeneralConfig.enableDevBees) list.add(new ItemStack(ModItems.OREO_COOKIE.get()));
                if (GeneralConfig.enableSupporterBees) list.add(new ItemStack(ModItems.STRAWBEERRY_MILKSHAKE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_BOX.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_BOX_TEMP.get()));
                ItemStack creativeBeepedia = new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEEPEDIA.get());
                creativeBeepedia.getOrCreateTag().putBoolean(NBTConstants.Beepedia.CREATIVE, true);
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEEPEDIA.get()));
                list.add(creativeBeepedia);
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BEE_LOCATOR.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_DIPPER.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SCRAPER.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SMOKER.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BELLOW.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SMOKERCAN.get()));
                list.add(new ItemStack(ModItems.WAX.get()));
                list.add(new ItemStack(ModItems.WAX_BLOCK_ITEM.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.GOLD_FLOWER_ITEM.get()));

                list.add(new ItemStack(ModItems.FLOW_HIVE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BREEDER_ITEM.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T1_APIARY_ITEM.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_APIARY_ITEM.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T3_APIARY_ITEM.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T4_APIARY_ITEM.get()));

                list.add(new ItemStack(ModItems.HONEY_GENERATOR_ITEM.get()));
                list.add(new ItemStack(ModItems.ENDER_BEECON_ITEM.get()));
                list.add(new ItemStack(ModItems.SOLIDIFICATION_CHAMBER_ITEM.get()));
                list.add(new ItemStack(ModItems.HONEY_POT_ITEM.get()));
                list.add(new ItemStack(ModItems.CENTRIFUGE_CRANK.get()));
                list.add(new ItemStack(ModItems.CENTRIFUGE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_MACHINE_BLOCK.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.FAKE_FLOWER.get()));

                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ENERGY_CAP_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ENERGY_XFER_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.ENERGY_FILL_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_CAP_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.BREED_TIME_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T2_NEST_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T3_NEST_UPGRADE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.T4_NEST_UPGRADE.get()));

                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_GLASS.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_GLASS_PLAYER.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.TRIMMED_WAXED_PLANKS.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_PLANKS.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_STAIRS.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_SLAB.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_FENCE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_FENCE_GATE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_BUTTON.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_PRESSURE_PLATE.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_DOOR.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_TRAPDOOR.get()));
                list.add(new ItemStack(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.WAXED_SIGN.get()));
                return list.stream();
            })
            .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_HIVES = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "hives"))
        .setItemIcon(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.OAK_BEE_NEST_ITEM)
        .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.NEST_ITEMS)
        .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_HONEY = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "honey"))
            .setItemIcon(() -> Items.HONEY_BOTTLE)
            .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BOTTLE_ITEMS)
            .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BLOCK_ITEMS)
            .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEY_BUCKET_ITEMS)
            .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_COMBS = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "combs"))
            .setItemIcon(() -> Items.HONEYCOMB)
            .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEYCOMB_ITEMS)
            .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.HONEYCOMB_BLOCK_ITEMS)
            .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_BEES = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "bees"))
            .setItemIcon(() -> Items.BEE_SPAWN_EGG)
            .addRegistry(com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems.SPAWN_EGG_ITEMS)
            .build();

    public static void register() {
        // NO-OP
    }
}
