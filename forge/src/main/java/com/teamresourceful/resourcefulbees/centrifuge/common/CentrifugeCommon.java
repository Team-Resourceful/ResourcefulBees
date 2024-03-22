package com.teamresourceful.resourcefulbees.centrifuge.common;

import com.teamresourceful.resourcefulbees.centrifuge.common.network.CentrifugeNetworkHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ModConstants.MOD_ID)
public class CentrifugeCommon {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CentrifugeNetworkHandler.init();
    }
}
