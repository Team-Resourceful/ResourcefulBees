package com.teamresourceful.resourcefulbees.common.registry.minecraft;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.creativetab.CreativeTabBuilder;
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
        .setListingFunction((item, items) -> {
            if (item.equals(ModItems.BEEPEDIA.get())) {
                ItemStack creativeBeepedia = new ItemStack(ModItems.BEEPEDIA.get());
                creativeBeepedia.getOrCreateTag().putBoolean(NBTConstants.Beepedia.CREATIVE, true);
                items.add(creativeBeepedia);
            }
        })
        .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_HIVES = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "hives"))
        .setIcon(() -> new ItemStack(ModItems.OAK_BEE_NEST_ITEM.get()))
        .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_HONEY = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "honey"))
            .setIcon(() -> new ItemStack(Items.HONEY_BOTTLE))
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_COMBS = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "combs"))
            .setIcon(() -> new ItemStack(Items.HONEYCOMB))
            .build();

    public static final CreativeModeTab RESOURCEFUL_BEES_BEES = CreativeTabBuilder.of(new ResourceLocation(ResourcefulBees.MOD_ID, "bees"))
            .setIcon(() -> new ItemStack(Items.BEE_SPAWN_EGG))
            .build();
}
