package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.platform.common.registry.creativetab.CreativeTabBuilder;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemGroupResourcefulBees {

    private ItemGroupResourcefulBees() {
        throw new UtilityClassError();
    }

    public static final CreativeModeTab RESOURCEFUL_BEES = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "resourcefulbees"))
            .setIcon(() -> new ItemStack(ModItems.HONEY_DIPPER.get()))
            .addRegistry(() -> ModItems.CENTRIFUGE_ITEMS)
            .setAddingFunction(list -> {
                list.add(ModItems.BEE_JAR);
                list.add(ModItems.OREO_COOKIE);
                list.add(ModItems.OREO_COOKIE);
                list.add(ModItems.BEE_BOX);
                list.add(ModItems.BEE_BOX_TEMP);
                ItemStack creativeBeepedia = new ItemStack(ModItems.BEEPEDIA.get());
                creativeBeepedia.getOrCreateTag().putBoolean(NBTConstants.Beepedia.CREATIVE, true);
                list.add(ModItems.BEEPEDIA);
                list.add(creativeBeepedia);
                list.add(ModItems.BEE_LOCATOR);
                list.add(ModItems.HONEY_DIPPER);
                list.add(ModItems.SCRAPER);
                list.add(ModItems.SMOKER);
                list.add(ModItems.BELLOW);
                list.add(ModItems.SMOKERCAN);
                list.add(ModItems.WAX);
                list.add(ModItems.WAX_BLOCK_ITEM);
                list.add(ModItems.GOLD_FLOWER_ITEM);

                list.add(ModItems.HONEY_GENERATOR_ITEM);
                list.add(ModItems.ENDER_BEECON_ITEM);
                list.add(ModItems.SOLIDIFICATION_CHAMBER_ITEM);
                list.add(ModItems.HONEY_POT_ITEM);
                list.add(ModItems.CENTRIFUGE_CRANK);
                list.add(ModItems.CENTRIFUGE);
                list.add(ModItems.WAXED_MACHINE_BLOCK);

                list.add(ModItems.BREED_TIME_UPGRADE);
                list.add(ModItems.T2_NEST_UPGRADE);
                list.add(ModItems.T3_NEST_UPGRADE);
                list.add(ModItems.T4_NEST_UPGRADE);

                list.add(ModItems.HONEY_GLASS);
                list.add(ModItems.HONEY_GLASS_PLAYER);
                list.add(ModItems.TRIMMED_WAXED_PLANKS);
                list.add(ModItems.WAXED_PLANKS);
                list.add(ModItems.WAXED_STAIRS);
                list.add(ModItems.WAXED_SLAB);
                list.add(ModItems.WAXED_FENCE);
                list.add(ModItems.WAXED_FENCE_GATE);
                list.add(ModItems.WAXED_BUTTON);
                list.add(ModItems.WAXED_PRESSURE_PLATE);
                list.add(ModItems.WAXED_DOOR);
                list.add(ModItems.WAXED_TRAPDOOR);
                list.add(ModItems.WAXED_SIGN);
            })
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_HIVES = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "hives"))
        .setIcon(() -> new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get()))
        .addRegistry(() -> ModItems.NEST_ITEMS)
        .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_HONEY = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "honey"))
            .setIcon(() -> new ItemStack(Items.HONEY_BOTTLE))
            .addRegistry(() -> ModItems.HONEY_BOTTLE_ITEMS)
            .addRegistry(() -> ModItems.HONEY_BLOCK_ITEMS)
            .addRegistry(() -> ModItems.HONEY_BUCKET_ITEMS)
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_COMBS = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "combs"))
            .setIcon(() -> new ItemStack(Items.HONEYCOMB))
            .addRegistry(() -> ModItems.HONEYCOMB_ITEMS)
            .addRegistry(() -> ModItems.HONEYCOMB_BLOCK_ITEMS)
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_BEES = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "bees"))
            .setIcon(() -> new ItemStack(Items.BEE_SPAWN_EGG))
            .addRegistry(() -> ModItems.SPAWN_EGG_ITEMS)
            .build();

    public static void register() {
        // NO-OP
    }
}
