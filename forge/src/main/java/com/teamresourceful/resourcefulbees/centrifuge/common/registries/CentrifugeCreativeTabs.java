package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class CentrifugeCreativeTabs {

    public static final Supplier<CreativeModeTab> RESOURCEFUL_BEES_CENTRIFUGE = new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "centrifuge"))
            .setItemIcon(CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL)
            .addRegistry(CentrifugeItems.CENTRIFUGE_ITEMS)
            .build();

    public static void register() {
        //NO-OP
    }
}
