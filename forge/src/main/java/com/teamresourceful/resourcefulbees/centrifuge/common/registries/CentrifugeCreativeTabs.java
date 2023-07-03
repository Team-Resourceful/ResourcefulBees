package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.item.tabs.ResourcefulCreativeTab;
import net.minecraft.resources.ResourceLocation;

public class CentrifugeCreativeTabs {

    public static void register() {
        new ResourcefulCreativeTab(new ResourceLocation(ModConstants.MOD_ID, "centrifuge"))
                .setItemIcon(CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL)
                .addRegistry(CentrifugeItems.CENTRIFUGE_ITEMS)
                .build();
    }
}
