package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "resourcefulbees"))
            .setItemIcon(ModItems.HONEY_DIPPER)
            .addContent(() -> {
                List<ItemStack> list = new ArrayList<>();
                list.add(new ItemStack(ModItems.BEE_JAR.get()));
                if (GeneralConfig.enableDevBees) list.add(new ItemStack(ModItems.OREO_COOKIE.get()));
                if (GeneralConfig.enableSupporterBees) list.add(new ItemStack(ModItems.STRAWBEERRY_MILKSHAKE.get()));
                list.add(new ItemStack(ModItems.BEE_BOX.get()));
                list.add(new ItemStack(ModItems.BEE_BOX_TEMP.get()));
                ItemStack creativeBeepedia = new ItemStack(ModItems.BEEPEDIA.get());
                creativeBeepedia.getOrCreateTag().putBoolean(NBTConstants.Beepedia.CREATIVE, true);
                list.add(new ItemStack(ModItems.BEEPEDIA.get()));
                list.add(creativeBeepedia);
                list.add(new ItemStack(ModItems.BEE_LOCATOR.get()));
                list.add(new ItemStack(ModItems.HONEY_DIPPER.get()));
                list.add(new ItemStack(ModItems.SCRAPER.get()));
                list.add(new ItemStack(ModItems.SMOKER.get()));
                list.add(new ItemStack(ModItems.BELLOW.get()));
                list.add(new ItemStack(ModItems.SMOKERCAN.get()));
                list.add(new ItemStack(ModItems.WAX.get()));
                list.add(new ItemStack(ModItems.WAX_BLOCK_ITEM.get()));
                list.add(new ItemStack(ModItems.GOLD_FLOWER_ITEM.get()));

                list.add(new ItemStack(ModItems.FLOW_HIVE.get()));
                list.add(new ItemStack(ModItems.BREEDER_ITEM.get()));
                list.add(new ItemStack(ModItems.T1_APIARY_ITEM.get()));
                list.add(new ItemStack(ModItems.T2_APIARY_ITEM.get()));
                list.add(new ItemStack(ModItems.T3_APIARY_ITEM.get()));
                list.add(new ItemStack(ModItems.T4_APIARY_ITEM.get()));

                list.add(new ItemStack(ModItems.HONEY_GENERATOR_ITEM.get()));
                list.add(new ItemStack(ModItems.ENDER_BEECON_ITEM.get()));
                list.add(new ItemStack(ModItems.SOLIDIFICATION_CHAMBER_ITEM.get()));
                list.add(new ItemStack(ModItems.HONEY_POT_ITEM.get()));
                list.add(new ItemStack(ModItems.CENTRIFUGE_CRANK.get()));
                list.add(new ItemStack(ModItems.CENTRIFUGE.get()));
                list.add(new ItemStack(ModItems.WAXED_MACHINE_BLOCK.get()));
                list.add(new ItemStack(ModItems.FAKE_FLOWER.get()));

                list.add(new ItemStack(ModItems.ENERGY_CAP_UPGRADE.get()));
                list.add(new ItemStack(ModItems.ENERGY_XFER_UPGRADE.get()));
                list.add(new ItemStack(ModItems.ENERGY_FILL_UPGRADE.get()));
                list.add(new ItemStack(ModItems.HONEY_CAP_UPGRADE.get()));
                list.add(new ItemStack(ModItems.BREED_TIME_UPGRADE.get()));
                list.add(new ItemStack(ModItems.T2_NEST_UPGRADE.get()));
                list.add(new ItemStack(ModItems.T3_NEST_UPGRADE.get()));
                list.add(new ItemStack(ModItems.T4_NEST_UPGRADE.get()));

                list.add(new ItemStack(ModItems.HONEY_GLASS.get()));
                list.add(new ItemStack(ModItems.HONEY_GLASS_PLAYER.get()));
                list.add(new ItemStack(ModItems.TRIMMED_WAXED_PLANKS.get()));
                list.add(new ItemStack(ModItems.WAXED_PLANKS.get()));
                list.add(new ItemStack(ModItems.WAXED_STAIRS.get()));
                list.add(new ItemStack(ModItems.WAXED_SLAB.get()));
                list.add(new ItemStack(ModItems.WAXED_FENCE.get()));
                list.add(new ItemStack(ModItems.WAXED_FENCE_GATE.get()));
                list.add(new ItemStack(ModItems.WAXED_BUTTON.get()));
                list.add(new ItemStack(ModItems.WAXED_PRESSURE_PLATE.get()));
                list.add(new ItemStack(ModItems.WAXED_DOOR.get()));
                list.add(new ItemStack(ModItems.WAXED_TRAPDOOR.get()));
                list.add(new ItemStack(ModItems.WAXED_SIGN.get()));
                list.add(new ItemStack(ModItems.WAXED_HANGING_SIGN.get()));
                return list.stream();
            })
            .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_HIVES = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "hives"))
        .setItemIcon(ModItems.OAK_BEE_NEST_ITEM)
        .addRegistry(ModItems.NEST_ITEMS)
        .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_HONEY = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "honey"))
            .setItemIcon(() -> Items.HONEY_BOTTLE)
            .addRegistry(ModItems.HONEY_BOTTLE_ITEMS)
            .addRegistry(ModItems.HONEY_BLOCK_ITEMS)
            .addRegistry(ModItems.HONEY_BUCKET_ITEMS)
            .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_COMBS = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "combs"))
            .setItemIcon(() -> Items.HONEYCOMB)
            .addRegistry(ModItems.HONEYCOMB_ITEMS)
            .addRegistry(ModItems.HONEYCOMB_BLOCK_ITEMS)
            .build();

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_BEES = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "bees"))
            .setItemIcon(() -> Items.BEE_SPAWN_EGG)
            .addRegistry(ModItems.SPAWN_EGG_ITEMS)
            .build();

    public static void register() {
        // NO-OP
    }
}
